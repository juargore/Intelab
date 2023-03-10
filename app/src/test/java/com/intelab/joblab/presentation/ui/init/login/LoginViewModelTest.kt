package com.intelab.joblab.presentation.ui.init.login

import app.cash.turbine.test
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.TokenResponse
import com.intelab.joblab.domain.entities.UserState
import com.intelab.joblab.domain.entities.requests.LoginRequest
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.ui.init.login.fragment.LoginFragmentDirections
import com.intelab.joblab.presentation.ui.init.login.viewmodels.LoginState
import com.intelab.joblab.presentation.ui.init.login.viewmodels.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var authUseCase : AuthUseCase
    private lateinit var authValidationUseCase : AuthValidationUseCase
    private lateinit var dbUseCase : DatabaseUseCase
    private lateinit var preferencesUseCase : PreferencesUseCase
    private lateinit var loginViewModel: LoginViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        authUseCase = mockk()
        authValidationUseCase = mockk()
        dbUseCase = mockk()
        preferencesUseCase = mockk()

        loginViewModel = LoginViewModel(
            autUseCase = authUseCase,
            authValidationUseCase = authValidationUseCase,
            dbUseCase = dbUseCase,
            preferencesUseCase = preferencesUseCase
        )
    }

    @Test
    fun `Login throws NoValidData Empty state Test`() = runTest {
        // GIVEN
        loginViewModel.userName = ""
        loginViewModel.userPassword = ""

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.NoValidData(R.string.dialog_message_fill_email_password))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login throws NoValidData Invalid Email state Test`() = runTest {
        // GIVEN
        val userEmail = "arturog@bluetrailsoft.com"
        loginViewModel.userName = userEmail
        loginViewModel.userPassword = "password"

        // WHEN
        coEvery { authValidationUseCase.isValidEmail(userEmail) } returns false

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.NoValidData(R.string.et_message_invalid_email))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login throws NoValidData Recruiter state Test`() = runTest {
        // GIVEN
        val userEmail = "arturog@bluetrailsoft.com"
        loginViewModel.userName = userEmail
        loginViewModel.userPassword = "12345"

        val response = flowOf(
            BaseResult.Success(UserState(
                profile = RECRUITER_PROFILE,
                type = "test",
                state = "test"
            ))
        )

        // WHEN
        coEvery { authUseCase.checkForUserState() } returns response
        coEvery { authValidationUseCase.isValidEmail(userEmail) } returns true
        coEvery { preferencesUseCase.saveEmail(userEmail) } returns Unit

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.IsLoading(true))
            assertEquals(awaitItem() , LoginState.IsLoading(false))
            assertEquals(awaitItem() , LoginState.NoValidData(R.string.dialog_login_error_go_to_web_app))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login throws Error Recruiter state Test`() = runTest {
        // GIVEN
        val userEmail = "arturog@bluetrailsoft.com"
        loginViewModel.userName = userEmail
        loginViewModel.userPassword = "12345"

        val errorResponse = ErrorGenericResponse(
            timestamp = "",
            status = 400,
            error = "",
            messageKey = "",
            message = ""
        )

        val response = flowOf(BaseResult.Error(errorResponse))

        // WHEN
        coEvery { authUseCase.checkForUserState() } returns response
        coEvery { authValidationUseCase.isValidEmail(userEmail) } returns true
        coEvery { preferencesUseCase.saveEmail(userEmail) } returns Unit

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.IsLoading(true))
            assertEquals(awaitItem() , LoginState.IsLoading(false))
            assertEquals(awaitItem() , LoginState.ErrorLogin(errorResponse))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login throws OpenCreateAccountDialog state Test`() = runTest {
        // GIVEN
        val userEmail = "arturog@bluetrailsoft.com"
        loginViewModel.userName = userEmail
        loginViewModel.userPassword = "12345"

        val response = flowOf(
            BaseResult.Success(UserState(
                profile = "test",
                type = "test",
                state = USER_NOT_EXIST_STATE
            ))
        )

        // WHEN
        coEvery { authUseCase.checkForUserState() } returns response
        coEvery { authValidationUseCase.isValidEmail(userEmail) } returns true
        coEvery { preferencesUseCase.saveEmail(userEmail) } returns Unit

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.IsLoading(true))
            assertEquals(awaitItem() , LoginState.IsLoading(false))
            assertEquals(awaitItem() , LoginState.OpenCreateAccountDialog(userEmail))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login throws OpenActivateAccountScreen state Test`() = runTest {
        // GIVEN
        val userEmail = "arturog@bluetrailsoft.com"
        loginViewModel.userName = userEmail
        loginViewModel.userPassword = "12345"

        val response = flowOf(
            BaseResult.Success(UserState(
                profile = "test",
                type = "test",
                state = USER_ACTIVATE_STATE
            ))
        )

        // WHEN
        coEvery { authUseCase.checkForUserState() } returns response
        coEvery { authValidationUseCase.isValidEmail(userEmail) } returns true
        coEvery { preferencesUseCase.saveEmail(userEmail) } returns Unit

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.IsLoading(true))
            assertEquals(awaitItem() , LoginState.IsLoading(false))
            assertEquals(awaitItem() , LoginState.OpenActivateAccountScreen(R.string.deep_link_activate_account))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login throws OpenAuthorizationScreen state Test`() = runTest {
        // GIVEN
        val userEmail = "arturog@bluetrailsoft.com"
        val userPass = "ARturo90@"
        loginViewModel.userName = userEmail
        loginViewModel.userPassword = userPass

        val response = flowOf(
            BaseResult.Success(UserState(
                profile = "test",
                type = "test",
                state = USER_INITIAL_REGISTER_STATE
            ))
        )

        // WHEN
        coEvery { authUseCase.checkForUserState() } returns response
        coEvery { authValidationUseCase.isValidEmail(userEmail) } returns true
        coEvery { preferencesUseCase.saveEmail(userEmail) } returns Unit

        coEvery { authUseCase.userLogin(
            LoginRequest(username = userEmail, password = userPass)
        ) } returns flowOf(BaseResult.Success(TokenResponse("","","")))

        coEvery { dbUseCase.insertOrUpdateRegistrationData(
            ComplementaryRegister(email = userEmail),
            className = LoginViewModel::class.simpleName
        ) } returns Unit

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.IsLoading(true))
            assertEquals(awaitItem() , LoginState.IsLoading(false))
            assertEquals(awaitItem() , LoginState.OpenAuthorizationScreen(
                R.string.deep_link_register_authorization
            ))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login throws SuccessLogin state Test`() = runTest {
        // GIVEN
        val userEmail = "arturog@bluetrailsoft.com"
        val userPass = "ARturo90@"
        loginViewModel.userName = userEmail
        loginViewModel.userPassword = userPass

        val response = flowOf(
            BaseResult.Success(UserState(
                profile = "test",
                type = "test",
                state = USER_COMPLEMENTARY_REGISTER_STATE
            ))
        )

        // WHEN
        coEvery { authUseCase.checkForUserState() } returns response
        coEvery { authValidationUseCase.isValidEmail(userEmail) } returns true
        coEvery { preferencesUseCase.saveEmail(userEmail) } returns Unit

        coEvery { authUseCase.userLogin(
            LoginRequest(username = userEmail, password = userPass)
        ) } returns flowOf(BaseResult.Success(TokenResponse("","","")))

        coEvery { dbUseCase.insertOrUpdateRegistrationData(
            ComplementaryRegister(email = userEmail),
            className = LoginViewModel::class.simpleName
        ) } returns Unit

        // THEN
        loginViewModel.state.test {
            assertEquals(awaitItem() , LoginState.Init)
            loginViewModel.onLoginClicked()
            assertEquals(awaitItem() , LoginState.IsLoading(true))
            assertEquals(awaitItem() , LoginState.IsLoading(false))
            val directions = LoginFragmentDirections.actionLoginFragmentToHomeNavigation()
            assertEquals(awaitItem() , LoginState.SuccessLogin(directions))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Suppress("SpellCheckingInspection")
    @Test
    fun `ErrorMessage values Test`() = runTest {
        // GIVEN
        loginViewModel.userPassword = "123"

        // THEN
        assertEquals(loginViewModel.errorMessage , null)

        // GIVEN
        loginViewModel.userPassword = "ARturo10@"

        // THEN
        assertEquals(loginViewModel.errorMessage , null)
    }

}
