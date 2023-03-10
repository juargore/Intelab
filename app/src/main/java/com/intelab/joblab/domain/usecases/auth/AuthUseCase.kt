package com.intelab.joblab.domain.usecases.auth

import com.intelab.joblab.domain.entities.requests.LoginRequest
import com.intelab.joblab.domain.entities.requests.SignUpRequest
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend fun userLogin(loginRequest: LoginRequest) = authRepository.userLogin(loginRequest)

    suspend fun userLogout() = authRepository.userLogOut()

    suspend fun userSignup(signUpRequest: SignUpRequest) = authRepository.userSignUp(signUpRequest)

    suspend fun checkForUserState() = authRepository.checkUserState()

    suspend fun sendEmailRecoveryCode(userEmail: String) = authRepository.sendRecoveryCode(userEmail)

    suspend fun compareAndVerifyPasswordRecoveryCode(validationCode: String) =
        authRepository.compareAndVerifyRecoveryCode(validationCode)

    suspend fun resetPassword(password: String, rePassword: String, validationCode: String) =
        authRepository.resetPassword(password, rePassword, validationCode)

    suspend fun sendActivationCode() = authRepository.sendActivationCode()

    suspend fun compareAndVerifyActivationCode(validationCode: String) =
        authRepository.compareAndVerifyActivationCode(validationCode)

    suspend fun deleteAccountFromServer() = authRepository.deleteAccountFromServer()
}
