package com.intelab.joblab.domain.entities

data class NotificationRequest(
    val model: String, // ej: s21
    val os: String, // ej: ANDROID
    val osVersion: String, // ej: 11
    val appVersion: String, // ej: 1.0.30
    val registrationToken: String,
    val deviceId: String, // ej: 12se1231ae2312
    val brand: String, // ej: Samsung
    val uuid: String? = null // not used on request but is value on NotificationResult
)

data class NotificationResult(
    val result: List<NotificationRequest>
)
