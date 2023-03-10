package com.intelab.joblab.domain.common

import android.content.Context

/**
 * Shared Preferences is the way in which one can store and retrieve small amounts of primitive data
 * as key/value pairs to a file on the device storage such as String, int, float, Boolean that make up
 * your preferences in an XML file inside the app on the device storage.
 * */
class SharedPrefs(context: Context) {

    private val sharedPref = context.getSharedPreferences(JOBLAB_PREFERENCES, Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        put(ACCESS_TOKEN, token.trim())
    }

    fun getAccessToken(): String = get(ACCESS_TOKEN, String::class.java)

    fun clearAccessToken() {
        deleteKey(ACCESS_TOKEN)
    }

    fun saveRefreshToken(token: String) {
        put(REFRESH_TOKEN, token.trim())
    }

    fun getRefreshToken(): String = get(REFRESH_TOKEN, String::class.java)

    fun clearRefreshToken() {
        deleteKey(REFRESH_TOKEN)
    }

    fun saveUserEmail(useremail: String) {
        put(USER_NAME, useremail.trim())
    }

    fun getUserEmail(): String = get(USER_NAME, String::class.java)

    fun saveDeviceId(deviceId: String) {
        put(DEVICE_ID, deviceId.trim())
    }

    fun getDeviceId(): String = get(DEVICE_ID, String::class.java)

    fun saveFirebaseToken(firebaseToken: String) {
        put(FIREBASE_TOKEN, firebaseToken.trim())
    }

    fun getFirebaseToken(): String = get(FIREBASE_TOKEN, String::class.java)

    fun clearUserEmail() {
        deleteKey(USER_NAME)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> get(key: String, clazz: Class<T>): T =
        when (clazz) {
            String::class.java -> sharedPref.getString(key, "")
            Boolean::class.java -> sharedPref.getBoolean(key, false)
            Float::class.java -> sharedPref.getFloat(key, -1f)
            Double::class.java -> sharedPref.getFloat(key, -1f)
            Int::class.java -> sharedPref.getInt(key, -1)
            Long::class.java -> sharedPref.getLong(key, -1L)
            else -> null
        } as T

    private fun <T> put(key: String, data: T) {
        val editor = sharedPref.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }

    private fun deleteKey(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    companion object {
        private const val JOBLAB_PREFERENCES = "JoblabPreferences"
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_NAME = "userName"
        private const val DEVICE_ID = "deviceId"
        private const val FIREBASE_TOKEN = "firebaseToken"
    }
}
