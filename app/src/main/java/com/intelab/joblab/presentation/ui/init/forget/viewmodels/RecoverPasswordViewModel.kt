package com.intelab.joblab.presentation.ui.init.forget.viewmodels

import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexOneNegative
import com.intelab.joblab.presentation.base.utils._validationCode
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    val authUseCase: AuthUseCase,
    val savedStateHandle: SavedStateHandle,
    val authValidationUseCase: AuthValidationUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<RecoverPasswordState>(RecoverPasswordState.Init)

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var passwordMessage by bindDelegate(_indexOneNegative)

    @get:Bindable
    var passwordConfMessage by bindDelegate(_indexOneNegative)

    @get:Bindable
    var password by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var rePassword by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    fun onNextClicked() {
        if (!authValidationUseCase.isValidPassword(password)) {
            passwordMessage = R.string.error_password_requirements
        }
        if (password != rePassword) {
            passwordConfMessage = R.string.et_message_different_password
        }
        if (passwordMessage != _indexOneNegative || passwordConfMessage != _indexOneNegative)
            return

        launch {
            val validationCode = savedStateHandle.get<String>(_validationCode) ?: ""
            authUseCase.resetPassword(password, rePassword, validationCode)
                .onStart { loadingWithDelay(this@RecoverPasswordViewModel, true)
                }.collect { result ->
                    loadingWithDelay(this@RecoverPasswordViewModel, false)
                    when (result) {
                        is BaseResult.Error -> state.value = RecoverPasswordState.ErrorRecoverPassword(result.rawResponse)
                        is BaseResult.Success -> state.value = RecoverPasswordState.OpenSuccessDialog(R.string.dialog_message_success_changed_password)
                    }
                }
        }
    }

    private fun isNextBnEnabled() : Boolean = password.isNotEmpty() && rePassword.isNotEmpty()

    fun passwordAfterTextChanged() { passwordMessage = _indexOneNegative }

    fun passwordConfirmationAfterTextChanged() { passwordConfMessage = _indexOneNegative }
}

sealed class RecoverPasswordState {
    object Init : RecoverPasswordState()
    data class IsLoading(val isLoading: Boolean) : RecoverPasswordState()
    data class NotValidData(val resourceId: Int) : RecoverPasswordState()
    data class OpenLoginScreen(val directions: NavDirections) : RecoverPasswordState()
    data class OpenSuccessDialog(@StringRes val resourceId: Int) : RecoverPasswordState()
    data class ErrorRecoverPassword(val rawResponse: ErrorGenericResponse) : RecoverPasswordState()
}
