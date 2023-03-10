package com.intelab.joblab.presentation.ui.init.register

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.PrivacyConsentResponse
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PrivacyAndConsentState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PrivacyAndConsentViewModel
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
class PrivacyAndConsentViewModelTest {

    private lateinit var catalogUseCase: CatalogUseCase
    private lateinit var savedStateHandle: SavedStateHandle

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        catalogUseCase = mockk()
        savedStateHandle = mockk()
    }

    /*
    @Test
    fun `Function validates privacy note Test`() = runTest {
        // GIVEN
        var eventsCount = 0

        // WHEN
        val response = PrivacyConsentResponse(html = "hi")
        coEvery { catalogUseCase.getPrivacyNotice() } returns flowOf(BaseResult.Success(response))
        every { savedStateHandle.set("type", 1) }
        every { savedStateHandle.get<Int>("type") } returns 1

        val privacyAndConsentViewModel = PrivacyAndConsentViewModel(
            catalogUseCase = catalogUseCase,
            savedStateHandle = savedStateHandle
        )

        // THEN
        privacyAndConsentViewModel.state.test {
            Assert.assertEquals(awaitItem(), PrivacyAndConsentState.Init)
            Assert.assertEquals(awaitItem(), PrivacyAndConsentState.IsLoading(true))
            Assert.assertEquals(awaitItem(), PrivacyAndConsentState.IsLoading(false))
        }
    }*/
}
