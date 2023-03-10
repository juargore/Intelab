package com.intelab.joblab.presentation.ui.init.splash.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentSplashBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.navigateToDeepLink
import com.intelab.joblab.presentation.base.utils._delay200
import com.intelab.joblab.presentation.extensions.simpleDialog
import com.intelab.joblab.presentation.ui.init.splash.viewmodel.SplashState
import com.intelab.joblab.presentation.ui.init.splash.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentSplashBinding.bind(view).also { it.viewModel = viewModel }
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    lifecycleScope.launch {
                        delay(_delay200)
                        viewModel.initialize()
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = SplashState.Init
    }

    private fun handleStateChange(state: SplashState) {
        when (state) {
            is SplashState.Init -> Unit
            is SplashState.OpenLoginScreen -> navigateSafe(state.direction)
            is SplashState.OpenHomeScreen -> navigateSafe(state.direction)
            is SplashState.OpenOnBoardingScreen -> navigateSafe(state.direction)
            is SplashState.OpenAuthorizationScreen -> navigateToDeepLink(
                getString(state.deepLink).toUri(),
                state.popUpTo,
                state.popUpToInclusive
            )
            is SplashState.OpenDialog ->
                simpleDialog(R.string.dialog_title_error, state.message) {
                    viewModel.clearTokens()
                    viewModel.goToLoginScreen()
                }
        }
    }
}