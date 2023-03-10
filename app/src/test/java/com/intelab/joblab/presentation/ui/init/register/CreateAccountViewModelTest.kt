package com.intelab.joblab.presentation.ui.init.register

import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.common.propertyChangedCallback
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import com.intelab.joblab.domain.entities.UserState
import com.intelab.joblab.domain.entities.requests.SignUpRequest
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.ui.init.register.fragments.CreateAccountFragmentDirections
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CreateAccountState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CreateAccountViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAccountViewModelTest {

    private lateinit var dbUseCase: DatabaseUseCase
    private lateinit var preferencesUseCase: PreferencesUseCase
    private lateinit var authValidationUseCase: AuthValidationUseCase
    private lateinit var authUseCase: AuthUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        dbUseCase = mockk()
        preferencesUseCase = mockk()
        authValidationUseCase = mockk()
        authUseCase = mockk()
        savedStateHandle = mockk()
    }

    @Test
    fun `Function validates onBackClicked Test`() = runTest {
        every { savedStateHandle.get<String>("email") } returns "test@gmail.com"

        val createAccountViewModel = CreateAccountViewModel(
            dbUseCase = dbUseCase,
            preferencesUseCase = preferencesUseCase,
            authValidationUseCase = authValidationUseCase,
            authUseCase = authUseCase,
            savedStateHandle = savedStateHandle
        )

        createAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), CreateAccountState.Init)
            createAccountViewModel.onBackClicked()
            Assert.assertEquals(awaitItem(), CreateAccountState.OpenDialog(
                R.string.dialog_title_create_account_cancel,
                R.string.dialog_description_create_account_cancel
            ))
        }
    }

    @Test
    fun `Function invalid email and password Test`() = runTest {
        var counter = 0
        val wrongEmail = "test@i.c"
        val cPassword = "Hello10@"

        every { savedStateHandle.get<String>("email") } returns wrongEmail
        coEvery { authValidationUseCase.isValidEmail(wrongEmail) } returns false
        coEvery { authValidationUseCase.isValidPassword(cPassword) } returns false

        val createAccountViewModel = CreateAccountViewModel(
            dbUseCase = dbUseCase,
            preferencesUseCase = preferencesUseCase,
            authValidationUseCase = authValidationUseCase,
            authUseCase = authUseCase,
            savedStateHandle = savedStateHandle
        )

        createAccountViewModel.password = cPassword
        createAccountViewModel.confirmPassword = "hi"

        createAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), CreateAccountState.Init)
            createAccountViewModel.onNextClicked()
            createAccountViewModel.addOnPropertyChangedCallback(propertyChangedCallback { _, id ->
                if (id == BR.emailMessage) {
                    counter++
                }
                if (counter == 1) {
                    Assert.assertEquals(1, counter)
                }
            })
        }
    }

    @Test
    fun `Function validates NotValidData Test`() = runTest {
        val goodEmail = "test.here@gmail.com"
        val cPassword = "Hello10@"

        every { savedStateHandle.get<String>("email") } returns goodEmail
        coEvery { authValidationUseCase.isValidEmail(goodEmail) } returns true
        coEvery { authValidationUseCase.isValidPassword(cPassword) } returns true
        coEvery { preferencesUseCase.saveEmail(goodEmail) } returns Unit
        coEvery { authUseCase.checkForUserState() } returns flowOf(
            BaseResult.Success(UserState(profile = RECRUITER_PROFILE, "", ""))
        )

        val createAccountViewModel = CreateAccountViewModel(
            dbUseCase = dbUseCase,
            preferencesUseCase = preferencesUseCase,
            authValidationUseCase = authValidationUseCase,
            authUseCase = authUseCase,
            savedStateHandle = savedStateHandle
        )

        createAccountViewModel.password = cPassword
        createAccountViewModel.confirmPassword = cPassword

        createAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), CreateAccountState.Init)
            createAccountViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(false))
            Assert.assertEquals(awaitItem(), CreateAccountState.NotValidData(R.string.dialog_create_account_error_try_another_email))
        }
    }

    @Test
    fun `Function validates OpenActivateAccountScreen Test`() = runTest {
        val goodEmail = "test.here@gmail.com"
        val cPassword = "Hello10@"

        every { savedStateHandle.get<String>("email") } returns goodEmail
        coEvery { authValidationUseCase.isValidEmail(goodEmail) } returns true
        coEvery { authValidationUseCase.isValidPassword(cPassword) } returns true
        coEvery { preferencesUseCase.saveEmail(goodEmail) } returns Unit
        coEvery { authUseCase.checkForUserState() } returns flowOf(
            BaseResult.Success(UserState(profile = "", "", state = USER_NOT_EXIST_STATE))
        )
        coEvery { dbUseCase.insertOrUpdateRegistrationData(
            ComplementaryRegister(email = goodEmail),
            CreateAccountViewModel::class.simpleName)
        } returns Unit
        coEvery { authUseCase.userSignup(SignUpRequest(goodEmail, cPassword, cPassword)) } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true))
        )

        val createAccountViewModel = CreateAccountViewModel(
            dbUseCase = dbUseCase,
            preferencesUseCase = preferencesUseCase,
            authValidationUseCase = authValidationUseCase,
            authUseCase = authUseCase,
            savedStateHandle = savedStateHandle
        )

        createAccountViewModel.password = cPassword
        createAccountViewModel.confirmPassword = cPassword

        createAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), CreateAccountState.Init)
            createAccountViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(false))
            val directions =
                CreateAccountFragmentDirections.actionCreateAccountFragmentToActivateAccountFragment()
            Assert.assertEquals(awaitItem(),
                CreateAccountState.OpenActivateAccountScreen(directions)
            )
        }
    }

    @Test
    fun `Function validates ErrorCreateAccount Test`() = runTest {
        val goodEmail = "test.here@gmail.com"
        val cPassword = "Hello10@"

        every { savedStateHandle.get<String>("email") } returns goodEmail
        coEvery { authValidationUseCase.isValidEmail(goodEmail) } returns true
        coEvery { authValidationUseCase.isValidPassword(cPassword) } returns true
        coEvery { preferencesUseCase.saveEmail(goodEmail) } returns Unit
        coEvery { authUseCase.checkForUserState() } returns flowOf(
            BaseResult.Success(UserState(profile = "", "", state = USER_ACTIVATE_STATE))
        )
        coEvery { dbUseCase.insertOrUpdateRegistrationData(
            ComplementaryRegister(email = goodEmail),
            CreateAccountViewModel::class.simpleName)
        } returns Unit

        coEvery { authUseCase.userSignup(SignUpRequest(goodEmail, cPassword, cPassword)) } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true))
        )

        val createAccountViewModel = CreateAccountViewModel(
            dbUseCase = dbUseCase,
            preferencesUseCase = preferencesUseCase,
            authValidationUseCase = authValidationUseCase,
            authUseCase = authUseCase,
            savedStateHandle = savedStateHandle
        )

        createAccountViewModel.password = cPassword
        createAccountViewModel.confirmPassword = cPassword

        createAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), CreateAccountState.Init)
            createAccountViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(false))
            val directions =
                CreateAccountFragmentDirections.actionCreateAccountFragmentToActivateAccountFragment()
            Assert.assertEquals(awaitItem(), CreateAccountState.OpenActivateAccountScreen(directions))
        }
    }

    @Test
    fun `Function validates BackLoginScreen Test`() = runTest {
        val goodEmail = "test.here@gmail.com"
        val cPassword = "Hello10@"

        every { savedStateHandle.get<String>("email") } returns goodEmail
        coEvery { authValidationUseCase.isValidEmail(goodEmail) } returns true
        coEvery { authValidationUseCase.isValidPassword(cPassword) } returns true
        coEvery { preferencesUseCase.saveEmail(goodEmail) } returns Unit
        coEvery { authUseCase.checkForUserState() } returns flowOf(
            BaseResult.Success(UserState(profile = "", "", state = RECRUITER_PROFILE))
        )

        val createAccountViewModel = CreateAccountViewModel(
            dbUseCase = dbUseCase,
            preferencesUseCase = preferencesUseCase,
            authValidationUseCase = authValidationUseCase,
            authUseCase = authUseCase,
            savedStateHandle = savedStateHandle
        )

        createAccountViewModel.password = cPassword
        createAccountViewModel.confirmPassword = cPassword

        createAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), CreateAccountState.Init)
            createAccountViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), CreateAccountState.IsLoading(false))
            Assert.assertEquals(awaitItem(), CreateAccountState.BackLoginScreen)
        }
    }

}
