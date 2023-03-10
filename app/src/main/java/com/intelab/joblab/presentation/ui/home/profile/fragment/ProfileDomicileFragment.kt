package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileDomicileBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileDomicileState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileDomicileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDomicileFragment : Fragment(R.layout.fragment_profile_domicile) {

    private val viewModel: ProfileDomicileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileDomicileBinding.bind(view).also { it.viewModel = viewModel }
    }

    private fun handleStateChange(state: ProfileDomicileState) {
        when (state) {
            is ProfileDomicileState.Init -> Unit
            is ProfileDomicileState.OpenProfileCreditScreen -> navigateSafe(state.direction)
            is ProfileDomicileState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileDomicileState.BackHomeScreen -> findNavController().navigateUp()
            is ProfileDomicileState.ErrorStates -> errorValidation(state.rawResponse)
            is ProfileDomicileState.ShowDialog -> {
                showJoblabDialog { errorDialogEmpty(getString(state.messageId)) }.show()
                viewModel.state.value = ProfileDomicileState.Init
            }
        }
    }
}
