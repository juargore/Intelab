package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileAcademicBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAcademicState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAcademicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileAcademicFragment : Fragment(R.layout.fragment_profile_academic) {

    private val viewModel: ProfileAcademicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileAcademicBinding.bind(view).also { it.viewModel = viewModel }
    }

    private fun handleStateChange(state: ProfileAcademicState) {
        when (state) {
            is ProfileAcademicState.Init -> Unit
            is ProfileAcademicState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileAcademicState.OpenProfileJobReferenceScreen -> navigateSafe(state.direction)
            is ProfileAcademicState.ExitScreen -> findNavController().navigateUp()
            is ProfileAcademicState.OnError -> errorValidation(state.rawResponse) {
                viewModel.state.value = ProfileAcademicState.Init
            }
            is ProfileAcademicState.OnErrorValidation -> {
                showJoblabDialog { errorDialogEmpty(getString(state.message)) }.show()
                viewModel.state.value = ProfileAcademicState.Init
            }
        }
    }
}
