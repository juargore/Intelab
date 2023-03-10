package com.intelab.joblab.presentation.ui.init.register.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.requests.SignUpRequest
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.ui.init.register.fragments.CreateAccountFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val preferencesUseCase: PreferencesUseCase,
    val authValidationUseCase: AuthValidationUseCase,
    val authUseCase: AuthUseCase,
    savedStateHandle: SavedStateHandle
) : ObservableViewModel() {

    val state = MutableStateFlow<CreateAccountState>(CreateAccountState.Init)

    @get:Bindable
    var nextButtonEnabled by bindDelegate(false)

    @get:Bindable
    var emailMessage by bindDelegate(_indexOneNegative)

    @get:Bindable
    var passwordMessage by bindDelegate(_indexOneNegative)

    @get:Bindable
    var passwordConfMessage by bindDelegate(_indexOneNegative)

    @get:Bindable
    var email by bindDelegate(savedStateHandle[_email] ?: "") { _, _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var password by bindDelegate("") { _, _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var confirmPassword by bindDelegate("") { _, _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    private fun isButtonEnabled(): Boolean =
        email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()

    fun onNextClicked() {
        if (!authValidationUseCase.isValidEmail(email)) {
            emailMessage = R.string.et_message_invalid_email
        }
        if (!authValidationUseCase.isValidPassword(password)) {
            passwordMessage = R.string.error_password_requirements
        }
        if (password != confirmPassword) {
            passwordConfMessage = R.string.et_message_different_password
        }
        if (emailMessage != _indexOneNegative || passwordMessage != _indexOneNegative || passwordConfMessage != _indexOneNegative)
            return

        // store user email on internal db and create a new register
        preferencesUseCase.saveEmail(email.trim())
        launch {
            authUseCase.checkForUserState().onStart {
                loadingWithDelay(this@CreateAccountViewModel, true)
            }.collect { result ->
                loadingWithDelay(this@CreateAccountViewModel, false)
                when (result) {
                    is BaseResult.Error -> state.value = CreateAccountState.ErrorCreateAccount(result.rawResponse)
                    is BaseResult.Success -> {
                        when (result.data.profile) {
                            RECRUITER_PROFILE ->
                                state.value = CreateAccountState.NotValidData(R.string.dialog_create_account_error_try_another_email)
                            else -> {
                                when (result.data.state) {
                                    USER_ACTIVATE_STATE -> {
                                        updateRegistrationData()
                                        state.value = CreateAccountState.OpenActivateAccountScreen(
                                            CreateAccountFragmentDirections.actionCreateAccountFragmentToActivateAccountFragment()
                                        )
                                    }
                                    USER_NOT_EXIST_STATE -> {
                                        updateRegistrationData()
                                        signupUser()
                                    }
                                    else -> state.value = CreateAccountState.BackLoginScreen
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateRegistrationData() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(email = email),
                CreateAccountViewModel::class.simpleName
            )
        }
    }

    private fun signupUser() {
        launch {
            authUseCase.userSignup(SignUpRequest(email, password, confirmPassword)).collect { result ->
                when (result) {
                    is BaseResult.Error -> state.value = CreateAccountState.ErrorCreateAccount(result.rawResponse)
                    is BaseResult.Success -> {
                        val directions = CreateAccountFragmentDirections.actionCreateAccountFragmentToActivateAccountFragment()
                        state.value = CreateAccountState.OpenActivateAccountScreen(directions)
                    }
                }
            }
        }
    }

    fun onBackClicked() {
        state.value = CreateAccountState.OpenDialog(
            R.string.dialog_title_create_account_cancel,
            R.string.dialog_description_create_account_cancel
        )
    }

    fun emailAfterTextChanged() { emailMessage = _indexOneNegative }

    fun passwordAfterTextChanged() { passwordMessage = _indexOneNegative }

    fun passwordConfirmationAfterTextChanged() { passwordConfMessage = _indexOneNegative }
}

sealed class CreateAccountState {
    object Init : CreateAccountState()
    object BackLoginScreen : CreateAccountState()
    data class IsLoading(val isLoading: Boolean) : CreateAccountState()
    data class NotValidData(val resourceId: Int) : CreateAccountState()
    data class OpenDialog(val title: Int, val description: Int) : CreateAccountState()
    data class OpenActivateAccountScreen(val direction: NavDirections) : CreateAccountState()
    data class ErrorCreateAccount(val rawResponse: ErrorGenericResponse) : CreateAccountState()
}
