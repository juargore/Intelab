package com.intelab.joblab.presentation.ui.init.forget

import app.cash.turbine.test
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.auth.AuthValidationUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.ui.init.forget.fragment.ForgetPasswordFragmentDirections
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.ForgetPasswordState
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.ForgetPasswordViewModel
import io.mockk.coEvery
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
class ForgetPasswordViewModelTest {

    private lateinit var authUseCase : AuthUseCase
    private lateinit var authValidationUseCase : AuthValidationUseCase
    private lateinit var preferencesUseCase : PreferencesUseCase
    private lateinit var forgetPasswordViewModel : ForgetPasswordViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        authUseCase = mockk()
        authValidationUseCase = mockk()
        preferencesUseCase = mockk()

        forgetPasswordViewModel = ForgetPasswordViewModel(
            authUseCase = authUseCase,
            authValidationUseCase = authValidationUseCase,
            preferencesUseCase = preferencesUseCase
        )
    }

    @Test
    fun `Function throws NoValidData Invalid Email Test`() = runTest {
        // GIVEN
        val email = "test@"
        forgetPasswordViewModel.email = email

        // WHEN
        coEvery { authValidationUseCase.isValidEmail(email) } returns false

        // THEN
        forgetPasswordViewModel.state.test {
            Assert.assertEquals(awaitItem(), ForgetPasswordState.Init)
            forgetPasswordViewModel.onNextClicked()
            Assert.assertEquals(
                awaitItem(),
                ForgetPasswordState.NoValidData(R.string.et_message_invalid_email)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Function throws ErrorForgetPassword Test`() = runTest {
        // GIVEN
        val email = "arturog@bluetrailsoft.com"
        val errorResponse = ErrorGenericResponse("", 500, "", "", "")
        forgetPasswordViewModel.email = email

        // WHEN
        coEvery { authValidationUseCase.isValidEmail(email) } returns true
        coEvery { preferencesUseCase.saveEmail(email) } returns Unit
        coEvery { authUseCase.sendEmailRecoveryCode(email) } returns flowOf(
            BaseResult.Error(errorResponse)
        )

        // THEN
        forgetPasswordViewModel.state.test {
            Assert.assertEquals(awaitItem(), ForgetPasswordState.Init)
            forgetPasswordViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), ForgetPasswordState.IsLoading(true))
            Assert.assertEquals(awaitItem(), ForgetPasswordState.IsLoading(false))
            Assert.assertEquals(
                awaitItem(),
                ForgetPasswordState.ErrorForgetPassword(errorResponse)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Function success OpenVerificationCodeScreen Test`() = runTest {
        // GIVEN
        val email = "arturog@bluetrailsoft.com"
        forgetPasswordViewModel.email = email

        // WHEN
        coEvery { authValidationUseCase.isValidEmail(email) } returns true
        coEvery { preferencesUseCase.saveEmail(email) } returns Unit
        coEvery { authUseCase.sendEmailRecoveryCode(email) } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true))
        )

        // THEN
        forgetPasswordViewModel.state.test {
            Assert.assertEquals(awaitItem(), ForgetPasswordState.Init)
            forgetPasswordViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), ForgetPasswordState.IsLoading(true))
            Assert.assertEquals(awaitItem(), ForgetPasswordState.IsLoading(false))
            val directions =
                ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToVerificationCodeFragment()
            Assert.assertEquals(
                awaitItem(),
                ForgetPasswordState.OpenVerificationCodeScreen(directions)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NextBnEnabled values Test`() = runTest {
        // GIVEN
        forgetPasswordViewModel.email = ""

        // THEN
        Assert.assertEquals(false, forgetPasswordViewModel.nextBnEnabled)

        // GIVEN
        forgetPasswordViewModel.email = "notempty@gmail.com"

        // THEN
        Assert.assertEquals(true, forgetPasswordViewModel.nextBnEnabled)
    }
}
