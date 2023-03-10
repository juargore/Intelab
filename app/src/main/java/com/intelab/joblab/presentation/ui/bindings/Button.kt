package com.intelab.joblab.presentation.ui.bindings

import android.graphics.Paint
import android.widget.Button
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.BindingAdapter
import com.intelab.joblab.presentation.ui.drawables.ParallelogramDrawable

@BindingAdapter("android:background", "android:textColor", requireAll = true)
fun Button.setButtonBackground(@ColorRes backgroundColor: Int, @ColorRes textColor: Int) {
    val drawable = ParallelogramDrawable()
    val color = ContextCompat.getColor(context, backgroundColor)
    drawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        color,
        BlendModeCompat.SRC_ATOP
    )
    setTextColor(ContextCompat.getColor(context, textColor))
    background = drawable
}

@BindingAdapter("underline")
fun Button.setUnderlineText(underline: Boolean) {
    if (underline) {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}