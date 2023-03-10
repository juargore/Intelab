package com.intelab.joblab.presentation.ui.init.register

import app.cash.turbine.test
import com.intelab.joblab.presentation.ui.init.register.fragments.AuthorizationFragmentDirections
import com.intelab.joblab.presentation.ui.init.register.viewmodels.AuthorizationState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.AuthorizationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorizationViewModelTest {

    private lateinit var authorizationViewModel: AuthorizationViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        authorizationViewModel = AuthorizationViewModel()
    }

    @Test
    fun `Function validates onNextClicked Test`() = runTest {

        authorizationViewModel.state.test {
            Assert.assertEquals(awaitItem(), AuthorizationState.Init)
            authorizationViewModel.onNextClicked()
            val direction =
                AuthorizationFragmentDirections.actionAuthorizationFragmentToPersonalInformationFragment()
            Assert.assertEquals(awaitItem(), AuthorizationState.OpenPostulationScreen(direction))
        }
    }

    @Test
    fun `Function validates onPrivacyLinkClicked Test`() = runTest {

        authorizationViewModel.state.test {
            Assert.assertEquals(awaitItem(), AuthorizationState.Init)
            authorizationViewModel.onPrivacyLinkClicked()
            val direction =
                AuthorizationFragmentDirections.actionAuthorizationFragmentToPrivacityAndConsentFragment(1)
            Assert.assertEquals(awaitItem(), AuthorizationState.OpenPrivacyAndConsentScreen(direction))
        }
    }

    @Test
    fun `Function validates onConsentLinkClicked Test`() = runTest {

        authorizationViewModel.state.test {
            Assert.assertEquals(awaitItem(), AuthorizationState.Init)
            authorizationViewModel.onConsentLinkClicked()
            val direction =
                AuthorizationFragmentDirections.actionAuthorizationFragmentToPrivacityAndConsentFragment(2)
            Assert.assertEquals(awaitItem(), AuthorizationState.OpenPrivacyAndConsentScreen(direction))
        }
    }
}
