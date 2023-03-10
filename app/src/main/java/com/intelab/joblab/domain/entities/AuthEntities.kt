package com.intelab.joblab.domain.entities

data class TokenResponse(
    val tokenType: String,
    val accessToken: String,
    val refreshToken: String
)

data class SuccessGenericResponse(
    val success: Boolean = true
)

data class UserState(
    val profile: String,
    val type: String,
    val state: String,
)

data class GeneralResponseOneMessage(
    val message: String
)

data class PhotoResponse(
    val URL: String
)