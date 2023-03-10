package com.intelab.joblab.presentation.ui.init.forget

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.common.propertyChangedCallback
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.RecoverPasswordState
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.RecoverPasswordViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecoverPasswordViewModelTest {

    private lateinit var authUseCase: AuthUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var authValidationUseCase: AuthValidationUseCase
    private lateinit var recoverPasswordViewModel: RecoverPasswordViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        authUseCase = mockk()
        savedStateHandle = mockk()
        authValidationUseCase = mockk()

        recoverPasswordViewModel = RecoverPasswordViewModel(
            authUseCase = authUseCase,
            savedStateHandle = savedStateHandle,
            authValidationUseCase = authValidationUseCase
        )
    }

    @Test
    fun `Function throws NoValidData Invalid Password Test`() = runTest {
        // GIVEN
        var counterPasswordMessage = 0
        val password = "12345aa"
        recoverPasswordViewModel.password = password

        // WHEN
        coEvery { authValidationUseCase.isValidPassword(password) } returns false

        recoverPasswordViewModel.addOnPropertyChangedCallback(propertyChangedCallback { _, id ->
            if (id == BR.passwordMessage) {
                counterPasswordMessage++
            }
        })

        recoverPasswordViewModel.onNextClicked()

        // THEN
        delay(3_000)
        Assert.assertEquals(1, counterPasswordMessage)
        Assert.assertTrue(recoverPasswordViewModel.passwordMessage == R.string.error_password_requirements)
    }

    @Suppress("SpellCheckingInspection")
    @Test
    fun `Functiond throws No match Password Test`() = runTest {
        // GIVEN
        var counterPasswordMessage = 0
        val password1 = "ARturo90@"
        val password2 = "ARturo123"
        recoverPasswordViewModel.password = password1
        recoverPasswordViewModel.rePassword = password2

        // WHEN
        coEvery { authValidationUseCase.isValidPassword(password1) } returns true

        recoverPasswordViewModel.addOnPropertyChangedCallback(propertyChangedCallback { _, id ->
            if (id == BR.passwordConfMessage) {
                counterPasswordMessage++
            }
        })

        recoverPasswordViewModel.onNextClicked()

        // THEN
        delay(3_000)
        Assert.assertEquals(1, counterPasswordMessage)
        Assert.assertTrue(recoverPasswordViewModel.passwordConfMessage == R.string.et_message_different_password)
    }

    @Suppress("SpellCheckingInspection")
    @Test
    fun `Function throws ErrorRecoverPassword state Test`() = runTest {
        // GIVEN
        val password1 = "ARturo90@"
        val password2 = "ARturo90@"
        val returHanlde = "Hi"
        val errorResponse = ErrorGenericResponse("", 400, "", "", "")
        recoverPasswordViewModel.password = password1
        recoverPasswordViewModel.rePassword = password2

        // WHEN
        coEvery { authValidationUseCase.isValidPassword(password1) } returns true
        every { savedStateHandle.get<String>("validationCode") } returns returHanlde
        coEvery { authUseCase.resetPassword(password1, password2, returHanlde) } returns flowOf(
            BaseResult.Error(errorResponse))

        // THEN
        recoverPasswordViewModel.state.test {
            Assert.assertEquals(awaitItem(), RecoverPasswordState.Init)
            recoverPasswordViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), RecoverPasswordState.IsLoading(true))
            Assert.assertEquals(awaitItem(), RecoverPasswordState.IsLoading(false))
            Assert.assertEquals(
                awaitItem(), RecoverPasswordState.ErrorRecoverPassword(errorResponse)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Suppress("SpellCheckingInspection")
    @Test
    fun `Function success OpenLoginScreenTest`() = runTest {
        // GIVEN
        val password1 = "ARturo90@"
        val password2 = "ARturo90@"
        val returHanlde = "Hi"
        recoverPasswordViewModel.password = password1
        recoverPasswordViewModel.rePassword = password2

        // WHEN
        coEvery { authValidationUseCase.isValidPassword(password1) } returns true
        every { savedStateHandle.get<String>("validationCode") } returns returHanlde
        coEvery { authUseCase.resetPassword(password1, password2, returHanlde) } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true)))

        // THEN
        recoverPasswordViewModel.state.test {
            Assert.assertEquals(awaitItem(), RecoverPasswordState.Init)
            recoverPasswordViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), RecoverPasswordState.IsLoading(true))
            Assert.assertEquals(awaitItem(), RecoverPasswordState.IsLoading(false))
            Assert.assertEquals(
                awaitItem(), RecoverPasswordState.OpenSuccessDialog(R.string.dialog_message_success_changed_password)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NextBnEnabled values Test`() = runTest {
        // GIVEN
        recoverPasswordViewModel.password = ""
        recoverPasswordViewModel.rePassword = ""

        // THEN
        Assert.assertEquals(false, recoverPasswordViewModel.nextBnEnabled)

        // GIVEN
        recoverPasswordViewModel.password = "123"
        recoverPasswordViewModel.rePassword = "12345"

        // THEN
        Assert.assertEquals(true, recoverPasswordViewModel.nextBnEnabled)
    }
}
