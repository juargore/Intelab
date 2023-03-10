package com.intelab.joblab.presentation.ui.init.register

import app.cash.turbine.test
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.ui.init.register.fragments.PersonalInformationFragmentDirections
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationViewModel
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
class PersonalInformationViewModelTest {

    private lateinit var dbUseCase: DatabaseUseCase
    private lateinit var authUseCase: AuthUseCase
    private lateinit var candidateUseCase: CandidateUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        dbUseCase = mockk()
        authUseCase = mockk()
        candidateUseCase = mockk()
    }

    @Test
    fun `Function validates onSavedClicked Test`() = runTest {
        val register = ComplementaryRegister()
        coEvery { dbUseCase.getRegistrationData() } returns flowOf(
            ComplementaryRegister(email = "hello@gmail.com")
        )
        coEvery { dbUseCase.insertOrUpdateRegistrationData(register, PersonalInformationViewModel::class.simpleName) } returns Unit
        val personalInformationViewModel = PersonalInformationViewModel(
            dbUseCase = dbUseCase,
            authUseCase = authUseCase,
            candidateUseCase = candidateUseCase
        )

        personalInformationViewModel.state.test {
            Assert.assertEquals(awaitItem(), PersonalInformationState.Init)
            personalInformationViewModel.onSavedClicked()
            val directions =
                PersonalInformationFragmentDirections.actionPersonalInformationValidateFragmentToPostulationFragment()
            Assert.assertEquals(awaitItem(), PersonalInformationState.OpenPersonalInformationValidateScreen(directions))
        }
    }

    @Test
    fun `Function validates onCancelClicked Test`() = runTest {
        coEvery { dbUseCase.getRegistrationData() } returns flowOf(
            ComplementaryRegister(email = "hello@gmail.com")
        )
        val personalInformationViewModel = PersonalInformationViewModel(
            dbUseCase = dbUseCase,
            authUseCase = authUseCase,
            candidateUseCase = candidateUseCase
        )

        personalInformationViewModel.state.test {
            Assert.assertEquals(awaitItem(), PersonalInformationState.Init)
            personalInformationViewModel.onCancelClicked()
            Assert.assertEquals(awaitItem(), PersonalInformationState.BackAuthorizationScreen)
        }
    }

    @Test
    fun `Function validates onPhotoClicked Test`() = runTest {
        coEvery { dbUseCase.getRegistrationData() } returns flowOf(
            ComplementaryRegister(email = "hello@gmail.com")
        )
        val personalInformationViewModel = PersonalInformationViewModel(
            dbUseCase = dbUseCase,
            authUseCase = authUseCase,
            candidateUseCase = candidateUseCase
        )

        personalInformationViewModel.state.test {
            Assert.assertEquals(awaitItem(), PersonalInformationState.Init)
            personalInformationViewModel.onPhotoClicked()
            Assert.assertEquals(awaitItem(), PersonalInformationState.OpenBottomSheetDialog)
        }
    }

    @Test
    fun `Function validates button enabled Test`() = runTest {
        coEvery { dbUseCase.getRegistrationData() } returns flowOf(
            ComplementaryRegister(email = "hello@gmail.com")
        )
        val personalInformationViewModel = PersonalInformationViewModel(
            dbUseCase = dbUseCase,
            authUseCase = authUseCase,
            candidateUseCase = candidateUseCase
        )

        personalInformationViewModel.foreign = true
        personalInformationViewModel.passport = ""

        Assert.assertEquals(false, personalInformationViewModel.nextButtonEnabled)

        personalInformationViewModel.foreign = false
        personalInformationViewModel.curp = "no_valid_curp"

        Assert.assertEquals(false, personalInformationViewModel.nextButtonEnabled)

        personalInformationViewModel.foreign = false
        personalInformationViewModel.curp = "GORJ910624HJCMSN07"
        personalInformationViewModel.email = "testing@gmail.com"
        personalInformationViewModel.nationality = "Nacional"
        personalInformationViewModel.names = "John"
        personalInformationViewModel.lastName = "Doe"
        personalInformationViewModel.phone = "3322443311"

        Assert.assertEquals(true, personalInformationViewModel.nextButtonEnabled)
    }
}
