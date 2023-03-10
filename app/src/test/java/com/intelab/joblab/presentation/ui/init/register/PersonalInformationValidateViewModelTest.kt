package com.intelab.joblab.presentation.ui.init.register

import android.net.Uri
import androidx.databinding.library.baseAdapters.BR
import app.cash.turbine.test
import com.intelab.joblab.domain.common.propertyChangedCallback
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationValidateState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationValidateViewModel
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
class PersonalInformationValidateViewModelTest {

    private lateinit var dbUseCase: DatabaseUseCase
    private lateinit var candidateUseCase: CandidateUseCase
    private lateinit var authUseCase: AuthUseCase
    private lateinit var personalInformationValidateViewModel
    : PersonalInformationValidateViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        dbUseCase = mockk()
        candidateUseCase = mockk()
        authUseCase = mockk()

        personalInformationValidateViewModel = PersonalInformationValidateViewModel(
            dbUseCase = dbUseCase,
            candidateUseCase = candidateUseCase,
            authUseCase = authUseCase
        )
    }

    @Test
    fun `Function validates getRegistrationData Test`() = runTest {
        val photoUri = mockk<Uri>()
        var counter = 0

        coEvery { dbUseCase.getRegistrationData() } returns flowOf(
            ComplementaryRegister(photoUri = photoUri)
        )
        coEvery { dbUseCase.getAllJobPostulation() } returns flowOf(listOf())

        personalInformationValidateViewModel.state.test {
            Assert.assertEquals(awaitItem(), PersonalInformationValidateState.Init)
            personalInformationValidateViewModel.getDbRegistrationData()
            personalInformationValidateViewModel.addOnPropertyChangedCallback(propertyChangedCallback{ _, id ->
                if (id == BR.jobPostulations) {
                    counter ++
                }
                if (counter == 1) {
                    Assert.assertEquals(1, counter)
                }
            })
        }
    }

}
