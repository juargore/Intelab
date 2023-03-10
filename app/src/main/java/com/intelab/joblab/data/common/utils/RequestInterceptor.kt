package com.intelab.joblab.data.common.utils

import android.content.Context
import com.intelab.joblab.R
import com.intelab.joblab.data.common.module.CONNECTION_ERROR_CODE
import com.intelab.joblab.data.common.module.EXCEPTION_ERROR_CODE
import com.intelab.joblab.domain.common.Connectivity
import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.presentation.base.utils._appJsonType
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class RequestInterceptor constructor(
    private val pref: SharedPrefs,
    context: Context
) : Interceptor {

    private var mContext = context

    private val responseAnyException: (Int, String) -> Response = { code, message ->
        val errorResponse = """{"message":"$message"}"""
        val errorResponseBody =
            errorResponse.toResponseBody(_appJsonType.toMediaTypeOrNull())

        Response.Builder().code(code).request(
            Request.Builder().url("http://test-url/").build()
        ).protocol(Protocol.HTTP_1_1).body(errorResponseBody)
            .message("Response Any Exception").build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("x-source", "APP")
            .addHeader("Content-Type", "application/json; charset=utf-8")

        if (chain.request().header("Set-Token") != null)
            newRequest.addHeader("Authorization", "Bearer ${pref.getAccessToken()}")

        val totalRetries = 3
        var numRetries = 0
        val timeOuts = listOf(8, 13, 21, 34) // fibonacci sequence numbers

        chain.withConnectTimeout(timeOuts[0], TimeUnit.SECONDS)
        chain.withReadTimeout(timeOuts[0], TimeUnit.SECONDS)
        chain.withWriteTimeout(timeOuts[0], TimeUnit.SECONDS)

        while (numRetries <= totalRetries) {
            if (Connectivity(mContext).isNetworkAvailable()) {
                try {
                    numRetries++
                    return chain.proceed(newRequest.build())
                } catch (to: TimeoutException) {
                    if (numRetries <= totalRetries) {
                        chain.withConnectTimeout(timeOuts[numRetries], TimeUnit.SECONDS)
                        chain.withReadTimeout(timeOuts[numRetries], TimeUnit.SECONDS)
                        chain.withWriteTimeout(timeOuts[numRetries], TimeUnit.SECONDS)
                    }
                } catch (e: Exception) {
                    return responseAnyException(
                        EXCEPTION_ERROR_CODE,
                        mContext.getString(R.string.message_something_went_wrong)
                    )
                }
            } else {
                return responseAnyException(CONNECTION_ERROR_CODE, "")
            }
        }

        return responseAnyException(
            EXCEPTION_ERROR_CODE,
            mContext.getString(R.string.message_something_went_wrong)
        )
    }
}
