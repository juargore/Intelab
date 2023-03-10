package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileJobsBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileJobsState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileJobsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileJobsFragment : Fragment(R.layout.fragment_profile_jobs) {

    private val viewModel: ProfileJobsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileJobsBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = ProfileJobsState.Init
    }

    private fun handleStateChange(state: ProfileJobsState) {
        when (state) {
            is ProfileJobsState.Init -> Unit
            is ProfileJobsState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileJobsState.ErrorPreferableJobs -> errorValidation(state.rawResponse)
            is ProfileJobsState.BackHomeScreen -> findNavController().navigateUp()
            is ProfileJobsState.OpenPersonalProfileScreen -> navigateSafe(state.direction)
            is ProfileJobsState.OpenDialog -> {
                simpleDialog(state.title, state.description)
                viewModel.state.value = ProfileJobsState.Init
            }
        }
    }
}
