package com.intelab.joblab.domain.usecases.auth

import android.text.TextUtils
import com.intelab.joblab.BuildConfig
import java.util.regex.Pattern
import javax.inject.Inject

class AuthValidationUseCase @Inject constructor() {

    fun isValidPassword(s: String): Boolean {
        val textPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9 ]).+$")
        return !TextUtils.isEmpty(s) && textPattern.matcher(s).matches() && s.length >= 8
    }

    fun isValidEmail(s: CharSequence): Boolean {
        return if (BuildConfig.DEBUG) {
            validEmailDev(s)
        } else {
            validEmailProd(s)
        }
    }

    private fun validEmailDev(s: CharSequence) : Boolean {
        val textPattern = Pattern.compile("^([+\\w-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,20}|[0-9]{1,3})(]?)\$")
        return !TextUtils.isEmpty(s) && textPattern.matcher(s).matches() && s.length >= 8
    }

    private fun validEmailProd(s: CharSequence) : Boolean {
        val textPattern = Pattern.compile("^([\\w-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,20}|[0-9]{1,3})(]?)\$")
        return !TextUtils.isEmpty(s) && textPattern.matcher(s).matches() && s.length >= 8
    }
}
