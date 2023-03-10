package com.intelab.joblab.presentation.ui.bindings

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.intelab.joblab.presentation.ui.drawables.ParallelogramDrawable
import com.intelab.joblab.presentation.ui.drawables.TrapezeDrawable
import com.intelab.joblab.presentation.ui.drawables.TriangleDrawable

@BindingAdapter("parallelogramBackground")
fun View.setParallelogramBackground(@ColorRes colorRes: Int) {
    val color = ContextCompat.getColor(context, colorRes)
    val drawable = ParallelogramDrawable()
    drawable.paint = Paint().apply {
        setARGB(255, Color.red(color), Color.green(color), Color.blue(color))
    }
    background = drawable
}

@BindingAdapter("trapezeBackground")
fun View.setTrapezeBackground(@ColorRes colorRes: Int) {
    val color = ContextCompat.getColor(context, colorRes)
    val drawable = TrapezeDrawable()
    drawable.paint = Paint().apply {
        setARGB(255, Color.red(color), Color.green(color), Color.blue(color))
    }
    background = drawable
}

@BindingAdapter("triangleBackground")
fun View.setTriangleBackground(@ColorRes colorRes: Int) {
    val color = ContextCompat.getColor(context, colorRes)
    val drawable = TriangleDrawable()
    drawable.paint = Paint().apply {
        setARGB(255, Color.red(color), Color.green(color), Color.blue(color))
    }
    background = drawable
}

@BindingAdapter("customProgress")
fun ProgressBar.setCustomProgress(advance: Int) {
    progress = advance
    max = 8
}