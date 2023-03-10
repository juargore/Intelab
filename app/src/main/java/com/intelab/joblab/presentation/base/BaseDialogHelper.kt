package com.intelab.joblab.presentation.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog

abstract class BaseDialogHelper {

    abstract val dialogView: View
    abstract val builder: AlertDialog.Builder

    open var cancelable: Boolean = true
    open var dialog: AlertDialog? = null

    open fun create(): AlertDialog {
        dialog = builder
            .setCancelable(cancelable)
            .create()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog!!
    }
}