package com.intelab.joblab.presentation.ui.bindings

import androidx.databinding.BindingAdapter
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getProgressFormula
import com.intelab.joblab.presentation.ui.views.CircularCompletionView

@BindingAdapter("circularProgress")
fun CircularCompletionView.setProgress(progress: Int) {
    setCompletionPercentage(getProgressFormula(progress))
}