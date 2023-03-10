package com.intelab.joblab.presentation.extensions

import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.runWhenReady(action: () -> Unit) {
    val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            action()
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}