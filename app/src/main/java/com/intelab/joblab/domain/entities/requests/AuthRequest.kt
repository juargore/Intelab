package com.intelab.joblab.domain.entities.requests

data class LoginRequest(
    val username: String,
    val password: String
)

data class SignUpRequest(
    val username: String,
    val password: String,
    val rePassword: String
)

data class VerifyActivationCodeRequest(
    val username: String,
    val verificationCode: String
)

data class ActivationCodeRequest(val username: String)

data class ResetPasswordRequest(
    val username: String,
    val password: String,
    val rePassword: String,
    val verificationCode: String
)

data class RecoveryCodeRequest(
    val username: String
)

data class CompareAndVerifyRecoveryCodeRequest(
    val username: String,
    val verificationCode: String
)

data class UserStateRequest(val username: String)

data class RefreshTokenRequest(
    val username: String,
    val refreshToken: String
)

data class PreferableJobsRequest(
    val preferableJobs: List<PreferableJobs>
)

data class SignOutRequest(
    val username: String,
    val refreshToken: String
)
