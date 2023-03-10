package com.intelab.joblab.presentation.ui.init.forget.viewmodels

import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.ui.init.forget.fragment.ForgetPasswordFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    val authValidationUseCase: AuthValidationUseCase,
    val authUseCase: AuthUseCase,
    val preferencesUseCase: PreferencesUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ForgetPasswordState>(ForgetPasswordState.Init)

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var email by bindDelegate("") { _ , field ->
        nextBnEnabled = field.isNotEmpty()
    }

    fun onBackClicked() { state.value = ForgetPasswordState.BackLoginScreen }

    fun onNextClicked() {
        if (!authValidationUseCase.isValidEmail(email)) {
            state.value = ForgetPasswordState.NoValidData(R.string.et_message_invalid_email)
        } else {
            preferencesUseCase.saveEmail(email.trim())
            launch {
                authUseCase.sendEmailRecoveryCode(email)
                    .onStart { loadingWithDelay(this@ForgetPasswordViewModel, true) }
                    .collect { result ->
                        loadingWithDelay(this@ForgetPasswordViewModel, false)
                        when (result) {
                            is BaseResult.Error ->
                                state.value = ForgetPasswordState.ErrorForgetPassword(result.rawResponse)
                            is BaseResult.Success -> {
                                val directions = ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToVerificationCodeFragment()
                                state.value = ForgetPasswordState.OpenVerificationCodeScreen(directions)
                            }
                        }
                }
            }
        }
    }
}

sealed class ForgetPasswordState {
    object Init : ForgetPasswordState()
    object BackLoginScreen : ForgetPasswordState()
    data class NoValidData(val resourceId: Int) : ForgetPasswordState()
    data class IsLoading(val isLoading: Boolean) : ForgetPasswordState()
    data class OpenVerificationCodeScreen(val directions: NavDirections) : ForgetPasswordState()
    data class ErrorForgetPassword(val rawResponse: ErrorGenericResponse) : ForgetPasswordState()
}
