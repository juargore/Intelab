package com.intelab.joblab.presentation.ui.init.forget

import app.cash.turbine.test
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.ui.init.forget.fragment.VerificationCodeFragmentDirections
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.VerificationCodeState
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.VerificationCodeViewModel
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
class VerificationCodeViewModelTest {

    private lateinit var authUseCase: AuthUseCase
    private lateinit var preferencesUseCase: PreferencesUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        authUseCase = mockk()
        preferencesUseCase = mockk()
    }

    @Test
    fun `Function throws ErrorVerificationCode state Test`() = runTest {
        // GIVEN
        val verificationCode = "TEST123"
        val errorResponse = ErrorGenericResponse("", 400, "", "", "")

        // WHEN
        coEvery { preferencesUseCase.getEmail() } returns "email@bluetrailsoft.com"
        coEvery { authUseCase.compareAndVerifyPasswordRecoveryCode(verificationCode) } returns flowOf(
            BaseResult.Error(errorResponse)
        )

        val verificationCodeViewModel = VerificationCodeViewModel(
            authUseCase = authUseCase,
            preferencesUseCase = preferencesUseCase
        ).also {
            it.verificationCode = verificationCode
        }

        // THEN
        verificationCodeViewModel.state.test {
            Assert.assertEquals(awaitItem(), VerificationCodeState.Init)
            verificationCodeViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), VerificationCodeState.IsLoading(true))
            Assert.assertEquals(awaitItem(), VerificationCodeState.IsLoading(false))
            Assert.assertEquals(
                awaitItem(), VerificationCodeState.ErrorVerificationCode(errorResponse)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Function success OpenRecoverPasswordScreen state Test`() = runTest {
        // GIVEN
        val verificationCode = "TEST123"

        // WHEN
        coEvery { preferencesUseCase.getEmail() } returns "email@bluetrailsoft.com"
        coEvery { authUseCase.compareAndVerifyPasswordRecoveryCode(verificationCode) } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true))
        )

        val verificationCodeViewModel = VerificationCodeViewModel(
            authUseCase = authUseCase,
            preferencesUseCase = preferencesUseCase
        ).also {
            it.verificationCode = verificationCode
        }

        // THEN
        verificationCodeViewModel.state.test {
            Assert.assertEquals(awaitItem(), VerificationCodeState.Init)
            verificationCodeViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), VerificationCodeState.IsLoading(true))
            Assert.assertEquals(awaitItem(), VerificationCodeState.IsLoading(false))
            val directions =
                VerificationCodeFragmentDirections.actionVerificationCodeFragmentToRecoverPasswordFragment(
                    verificationCode
                )
            Assert.assertEquals(
                awaitItem(), VerificationCodeState.OpenRecoverPasswordScreen(directions)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Function success Resend code state Test`() = runTest {
        // GIVEN
        val email = "test@bluetrailsoft.com"

        coEvery { preferencesUseCase.getEmail() } returns email

        val verificationCodeViewModel = VerificationCodeViewModel(
            authUseCase = authUseCase,
            preferencesUseCase = preferencesUseCase
        )

        // WHEN
        coEvery { authUseCase.sendEmailRecoveryCode(email) } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true))
        )

        // THEN
        verificationCodeViewModel.state.test {
            Assert.assertEquals(awaitItem(), VerificationCodeState.Init)
            verificationCodeViewModel.onResentCodeClicked()
            Assert.assertEquals(awaitItem(), VerificationCodeState.IsLoading(true))
            Assert.assertEquals(awaitItem(), VerificationCodeState.IsLoading(false))
            Assert.assertEquals(
                awaitItem(), VerificationCodeState.OpenDialog(
                    R.string.dialog_title_recover_password,
                    R.string.dialog_description_resend_code
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NextBnEnabled values Test`() = runTest {
        // GIVEN
        val email = "test@bluetrailsoft.com"

        coEvery { preferencesUseCase.getEmail() } returns email

        val verificationCodeViewModel = VerificationCodeViewModel(
            authUseCase = authUseCase,
            preferencesUseCase = preferencesUseCase
        )
        verificationCodeViewModel.verificationCode = ""

        // THEN
        Assert.assertEquals(false, verificationCodeViewModel.nextBnEnabled)

        // GIVEN
        verificationCodeViewModel.verificationCode = "TEST123"

        // THEN
        Assert.assertEquals(true, verificationCodeViewModel.nextBnEnabled)
    }
}
