package com.intelab.joblab.presentation.ui.bindings

import android.text.*
import android.text.InputFilter.LengthFilter
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.intelab.joblab.R
import com.intelab.joblab.presentation.extensions.isEmail
import com.intelab.joblab.presentation.extensions.isValidCurp
import java.util.regex.Pattern

val EMAIL_ADDRESS_PATTERN: Pattern =
    Pattern.compile("[a-zA-Z0-9!#\$%&'*+-/=?^_`{|}~\"(),:;<>@\\[\\]]")

val PASSWORD_PATTERN: Pattern = Pattern.compile("[a-zA-Z0-9~`!@#\$%^&*_()-+={\\[}\\]|:;\"'<,>.?/]")

val ALPHANUMERIC_SPECIAL_CHARACTERS: Pattern =
    Pattern.compile("[a-zA-Z0-9À-ú~`!@#\$%^&*_()-+={\\[}\\]|:;\"'<,>.?/\\s]")

val ALPHABETIC_SPANISH_CHARACTERS: Pattern = Pattern.compile("[a-zA-ZÁÉÍÓÚáéíóúüñÑ ]")

private fun isOnlyAlphanumeric(c: Char): Boolean {
    return Character.isLetterOrDigit(c) || Character.isWhitespace(c)
}

private fun isOnlyAlphabetic(c: Char): Boolean {
    return ALPHABETIC_SPANISH_CHARACTERS.matcher(c.toString()).find()
}

private fun isOnlyEmailCharacters(c: Char): Boolean {
    return EMAIL_ADDRESS_PATTERN.matcher(c.toString()).find()
}

private fun isPasswordCharacters(c: Char): Boolean {
    return PASSWORD_PATTERN.matcher(c.toString()).find()
}

private fun isAlphanumericSpecialCharacters(c: Char): Boolean {
    return ALPHANUMERIC_SPECIAL_CHARACTERS.matcher(c.toString()).find()
}

private fun isAlphanumericDash(c: Char): Boolean {
    return Character.isLetterOrDigit(c) || Character.isWhitespace(c) || c == '-'
}

fun textFilter(
    source: CharSequence,
    start: Int,
    end: Int,
    f: (Char) -> Boolean
): CharSequence? {
    var keepOriginal = true
    val sb = StringBuilder(end - start)
    for (i in start until end) {
        val c: Char = source[i]
        if (f(c)) sb.append(c) else keepOriginal = false
    }
    return if (keepOriginal) null else {
        if (source is Spanned) {
            val sp = SpannableString(sb)
            TextUtils.copySpansFrom(source, start, sb.length, null, sp, 0)
            sp
        } else {
            sb
        }
    }
}

@BindingAdapter("alphabetic", "maxLength", requireAll = false)
fun EditText.showOnlyAlphabetic(boolean: Boolean, maxLength: Int?) {
    if (boolean) {
        val maxValue = maxLength ?: Int.MAX_VALUE
        filters = arrayOf()
        val textFilter = InputFilter { source, start, end, _, _, _ ->
            textFilter(source, start, end, ::isOnlyAlphabetic)
        }
        val lengthFilter = LengthFilter(maxValue)
        filters = arrayOf(textFilter, lengthFilter)
    }
}

@BindingAdapter("customHint")
fun EditText.customHint(@StringRes id: Int?) {
    id?.let { setHint(it) }
}

@BindingAdapter("alphanumeric", "maxLength", requireAll = false)
fun EditText.showOnlyAlphanumeric(boolean: Boolean?, maxLength: Int?) {
    if (boolean == true) {
        val maxValue = maxLength ?: Int.MAX_VALUE
        val filtersList = filters.toMutableList()
        val textFilter = InputFilter { source, start, end, _, _, _ ->
            textFilter(source, start, end, ::isOnlyAlphanumeric)
        }
        val lengthFilter = LengthFilter(maxValue)
        filtersList.apply {
            add(textFilter)
            add(lengthFilter)
        }
        filters = filtersList.toTypedArray()
    }
}

