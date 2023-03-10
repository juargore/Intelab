package com.intelab.joblab.presentation.ui.bindings

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("message")
fun TextInputLayout.setEmailMessage(messageId: Int) {
    if (messageId == -1) {
        error = null
        isErrorEnabled = false
    } else {
        isErrorEnabled = true
        error = context.getString(messageId)
    }
}
