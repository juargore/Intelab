package com.intelab.joblab.domain.usecases.notification

import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.NotificationRequest
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import kotlinx.coroutines.flow.Flow

interface INotificationRepository {

    suspend fun getDeviceInfoForNotification(deviceId: String, uuid: String):
            Flow<BaseResult<NotificationRequest, ErrorGenericResponse>>

    suspend fun sendDeviceInfoForNotification(notificationRequest: NotificationRequest) :
            Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun updateDeviceInfoForNotification(notificationRequest: NotificationRequest) :
            Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>
}