@BindingAdapter("textEmailAddress", "maxLength", requireAll = false)
fun EditText.filterTextEmailAddress(boolean: Boolean?, maxLength: Int?) {
    if (boolean == true) {
        val maxValue = maxLength ?: Int.MAX_VALUE
        val textFilter = InputFilter { source, start, end, _, _, _ ->
            textFilter(source, start, end, ::isOnlyEmailCharacters)
        }
        val lengthFilter = LengthFilter(maxValue)
        filters = arrayOf(textFilter, lengthFilter)
    }
}

@BindingAdapter("specialCharacters", "maxLength", requireAll = false)
fun EditText.filterSpecialCharacters(boolean: Boolean?, maxLength: Int?) {
    if (boolean == true) {
        val maxValue = maxLength ?: Int.MAX_VALUE
        val textFilter = InputFilter { source, start, end, _, _, _ ->
            textFilter(source, start, end, ::isAlphanumericSpecialCharacters)
        }
        val lengthFilter = LengthFilter(maxValue)
        filters = arrayOf(textFilter, lengthFilter)
    }
}

@BindingAdapter("alphanumericDash", "maxLength", requireAll = false)
fun EditText.filterAlphanumericDash(boolean: Boolean?, maxLength: Int?) {
    if (boolean == true) {
        val maxValue = maxLength ?: Int.MAX_VALUE
        val textFilter = InputFilter { source, start, end, _, _, _ ->
            textFilter(source, start, end, ::isAlphanumericDash)
        }
        val lengthFilter = LengthFilter(maxValue)
        filters = arrayOf(textFilter, lengthFilter)
    }
}

@BindingAdapter("textPassword", "maxLength", requireAll = false)
fun EditText.filterTextPassword(onFilter: Boolean?, maxLength: Int?) {
    val maxValue = maxLength ?: Int.MAX_VALUE
    val textFilter = InputFilter { source, start, end, _, _, _ ->
        if (onFilter == true)
            textFilter(source, start, end, ::isPasswordCharacters)
        else
            source
    }
    val lengthFilter = LengthFilter(maxValue)
    filters = arrayOf(textFilter, lengthFilter)
}

@BindingAdapter("maxFieldLength")
fun EditText.setMaximumFieldLength(maxLength: Int) {
    val filtersList = filters.toMutableList()
    val lengthFilter = LengthFilter(maxLength)
    filtersList.add(lengthFilter)
    filters = filtersList.toTypedArray()
}

@BindingAdapter("validateEmailFormat")
fun EditText.setEmailFormatValidation(boolean: Boolean?) {
    if (boolean == true) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                error = null
            }
        })
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = editableText.toString()
                error =
                    if (text.isEmpty() || text.isEmail()) null
                    else context.getString(R.string.et_message_invalid_email)
            }
        }
    }
}

@BindingAdapter("errorMessage", "minLength", requireAll = true)
fun EditText.setErrorMessage(@StringRes id: Int?, minLength: Int) {
    id?.let {
        error = context.getString(id, minLength)
    } ?: also {
        error = null
    }
}

@BindingAdapter("errorMessage")
fun EditText.setErrorMessage(@StringRes id: Int?) {
    id?.let {
        error = context.getString(id)
    } ?: also {
        error = null
    }
}

@BindingAdapter("inputFilter")
fun EditText.setFilter(boolean: Boolean) {
    if (boolean) {
        val filter =
            InputFilter { _, _, _, _, _, _ ->
                error = if (!text.toString().isValidCurp()) {
                    context.getString(R.string.dialog_profile_description_invalid_curp)
                } else {
                    null
                }
                null
            }

        val filtersList = filters.toMutableList()
        filtersList.add(filter)
        filters = filtersList.toTypedArray()
    }
}
