package com.intelab.joblab.domain.usecases.notification

import com.intelab.joblab.domain.entities.NotificationRequest
import javax.inject.Inject

class NotificationUseCase @Inject constructor(
    private val notificationRepository: INotificationRepository
) {

    @Suppress("unused")
    suspend fun getDeviceInfoForNotification(deviceId: String, uuid: String) =
        notificationRepository.getDeviceInfoForNotification(deviceId, uuid)

    suspend fun sendDeviceInfoForNotification(notificationRequest: NotificationRequest) =
        notificationRepository.sendDeviceInfoForNotification(notificationRequest)

    suspend fun updateDeviceInfoForNotification(notificationRequest: NotificationRequest) =
        notificationRepository.updateDeviceInfoForNotification(notificationRequest)
}
