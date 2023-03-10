package com.intelab.joblab.presentation.ui.home.main.viewmodels

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.Bindable
import com.intelab.joblab.domain.entities.AccutestResult
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.rotateDown
import com.intelab.joblab.presentation.extensions.rotateUp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TabWeaknessesViewModel @Inject constructor() : ObservableViewModel() {

    @get:Bindable
    var weaknesses by bindDelegate<List<AccutestResult>>(listOf())

    @get:Bindable
    var answerClosed by bindDelegate(true)

    fun onArrowClicked(v: View?) {
        answerClosed = if (answerClosed) {
            v?.rotateUp(); false
        } else {
            v?.rotateDown(); true
        }
    }

    val onRecyclerViewArrowClicked: (v: View, title: View, rv: View) -> Unit =
        { v, title, rv ->
            if (v.rotation == 0f) v.rotateUp() else v.rotateDown()
            rv.visibility = if (rv.isVisible) View.GONE else View.VISIBLE
            title.visibility = if (title.isVisible) View.GONE else View.VISIBLE
        }
}
