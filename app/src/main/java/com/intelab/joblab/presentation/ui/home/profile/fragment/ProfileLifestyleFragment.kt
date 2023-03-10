package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileLifestyleBinding
import com.intelab.joblab.presentation.extensions.errorValidation
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileLifestyleState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileLifestyleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileLifestyleFragment : Fragment(R.layout.fragment_profile_lifestyle) {

    private val viewModel: ProfileLifestyleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileLifestyleBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = ProfileLifestyleState.Init
    }

    private fun handleStateChange(state: ProfileLifestyleState) {
        when (state) {
            is ProfileLifestyleState.Init -> Unit
            is ProfileLifestyleState.OpenProfileEconomicScreen -> navigateSafe(state.direction)
            is ProfileLifestyleState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileLifestyleState.BackHomeScreen -> findNavController().navigateUp()
            is ProfileLifestyleState.ErrorStates -> errorValidation(state.rawResponse)
        }
    }
}
