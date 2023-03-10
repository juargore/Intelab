package com.intelab.joblab.domain.usecases.preferences

interface IPreferencesRepository {

    fun getAccessToken(): String

    fun clearSessionTokens()

    fun saveEmail(email: String)

    fun getEmail(): String

    fun saveFirebaseToken(firebaseToken: String)

    fun getFirebaseToken(): String

    fun saveDeviceId(deviceId: String)
}
