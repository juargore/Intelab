package com.intelab.joblab.domain.entities

data class Notifications(
    var id: Int? = null,
    var header: String? = null,
    var notifications: MutableList<Notification> = mutableListOf()
)

data class NotificationsResponse(
    val results : List<NotificationResponse>? = listOf()
)

data class NotificationResponse(
    val id: Int? = null,
    val message: String? = null,
    val period: String? = null,
    val newMessage: Boolean? = null
)

data class Notification(
    val id: Int,
    val mainText: String,
    val complementaryText: String? = null,
    val isNew: Boolean? = null
)

data class TotalUnseenNotificationsRequest(
    val count: Int? = null
)
