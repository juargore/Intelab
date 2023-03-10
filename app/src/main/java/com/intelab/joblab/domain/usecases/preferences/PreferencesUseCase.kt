package com.intelab.joblab.domain.usecases.preferences

import javax.inject.Inject

class PreferencesUseCase @Inject constructor(private val preferencesRepository: IPreferencesRepository) {

    fun getAccessToken() = preferencesRepository.getAccessToken()

    fun saveEmail(email: String) { preferencesRepository.saveEmail(email) }

    fun getEmail() = preferencesRepository.getEmail()

    fun clearSessionTokens() { preferencesRepository.clearSessionTokens() }

    fun saveFirebaseToken(firebaseToken: String) { preferencesRepository.saveFirebaseToken(firebaseToken) }

    fun getFirebaseToken() = preferencesRepository.getFirebaseToken()

    fun saveDeviceId(deviceId: String) { preferencesRepository.saveDeviceId(deviceId) }
}
