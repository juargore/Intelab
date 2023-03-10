package com.intelab.joblab.data.repositories

import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.domain.usecases.preferences.IPreferencesRepository

class PreferencesRepositoryImpl constructor(private val sharedPrefs: SharedPrefs) : IPreferencesRepository {

    override fun getAccessToken(): String {
        return sharedPrefs.getAccessToken()
    }

    override fun clearSessionTokens() {
        sharedPrefs.clearAccessToken()
        sharedPrefs.clearRefreshToken()
        sharedPrefs.clearUserEmail()
    }

    override fun saveEmail(email: String) {
        sharedPrefs.saveUserEmail(email)
    }

    override fun getEmail(): String {
        return sharedPrefs.getUserEmail()
    }

    override fun saveFirebaseToken(firebaseToken: String) {
        sharedPrefs.saveFirebaseToken(firebaseToken)
    }

    override fun getFirebaseToken(): String {
        return sharedPrefs.getFirebaseToken()
    }

    override fun saveDeviceId(deviceId: String) {
        sharedPrefs.saveDeviceId(deviceId)
    }
}
