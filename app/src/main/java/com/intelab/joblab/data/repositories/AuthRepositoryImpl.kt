package com.intelab.joblab.data.repositories

import com.intelab.joblab.data.apis.AuthApi
import com.intelab.joblab.data.common.utils.ErrorGenerator
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import com.intelab.joblab.domain.entities.requests.*
import com.intelab.joblab.domain.usecases.auth.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl constructor(
    private val authApi: AuthApi,
    private val sharedPrefs: SharedPrefs,
    private val errorGenerator: ErrorGenerator
) : IAuthRepository {
    
    override suspend fun userLogin(loginRequest: LoginRequest) = flow {
        val response = authApi.loginUser(loginRequest)
        if (response.isSuccessful) {
            with(response.body()!!) {
                sharedPrefs.saveAccessToken(accessToken)
                sharedPrefs.saveRefreshToken(refreshToken)
                sharedPrefs.saveUserEmail(loginRequest.username.trim())
            }
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun userLogOut() = flow {
        val userName = sharedPrefs.getUserEmail()
        val deviceId = sharedPrefs.getDeviceId()
        val response = authApi.signOutUser(
            deviceId = deviceId,
            signOutRequest = SignOutRequest(
                username = userName,
                refreshToken = sharedPrefs.getRefreshToken()
            )
        )
        if (response.isSuccessful) {
            sharedPrefs.saveFirebaseToken("")
            emit(BaseResult.Success(SuccessGenericResponse(true)))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun userSignUp(signUpRequest: SignUpRequest) = flow {
        val response = authApi.signUpUser(signUpRequest)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse(true)))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun checkUserState() = flow {
        val userEmail = sharedPrefs.getUserEmail()
        val response = authApi.stateUser(UserStateRequest(userEmail))
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendRecoveryCode(userEmail: String) = flow {
        val response = authApi.sendRecoveryCode(RecoveryCodeRequest(userEmail))
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse(true)))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun compareAndVerifyRecoveryCode(verificationCode: String) = flow {
        val userEmail = sharedPrefs.getUserEmail()
        val response = authApi.compareAndVerifyRecoveryCode(
            CompareAndVerifyRecoveryCodeRequest(userEmail, verificationCode)
        )
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse(true)))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun resetPassword(
        password: String,
        rePassword: String,
        verificationCode: String
    ) = flow {
        val userEmail = sharedPrefs.getUserEmail()
        val response = authApi.resetPassword(
            ResetPasswordRequest(
                userEmail,
                password,
                rePassword,
                verificationCode
            )
        )
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse(true)))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendActivationCode() = flow {
        val userEmail = sharedPrefs.getUserEmail()
        val response = authApi.sendActivationCode(ActivationCodeRequest(userEmail))
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse(true)))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun compareAndVerifyActivationCode(verificationCode: String) = flow {
        val userEmail = sharedPrefs.getUserEmail()
        val response = authApi.compareAndVerifyActivationCode(
            VerifyActivationCodeRequest(userEmail, verificationCode)
        )
        if (response.isSuccessful) {
            with(response.body()!!) {
                sharedPrefs.saveAccessToken(accessToken)
                sharedPrefs.saveRefreshToken(refreshToken)
                sharedPrefs.saveUserEmail(userEmail.trim())
            }
            emit(BaseResult.Success(SuccessGenericResponse(true)))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun deleteAccountFromServer(): Flow<BaseResult<Boolean, ErrorGenericResponse>> =
        flow {
            val response = authApi.deleteUserAccount()
            if (response.isSuccessful) {
                sharedPrefs.saveFirebaseToken("")
                emit(BaseResult.Success(true))
            } else {
                errorGenerator.validateError(response)?.let { emit(it) }
            }
        }
}
