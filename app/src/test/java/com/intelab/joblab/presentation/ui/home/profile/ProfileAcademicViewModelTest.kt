package com.intelab.joblab.presentation.ui.home.profile

import com.intelab.joblab.BR
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.common.propertyChangedCallback
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.EducationLvlUI
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAcademicViewModel
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
class ProfileAcademicViewModelTest {

    private lateinit var dbUseCase : DatabaseUseCase
    private lateinit var candidateUseCase : CandidateUseCase
    private lateinit var catalogUseCase : CatalogUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        candidateUseCase = mockk()
        catalogUseCase = mockk()
        dbUseCase = mockk()
    }

    @Test
    fun `Counter of Bindables`() = runTest {
        var eventsCount = 0

        // GIVEN
        coEvery { dbUseCase.getRegistrationData() } returns flowOf(
            ComplementaryRegister(
                firstName = "Arturo",
                otherNames = "Luis",
                fatherLastName = "Gomez",
                motherLastName = "Resendiz"
            )
        )

        val profileAcademicViewModel = ProfileAcademicViewModel(
            dbUseCase = dbUseCase,
            candidateUseCase = candidateUseCase,
            catalogUseCase = catalogUseCase
        )

        // WHEN
        profileAcademicViewModel.addOnPropertyChangedCallback(propertyChangedCallback{ _, id ->
            if (id == BR.userFullName) {
                eventsCount++
            }
            if (eventsCount == 1) {
                // THEN
                Assert.assertEquals(1, eventsCount)
            }
        })
    }

    @Suppress("LocalVariableName")
    @Test
    fun `Counter of EducationLevelList Bindables`() = runTest {
        val TOTAL_TIMES_CALLED = 1
        var eventsCount = 0

        // GIVEN
        coEvery { dbUseCase.getRegistrationData() } returns flowOf(
            ComplementaryRegister(
                firstName = "Juan",
                otherNames = "Arturo",
                fatherLastName = "Gomez",
                motherLastName = "Resendiz"
            )
        )
        coEvery { catalogUseCase.getEducationLevels() } returns flowOf(
            BaseResult.Success(
                listOf(EducationLvlUI(
                    id = 1,
                    educationLvlName = "Test"
                ))
            )
        )

        val profileAcademicViewModel = ProfileAcademicViewModel(
            dbUseCase = dbUseCase,
            candidateUseCase = candidateUseCase,
            catalogUseCase = catalogUseCase
        )

        // WHEN
        profileAcademicViewModel.addOnPropertyChangedCallback(propertyChangedCallback{ _, id ->
            if (id == BR.educationLevelsList) { eventsCount++ }
            if (eventsCount == TOTAL_TIMES_CALLED) {
                Assert.assertEquals(TOTAL_TIMES_CALLED, eventsCount)
            }
        })
    }
}
