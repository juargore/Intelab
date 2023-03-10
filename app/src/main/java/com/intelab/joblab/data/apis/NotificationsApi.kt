package com.intelab.joblab.data.apis

import com.intelab.joblab.data.common.module.DEVICE_ID
import com.intelab.joblab.data.common.module.UUID
import com.intelab.joblab.domain.entities.NotificationRequest
import com.intelab.joblab.domain.entities.NotificationResult
import retrofit2.Response
import retrofit2.http.*

interface NotificationsApi {

    @POST("users/devices")
    @Headers("Set-Token: true")
    suspend fun sendDeviceInfoForNotification(
        @Body notificationRequest: NotificationRequest
    ): Response<Unit>


    @PATCH("users/devices")
    @Headers("Set-Token: true")
    suspend fun updateDeviceInfoForNotification(
        @Body notificationRequest: NotificationRequest,
        @Header(DEVICE_ID) deviceId : String
    ): Response<Unit>


    @GET("users/devices")
    @Headers("Set-Token: true")
    suspend fun getDeviceInfoForNotification(
        @Header(DEVICE_ID) deviceId : String,
        @Header(UUID) uuid : String
    ): Response<NotificationResult>

}
