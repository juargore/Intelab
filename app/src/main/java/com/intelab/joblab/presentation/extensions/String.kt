package com.intelab.joblab.presentation.extensions

import android.text.TextUtils
import android.util.Patterns.EMAIL_ADDRESS
import com.intelab.joblab.presentation.base.utils.NOW
import java.util.*
import java.util.regex.Pattern

fun String?.isEmail() = !this.isNullOrEmpty() && EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidCurp(): Boolean {
    val pattern = Pattern.compile("^([A-Z][AEIOUX][A-Z]{2}\\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\\d|3[01])[HM](?:AS|B[CS]|C[CLMSH]|D[FG]|G[TR]|HG|JC|M[CNS]|N[ETL]|OC|PL|Q[TR]|S[PLR]|T[CSL]|VZ|YN|ZS)[B-DF-HJ-NP-TV-Z]{3}[A-Z\\d])(\\d)\$")
    return pattern.matcher(this.uppercase(Locale.getDefault())).matches()
}

fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone() = length == 10

fun String.replaceDoubleSpace() = replace("  ", " ")

fun String?.upperCaseDefault() = this?.uppercase(Locale.getDefault())?.trim() ?: ""

fun String?.lowerCaseDefault() = this?.lowercase(Locale.getDefault())?.trim() ?: ""

fun String.toStartedYear() = substring(0, 4).toInt()

fun String.toStartedMonth() = substring(5, 7).toInt() - 1

fun String.appendDayAtEnd(): String {
    return if (this == NOW) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        var month = calendar[Calendar.MONTH].toString()
        if (month.length == 1) {
            month = "0$month"
        }
        "$year-$month-01"
    } else {
        val year = this.substringBefore("-")
        var month = this.substringAfter("-").substringBeforeLast("-")
        if (month.length == 1) {
            month = "0$month"
        }
        "$year-$month-01"
    }
}