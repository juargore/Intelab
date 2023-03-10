package com.intelab.joblab.data.common.utils

import com.intelab.joblab.BuildConfig
import com.intelab.joblab.data.apis.AuthApi
import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.domain.entities.requests.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator constructor(
    private val sharedPrefs: SharedPrefs, private val requestInterceptor: RequestInterceptor
) : Authenticator {

    private fun buildTokenApi(): AuthApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(getRetrofitClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    private fun getRetrofitClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Accept", "application/json")
                }.build())
            }
            addInterceptor(requestInterceptor)
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                addInterceptor(interceptor = interceptor)
            }
        }.build()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val accessToken = getUpdatedToken()
            if (!accessToken.isNullOrEmpty()) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            } else null
        }
    }

    private suspend fun getUpdatedToken(): String? {
        val userEmail = sharedPrefs.getUserEmail()
        val refreshToken = sharedPrefs.getRefreshToken()
        val authApi = buildTokenApi()
        val response = authApi.refreshToken(RefreshTokenRequest(userEmail, refreshToken))
        if (response.isSuccessful) {
            response.body()?.let {
                sharedPrefs.saveAccessToken(it.accessToken)
                sharedPrefs.saveRefreshToken(it.refreshToken)
                sharedPrefs.saveUserEmail(userEmail)
                return it.accessToken
            }
        }
        return null
    }

}