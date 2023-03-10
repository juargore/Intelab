package com.intelab.joblab.presentation.ui.home.main.viewmodels

import android.view.View
import androidx.databinding.Bindable
import com.intelab.joblab.domain.entities.AccutestResult
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.rotateDown
import com.intelab.joblab.presentation.extensions.rotateUp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TabStrengthsViewModel @Inject constructor() : ObservableViewModel() {

    @get:Bindable
    var strengths by bindDelegate<List<AccutestResult>>(listOf())

    @get:Bindable
    var answerClosed by bindDelegate(true)

    fun onArrowClicked(v: View?) {
        answerClosed = if (answerClosed) {
            v?.rotateUp(); false
        } else {
            v?.rotateDown(); true
        }
    }
}
