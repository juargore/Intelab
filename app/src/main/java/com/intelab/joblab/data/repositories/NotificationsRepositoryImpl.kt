package com.intelab.joblab.data.repositories

import com.intelab.joblab.data.apis.NotificationsApi
import com.intelab.joblab.data.common.utils.ErrorGenerator
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.NotificationRequest
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import com.intelab.joblab.domain.usecases.notification.INotificationRepository
import kotlinx.coroutines.flow.flow

class NotificationsRepositoryImpl constructor(
    private val notificationApi: NotificationsApi,
    private val errorGenerator: ErrorGenerator
) : INotificationRepository {

    override suspend fun getDeviceInfoForNotification(deviceId: String, uuid: String) =
        flow {
            val response = notificationApi.getDeviceInfoForNotification(
                deviceId = deviceId,
                uuid = uuid
            )
            if (response.isSuccessful) {
                if (response.body()!!.result.isNotEmpty()) {
                    emit(BaseResult.Success(response.body()!!.result[0]))
                }
            } else {
                errorGenerator.validateError(response)?.let { emit(it) }
            }
        }

    override suspend fun sendDeviceInfoForNotification(notificationRequest: NotificationRequest) =
        flow {
            val response = notificationApi.sendDeviceInfoForNotification(notificationRequest)
            if (response.isSuccessful) {
                emit(BaseResult.Success(SuccessGenericResponse(true)))
            } else {
                errorGenerator.validateError(response)?.let { emit(it) }
            }
        }

    override suspend fun updateDeviceInfoForNotification(notificationRequest: NotificationRequest) =
        flow {
            val response = notificationApi.updateDeviceInfoForNotification(
                notificationRequest = notificationRequest,
                deviceId = notificationRequest.deviceId
            )
            if (response.isSuccessful) {
                emit(BaseResult.Success(SuccessGenericResponse(true)))
            } else {
                errorGenerator.validateError(response)?.let { emit(it) }
            }
        }
}
