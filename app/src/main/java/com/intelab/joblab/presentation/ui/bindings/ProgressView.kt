package com.intelab.joblab.presentation.ui.bindings

import androidx.databinding.BindingAdapter
import com.intelab.joblab.presentation.ui.views.ProgressView

@BindingAdapter("value")
fun ProgressView.setProgress(value :Int){
    setValue(value)
}