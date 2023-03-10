package com.intelab.joblab.domain.usecases.auth

import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.entities.requests.LoginRequest
import com.intelab.joblab.domain.entities.requests.SignUpRequest
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {

    suspend fun userLogin(loginRequest: LoginRequest):
            Flow<BaseResult<TokenResponse, ErrorGenericResponse>>

    suspend fun userLogOut(): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun userSignUp(signUpRequest: SignUpRequest):
            Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun checkUserState(): Flow<BaseResult<UserState, ErrorGenericResponse>>

    suspend fun sendRecoveryCode(userEmail: String): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun compareAndVerifyRecoveryCode(verificationCode: String):
            Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun resetPassword(password: String, rePassword: String, verificationCode: String):
            Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun sendActivationCode(): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun compareAndVerifyActivationCode(verificationCode: String):
            Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun deleteAccountFromServer() : Flow<BaseResult<Boolean, ErrorGenericResponse>>
}
