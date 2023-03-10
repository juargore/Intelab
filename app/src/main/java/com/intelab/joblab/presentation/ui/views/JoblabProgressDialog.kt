package com.intelab.joblab.presentation.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.intelab.joblab.R
import com.intelab.joblab.presentation.base.BaseDialogHelper

@SuppressLint("InflateParams")
class JoblabProgressDialog(context: Context) : BaseDialogHelper() {

    init {
        cancelable = false
    }

    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.popup_progress_dialog, null)
    }

    override val builder: AlertDialog.Builder =
        AlertDialog.Builder(context, R.style.JoblabProgressDialogStyle).setView(dialogView)
}