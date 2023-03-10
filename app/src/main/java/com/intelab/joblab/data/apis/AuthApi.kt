package com.intelab.joblab.data.apis

import com.intelab.joblab.data.common.module.DEVICE_ID
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.TokenResponse
import com.intelab.joblab.domain.entities.UserState
import com.intelab.joblab.domain.entities.requests.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    // ------------------------ TOKEN SECTION
    @POST("users/access-token/refresh")
    suspend fun refreshToken(
        @Body refreshTokenRequest: RefreshTokenRequest
    ): Response<TokenResponse>

    // ------------------------ END TOKEN SECTION


    // ------------------------ SIGN IN | SIGN OUT | SIGN UP | STATE SECTION
    @POST("users/signin")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<TokenResponse>

    @POST("users/signout")
    suspend fun signOutUser(
        @Body signOutRequest: SignOutRequest,
        @Header(DEVICE_ID) deviceId: String
    ): Response<Unit>

    @POST("users/signup")
    suspend fun signUpUser(@Body signUpRequest: SignUpRequest): Response<Unit>

    @POST("users/state")
    suspend fun stateUser(@Body userStateRequest: UserStateRequest): Response<UserState>

    // ------------------------ END SIGN IN | SIGN OUT | SIGN UP SECTION


    // ------------------------ PASSWORD | CODE |  SECTION
    @POST("users/password/recovery-code")
    suspend fun sendRecoveryCode(@Body recoveryCodeRequest: RecoveryCodeRequest): Response<Unit>

    @POST("users/password/recovery-code/verify")
    suspend fun compareAndVerifyRecoveryCode(
        @Body compareAndVerifyRecoveryCodeRequest: CompareAndVerifyRecoveryCodeRequest
    ): Response<Unit>

    @POST("users/password/reset")
    suspend fun resetPassword(
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Response<Unit>

    @POST("users/activation-code")
    suspend fun sendActivationCode(@Body activationCodeRequest: ActivationCodeRequest): Response<Unit>

    @POST("users/activation-code/verify")
    suspend fun compareAndVerifyActivationCode(
        @Body verifyActivationCodeRequest: VerifyActivationCodeRequest
    ): Response<TokenResponse>

    // ------------------------ END PASSWORD | CODE |  SECTION

    @DELETE("users")
    suspend fun deleteUserAccount(): Response<ErrorGenericResponse>
}
