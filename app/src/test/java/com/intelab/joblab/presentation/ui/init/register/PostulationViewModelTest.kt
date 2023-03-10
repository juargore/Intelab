package com.intelab.joblab.presentation.ui.init.register

import androidx.databinding.library.baseAdapters.BR
import app.cash.turbine.test
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.common.propertyChangedCallback
import com.intelab.joblab.domain.entities.JobPostulation
import com.intelab.joblab.domain.entities.ParentJobUI
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.ui.init.register.fragments.PostulationFragmentDirections
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PostulationState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PostulationViewModel
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
class PostulationViewModelTest {

    private lateinit var catalogUseCase: CatalogUseCase
    private lateinit var databaseUseCase: DatabaseUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        catalogUseCase = mockk()
        databaseUseCase = mockk()
    }

    @Test
    fun `Function validates loadJobsData Test`() = runTest {
        var actionCount = 0
        coEvery { catalogUseCase.getJobs() } returns flowOf(
            BaseResult.Success(listOf(ParentJobUI(header = "A", jobList = mutableListOf())))
        )
        coEvery { databaseUseCase.deleteAllJobPostulation() } returns Unit
        coEvery { databaseUseCase.insertJobPostulation(JobPostulation(id = 1, description = "hi")) }

        val postulationViewModel = PostulationViewModel(
            catalogUseCase = catalogUseCase,
            databaseUseCase = databaseUseCase
        )

        postulationViewModel.state.test {
            Assert.assertEquals(awaitItem(), PostulationState.IsLoading(true))
            Assert.assertEquals(awaitItem(), PostulationState.IsLoading(false))
            postulationViewModel.addOnPropertyChangedCallback(propertyChangedCallback { _ , id ->
                if (id == BR.jobItems) {
                    actionCount++
                    Assert.assertEquals(1, actionCount)
                }
            })
            Assert.assertEquals(postulationViewModel.jobItems.size, 0)
        }
    }

    @Test
    fun `Function validates onNextClicked Test`() = runTest {
        coEvery { catalogUseCase.getJobs() } returns flowOf(
            BaseResult.Success(listOf(ParentJobUI(header = "A", jobList = mutableListOf())))
        )
        coEvery { databaseUseCase.deleteAllJobPostulation() } returns Unit
        coEvery { databaseUseCase.insertJobPostulation(JobPostulation(id = 1, description = "hi")) }

        val postulationViewModel = PostulationViewModel(
            catalogUseCase = catalogUseCase,
            databaseUseCase = databaseUseCase
        )

        postulationViewModel.state.test {
            Assert.assertEquals(awaitItem(), PostulationState.IsLoading(true))
            Assert.assertEquals(awaitItem(), PostulationState.IsLoading(false))
            postulationViewModel.onNextClicked()
            val direction =
                PostulationFragmentDirections.actionPostulationFragmentToPersonalInformationValidateFragment()
            Assert.assertEquals(awaitItem(), PostulationState.OpenBureauScreen(direction))
        }
    }
}
