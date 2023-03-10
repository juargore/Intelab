package com.intelab.joblab.presentation.ui.home.accutest.viewmodels

import androidx.navigation.NavDirections
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.ui.home.accutest.fragment.AccutestInstructionFragmentDirections
import kotlinx.coroutines.flow.MutableStateFlow

class AccutestInstructionViewModel : ObservableViewModel() {

    val state = MutableStateFlow<AccutestInstructionState>(AccutestInstructionState.Init)

    fun onAcceptClicked() {
        val directions = AccutestInstructionFragmentDirections.actionAccutestInstructionFragmentToAccutestStepOneFragment()
        state.value = AccutestInstructionState.OpenTestState(directions)
    }
}

sealed class AccutestInstructionState {
    object Init : AccutestInstructionState()
    data class OpenTestState(val directions: NavDirections) : AccutestInstructionState()
}