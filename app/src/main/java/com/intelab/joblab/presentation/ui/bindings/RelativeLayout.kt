package com.intelab.joblab.presentation.ui.bindings

import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("android:background")
fun RelativeLayout.setBackgroundViewColor(@ColorRes backgroundColor: Int) {
    val color = ContextCompat.getColor(context, backgroundColor)
    setBackgroundColor(color)
}