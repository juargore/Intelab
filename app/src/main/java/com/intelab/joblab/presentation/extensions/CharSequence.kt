package com.intelab.joblab.presentation.extensions

import java.text.Normalizer

val REGEX_WITHOUT_ACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun CharSequence.withoutMarkAccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_WITHOUT_ACCENT.replace(temp, "")
}