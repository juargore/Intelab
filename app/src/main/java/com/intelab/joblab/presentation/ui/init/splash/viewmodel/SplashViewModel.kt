package com.intelab.joblab.presentation.ui.init.splash.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.ui.init.splash.fragment.SplashFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    val preferencesUseCase: PreferencesUseCase,
    val authUseCase: AuthUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<SplashState>(SplashState.Init)

    fun initialize() {
        if (preferencesUseCase.getAccessToken().isEmpty()) {
            goToOnBoardingScreen()
        } else {
            callUserStateService()
        }
    }

    private fun callUserStateService() {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.checkForUserState().collect { result ->
                when (result) {
                    is BaseResult.Error -> goToLoginScreen()
                    is BaseResult.Success -> redirectScreen(result.data.state)
                }
            }
        }
    }

    private fun redirectScreen(userState: String) {
        when (userState) {
            USER_INITIAL_REGISTER_STATE -> {
                state.value = SplashState.OpenAuthorizationScreen(
                    R.string.deep_link_register_authorization,
                    R.id.splashFragment, true
                )
            }
            USER_COMPLEMENTARY_REGISTER_STATE, USER_COMPLETED_STATE -> {
                val directions = SplashFragmentDirections.actionSplashFragmentToHomeNavigation()
                state.value = SplashState.OpenHomeScreen(directions)
            }
            NOT_EXIST -> {
                state.value = SplashState.OpenDialog(R.string.dialog_message_error_delete_account)
            }
        }
    }

    fun clearTokens() { preferencesUseCase.clearSessionTokens() }

    fun goToLoginScreen() {
        val directions = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        state.value = SplashState.OpenLoginScreen(directions)
    }

    private fun goToOnBoardingScreen() {
        val directions = SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment()
        state.value = SplashState.OpenOnBoardingScreen(directions)
    }
}

sealed class SplashState {
    object Init : SplashState()
    data class OpenLoginScreen(val direction: NavDirections) : SplashState()
    data class OpenHomeScreen(val direction: NavDirections) : SplashState()
    data class OpenDialog(@StringRes val message: Int) : SplashState()
    data class OpenOnBoardingScreen(val direction: NavDirections) : SplashState()
    data class OpenAuthorizationScreen(
        @StringRes val deepLink: Int,
        val popUpTo: Int?,
        val popUpToInclusive: Boolean
    ) : SplashState()
}