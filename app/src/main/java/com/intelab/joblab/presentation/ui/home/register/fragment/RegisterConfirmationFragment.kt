package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentRegisterConfirmationBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.RegisterConfirmationState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.RegisterConfirmationState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.RegisterConfirmationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterConfirmationFragment : Fragment(R.layout.fragment_register_confirmation) {

    private val viewModel: RegisterConfirmationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentRegisterConfirmationBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.socialMediaFragment,
            RegisterConfirmationFragmentDirections.actionRegisterConfirmationFragmentToSocialMediaFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: RegisterConfirmationState) {
        when (state) {
            is Init -> Unit
            is BackHomeScreen -> navigateSafe(state.direction)
            is BackSocialMediaScreen -> navigatePreviousScreen(state.id, state.directions)
            is IsLoading -> updateProgressDialog(state.isLoading)
            is ErrorStates -> errorValidation(state.rawResponse)
        }
    }
}
