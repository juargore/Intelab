package com.intelab.joblab.data.common.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intelab.joblab.R
import com.intelab.joblab.data.common.module.BAD_REQUEST_ERROR_CODE
import com.intelab.joblab.data.common.module.CONNECTION_ERROR_CODE
import com.intelab.joblab.data.common.module.REFRESH_TOKEN_EXPIRED_CODE
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import retrofit2.Response
import javax.inject.Inject

class ErrorGenerator @Inject constructor(
    private val context: Context,
    private val sharedPrefs: SharedPrefs
) {
    private val type = object : TypeToken<ErrorGenericResponse>(){}.type

    fun <T>validateError(response: Response<T>): BaseResult.Error<ErrorGenericResponse>? {
        return if (response.code() != CONNECTION_ERROR_CODE) {
            returnErrorResponse(response)
        } else {
            null
        }
    }

    fun <T> returnErrorResponse(response: Response<T>): BaseResult.Error<ErrorGenericResponse> {
        try {
            val err: ErrorGenericResponse =
                Gson().fromJson(response.errorBody()!!.charStream(), type)
            return if (err.status == REFRESH_TOKEN_EXPIRED_CODE) {
                sharedPrefs.clearAccessToken()
                sharedPrefs.clearRefreshToken()
                BaseResult.Error(
                    ErrorGenericResponse(
                        timestamp = "",
                        status = REFRESH_TOKEN_EXPIRED_CODE,
                        error = context.getString(R.string.dialog_title_error),
                        messageKey = "",
                        message = context.getString(R.string.dialog_message_error_refresh_token_expired),
                        path = context.getString(R.string.dialog_message_none),
                        details = emptyList()
                    )
                )
            } else {
                BaseResult.Error(err)
            }
        } catch (e: Exception) {
            /* sometimes the error response is not well formatted and it is crashing the app;
               let's create a generic error response if that happens again */
            return getGenericError(e)
        }
    }

    private fun getGenericError(e: Exception? = null): BaseResult.Error<ErrorGenericResponse> {
        return BaseResult.Error(
            ErrorGenericResponse(
                timestamp = "",
                status = BAD_REQUEST_ERROR_CODE,
                error = e?.message ?: "",
                messageKey = "",
                message = context.getString(R.string.message_something_went_wrong),
                path = context.getString(R.string.dialog_message_none),
                details = emptyList()
            )
        )
    }
}