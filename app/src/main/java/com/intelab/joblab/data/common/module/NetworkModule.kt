package com.intelab.joblab.data.common.module

import android.content.Context
import com.intelab.joblab.BuildConfig
import com.intelab.joblab.data.common.utils.RequestInterceptor
import com.intelab.joblab.data.common.utils.TokenAuthenticator
import com.intelab.joblab.domain.common.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit {
        return Retrofit.Builder().apply {
            addConverterFactory(GsonConverterFactory.create())
            client(okHttp)
            baseUrl(BuildConfig.BASE_URL)
        }.build()
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        requestInterceptor: RequestInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(requestInterceptor)
            authenticator(tokenAuthenticator)
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                addInterceptor(interceptor = interceptor)
            }
        }.build()
    }

    @Provides
    fun provideRequestInterceptor(prefs: SharedPrefs, ctx: Context): RequestInterceptor {
        return RequestInterceptor(prefs, ctx)
    }

    @Provides
    fun provideTokenAuthenticator(
        prefs: SharedPrefs,
        requestInterceptor: RequestInterceptor,
    ): TokenAuthenticator {
        return TokenAuthenticator(prefs, requestInterceptor)
    }

    @Provides
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }
}
