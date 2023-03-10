package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileCreditBinding
import com.intelab.joblab.presentation.extensions.errorValidation
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileCreditState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileCreditViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileCreditFragment : Fragment(R.layout.fragment_profile_credit) {

    private val viewModel: ProfileCreditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileCreditBinding.bind(view).also { it.viewModel = viewModel }
    }

    private fun handleStateChange(state: ProfileCreditState) {
        when (state) {
            is ProfileCreditState.Init -> Unit
            is ProfileCreditState.OpenProfileLifestyleScreen -> navigateSafe(state.direction)
            is ProfileCreditState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileCreditState.BackHomeScreen -> findNavController().navigateUp()
            is ProfileCreditState.ErrorStates -> errorValidation(state.rawResponse)
        }
    }
}
