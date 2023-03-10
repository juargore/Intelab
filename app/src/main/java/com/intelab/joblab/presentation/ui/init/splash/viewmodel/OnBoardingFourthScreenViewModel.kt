package com.intelab.joblab.presentation.ui.init.splash.viewmodel

import androidx.navigation.NavDirections
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.ui.init.splash.fragment.OnBoardingFragmentDirections
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class OnBoardingFourthScreenViewModel @Inject constructor() : ObservableViewModel() {

    val state = MutableStateFlow<OnBoardingFourthScreenState>(OnBoardingFourthScreenState.Init)

    fun onContinueClicked() {
        val directions = OnBoardingFragmentDirections.actionOnBoardingFragmentToLoginFragment()
        state.value = OnBoardingFourthScreenState.OpenLoginScreen(directions)
    }
}

sealed class OnBoardingFourthScreenState {
    object Init : OnBoardingFourthScreenState()
    data class OpenLoginScreen(val directions: NavDirections) : OnBoardingFourthScreenState()
}