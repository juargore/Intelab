package com.intelab.joblab.presentation.ui.init.register.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentAuthorizationBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.ui.init.register.viewmodels.AuthorizationState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.AuthorizationState.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.AuthorizationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationFragment : Fragment(R.layout.fragment_authorization) {

    private val viewModel: AuthorizationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentAuthorizationBinding.bind(view).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: AuthorizationState) {
        when (state) {
            is Init -> Unit
            is OpenPostulationScreen -> navigateSafe(state.direction)
            is OpenPrivacyAndConsentScreen -> navigateSafe(state.directions)
        }
    }
}
