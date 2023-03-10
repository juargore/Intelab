package com.intelab.joblab.presentation.ui.init.login.viewmodels

import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.UserState
import com.intelab.joblab.domain.entities.requests.LoginRequest
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.ui.init.login.fragment.LoginFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val autUseCase: AuthUseCase,
    private val authValidationUseCase: AuthValidationUseCase,
    private val dbUseCase: DatabaseUseCase,
    private val preferencesUseCase: PreferencesUseCase
) : ObservableViewModel() {

    var state = MutableStateFlow<LoginState>(LoginState.Init)

    @get:Bindable
    var userName by bindDelegate("")

    @get:Bindable
    var userPassword by bindDelegate("")

    @get:Bindable
    var passwordMessage by bindDelegate(false)

    @get:Bindable
    var errorMessage by bindDelegate<Int?>(null)

    fun onLoginClicked() {
        if (userName.isEmpty() || userPassword.isEmpty()) {
            state.value = LoginState.NoValidData(R.string.dialog_message_fill_email_password)
        } else if (!authValidationUseCase.isValidEmail(userName)) {
            state.value = LoginState.NoValidData(R.string.et_message_invalid_email)
        } else {
            launch {
                preferencesUseCase.saveEmail(userName)
                autUseCase.checkForUserState()
                    .onStart { loadingWithDelay(this@LoginViewModel, true) }
                    .collect { stateResult ->
                        loadingWithDelay(this@LoginViewModel, false)
                        when (stateResult) {
                            is BaseResult.Error -> state.value = LoginState.ErrorLogin(stateResult.rawResponse)
                            is BaseResult.Success -> validateDataProfile(stateResult.data)
                        }
                    }
            }
        }
    }

    private fun validateDataProfile(stateResult: UserState) {
        when (stateResult.profile) {
            RECRUITER_PROFILE -> {
                state.value = LoginState.NoValidData(R.string.dialog_login_error_go_to_web_app)
            }
            else -> validateState(stateResult)
        }
    }

    private fun validateState(stateResult: UserState) {
        when (stateResult.state) {
            USER_NOT_EXIST_STATE -> {
                state.value = LoginState.OpenCreateAccountDialog(userName)
            }
            USER_ACTIVATE_STATE -> {
                state.value = LoginState.OpenActivateAccountScreen(R.string.deep_link_activate_account)
            }
            else -> loginUser(stateResult)
        }
    }

    private fun loginUser(stateResult: UserState) {
        launch {
            autUseCase.userLogin(LoginRequest(userName, userPassword)).collect { loginResult ->
                when (loginResult) {
                    is BaseResult.Error ->
                        state.value = LoginState.ErrorLogin(loginResult.rawResponse)
                    is BaseResult.Success -> {
                        when (stateResult.state) {
                            USER_INITIAL_REGISTER_STATE -> {
                                updateRegistrationData()
                                state.value = LoginState.OpenAuthorizationScreen(R.string.deep_link_register_authorization)
                            }
                            USER_COMPLEMENTARY_REGISTER_STATE, USER_COMPLETED_STATE -> {
                                state.value = LoginState.SuccessLogin(LoginFragmentDirections.actionLoginFragmentToHomeNavigation())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateRegistrationData() {
        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    email = userName,
                ), LoginViewModel::class.simpleName
            )
        }
    }

    fun onRegisterClicked() {
        val direction = LoginFragmentDirections.actionLoginFragmentToCreateAccountFragment("")
        state.value = LoginState.OpenRegistrationScreen(direction)
    }

    fun onForgotPasswordClicked() {
        val direction = LoginFragmentDirections.actionLoginFragmentToForgetNavigation()
        state.value = LoginState.OpenForgetPasswordScreen(direction)
    }
}

sealed class LoginState {
    object Init : LoginState()
    data class IsLoading(val isLoading: Boolean) : LoginState()
    data class NoValidData(val resourceId: Int) : LoginState()
    data class SuccessLogin(val direction: NavDirections) : LoginState()
    data class OpenCreateAccountDialog(val email: String) : LoginState()
    data class ErrorLogin(val rawResponse: ErrorGenericResponse) : LoginState()
    data class OpenRegistrationScreen(val direction: NavDirections) : LoginState()
    data class OpenForgetPasswordScreen(val direction: NavDirections) : LoginState()
    data class OpenActivateAccountScreen(@StringRes val deepLink: Int) : LoginState()
    data class OpenAuthorizationScreen(@StringRes val deepLink: Int) : LoginState()
}
