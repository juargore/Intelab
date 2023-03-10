package com.intelab.joblab.presentation.ui.init.register

import app.cash.turbine.test
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.SuccessGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.presentation.ui.init.register.fragments.ActivateAccountFragmentDirections
import com.intelab.joblab.presentation.ui.init.register.viewmodels.ActivateAccountState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.ActivateAccountViewModel
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
class ActivateAccountViewModelTest {

    private lateinit var authUseCase : AuthUseCase
    private lateinit var activateAccountViewModel: ActivateAccountViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        authUseCase = mockk()
        activateAccountViewModel = ActivateAccountViewModel(
            authUseCase = authUseCase
        )
    }

    @Test
    fun `Function returns success when compares code Test`() = runTest {
        // GIVEN
        activateAccountViewModel.firstChar = "A"
        activateAccountViewModel.secondChar = "B"
        activateAccountViewModel.thirdChar = "0"
        activateAccountViewModel.fourthChar = "W"
        activateAccountViewModel.fifthChar = "K"
        activateAccountViewModel.sixthChar = "4"

        val code = "${activateAccountViewModel.firstChar}${activateAccountViewModel.secondChar}" +
                "${activateAccountViewModel.thirdChar}${activateAccountViewModel.fourthChar}" +
                "${activateAccountViewModel.fifthChar}${activateAccountViewModel.sixthChar}"

        // WHEN
        coEvery { authUseCase.compareAndVerifyActivationCode(code) } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true))
        )

        // THEN
        activateAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), ActivateAccountState.Init)
            activateAccountViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(false))
            val direction =
                ActivateAccountFragmentDirections.actionActivateAccountFragmentToAuthorizationFragment()
            Assert.assertEquals(awaitItem(), ActivateAccountState.OpenAuthorizationScreen(direction))
        }
    }

    @Test
    fun `Function fails when compares code Test`() = runTest {
        activateAccountViewModel.firstChar = "0"
        activateAccountViewModel.secondChar = "0"
        activateAccountViewModel.thirdChar = "0"
        activateAccountViewModel.fourthChar = "0"
        activateAccountViewModel.fifthChar = "0"
        activateAccountViewModel.sixthChar = "0"

        val code = "${activateAccountViewModel.firstChar}${activateAccountViewModel.secondChar}" +
                "${activateAccountViewModel.thirdChar}${activateAccountViewModel.fourthChar}" +
                "${activateAccountViewModel.fifthChar}${activateAccountViewModel.sixthChar}"
        val errorResponse = ErrorGenericResponse("", 500, "", "", "")

        // WHEN
        coEvery { authUseCase.compareAndVerifyActivationCode(code) } returns flowOf(
            BaseResult.Error(errorResponse)
        )

        // THEN
        activateAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), ActivateAccountState.Init)
            activateAccountViewModel.onNextClicked()
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(false))
            Assert.assertEquals(awaitItem(), ActivateAccountState.ErrorCreateAccount(errorResponse))
        }
    }

    @Test
    fun `Function returns success when sends code Test`() = runTest {
        // WHEN
        coEvery { authUseCase.sendActivationCode() } returns flowOf(
            BaseResult.Success(SuccessGenericResponse(true))
        )

        // THEN
        activateAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), ActivateAccountState.Init)
            activateAccountViewModel.onSendActivateCodeClicked()
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(false))
            Assert.assertEquals(awaitItem(), ActivateAccountState.OpenDialog(
                R.string.dialog_title_activate_account,
                R.string.dialog_description_resend_code
            ))
        }
    }

    @Test
    fun `Function fails when sends code Test`() = runTest {
        // GIVEN
        val errorResponse = ErrorGenericResponse("", 500, "", "", "")

        // WHEN
        coEvery { authUseCase.sendActivationCode() } returns flowOf(
            BaseResult.Error(errorResponse)
        )

        // THEN
        activateAccountViewModel.state.test {
            Assert.assertEquals(awaitItem(), ActivateAccountState.Init)
            activateAccountViewModel.onSendActivateCodeClicked()
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(true))
            Assert.assertEquals(awaitItem(), ActivateAccountState.IsLoading(false))
            Assert.assertEquals(awaitItem(), ActivateAccountState.ErrorCreateAccount(errorResponse))
        }
    }

    @Test
    fun `Function validates activation code Test`() = runTest {
        activateAccountViewModel.setActivationCodeFromClipBoard("123456")
        Assert.assertEquals(activateAccountViewModel.firstChar, "1")
        Assert.assertEquals(activateAccountViewModel.secondChar, "2")
        Assert.assertEquals(activateAccountViewModel.thirdChar, "3")
        Assert.assertEquals(activateAccountViewModel.fourthChar, "4")
        Assert.assertEquals(activateAccountViewModel.fifthChar, "5")
        Assert.assertEquals(activateAccountViewModel.sixthChar, "6")
    }

    @Test
    fun `Function validates button enabled Test`() = runTest {
        activateAccountViewModel.firstChar = "0"
        activateAccountViewModel.secondChar = "0"
        activateAccountViewModel.thirdChar = "0"
        activateAccountViewModel.fourthChar = "0"
        activateAccountViewModel.fifthChar = "0"
        activateAccountViewModel.sixthChar = "0"

        Assert.assertEquals(true, activateAccountViewModel.nextButtonEnabled)

        activateAccountViewModel.sixthChar = ""

        Assert.assertEquals(false, activateAccountViewModel.nextButtonEnabled)
    }
}
