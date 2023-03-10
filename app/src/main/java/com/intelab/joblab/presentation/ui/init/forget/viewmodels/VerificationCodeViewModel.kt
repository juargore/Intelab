package com.intelab.joblab.presentation.ui.init.forget.viewmodels

import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.base.utils._delay10
import com.intelab.joblab.presentation.ui.init.forget.fragment.VerificationCodeFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    val authUseCase: AuthUseCase,
    val preferencesUseCase: PreferencesUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<VerificationCodeState>(VerificationCodeState.Init)

    @get:Bindable
    var maskEmail by bindDelegate("")

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var verificationCode by bindDelegate("") { _ , field ->
        nextBnEnabled = field.isNotEmpty()
    }

    init {
        val email = preferencesUseCase.getEmail()
        maskEmail = email.replace("(^[^@]|(?!^)\\G)[^@]".toRegex(), "$1*")
    }

    fun onNextClicked() {
        launch {
            authUseCase.compareAndVerifyPasswordRecoveryCode(verificationCode)
                .onStart { loadingWithDelay(this@VerificationCodeViewModel, true) }
                .collect { result ->
                    loadingWithDelay(this@VerificationCodeViewModel, false)
                    when (result) {
                        is BaseResult.Error -> state.value = VerificationCodeState.ErrorVerificationCode(result.rawResponse)
                        is BaseResult.Success -> {
                            state.value = VerificationCodeState.OpenRecoverPasswordScreen(
                                VerificationCodeFragmentDirections.actionVerificationCodeFragmentToRecoverPasswordFragment(
                                    verificationCode
                                )
                            )
                        }
                    }
                }
        }
    }

    fun onResentCodeClicked() {
        launch {
            val email = preferencesUseCase.getEmail()
            authUseCase.sendEmailRecoveryCode(email).onStart {
                state.value = VerificationCodeState.IsLoading(true)
                delay(_delay10)
            }.collect { result ->
                state.value = VerificationCodeState.IsLoading(false)
                delay(_delay10)
                when (result) {
                    is BaseResult.Error -> state.value = VerificationCodeState.ErrorVerificationCode(result.rawResponse)
                    is BaseResult.Success -> {
                        state.value = VerificationCodeState.OpenDialog(
                            R.string.dialog_title_recover_password,
                            R.string.dialog_description_resend_code
                        )
                    }
                }
            }
        }
    }

}

sealed class VerificationCodeState {
    object Init : VerificationCodeState()
    data class IsLoading(val isLoading: Boolean) : VerificationCodeState()
    data class ErrorVerificationCode(val rawResponse: ErrorGenericResponse) : VerificationCodeState()
    data class OpenRecoverPasswordScreen(val directions: NavDirections) : VerificationCodeState()
    data class OpenDialog(val title: Int, val description: Int) : VerificationCodeState()
}
