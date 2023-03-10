package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileEconomicBinding
import com.intelab.joblab.presentation.extensions.errorValidation
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileEconomicState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileEconomicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEconomicFragment : Fragment(R.layout.fragment_profile_economic) {

    private val viewModel: ProfileEconomicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileEconomicBinding.bind(view).also { it.viewModel = viewModel }
    }

    private fun handleStateChange(state: ProfileEconomicState) {
        when (state) {
            is ProfileEconomicState.Init -> Unit
            is ProfileEconomicState.OpenProfileAcademicScreen -> navigateSafe(state.direction)
            is ProfileEconomicState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileEconomicState.ExitScreen -> findNavController().navigateUp()
            is ProfileEconomicState.OnError -> errorValidation(state.rawResponse) {
                viewModel.state.value = ProfileEconomicState.Init
            }
        }
    }
}
