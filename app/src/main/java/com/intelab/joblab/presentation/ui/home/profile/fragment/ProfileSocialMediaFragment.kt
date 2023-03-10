package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileSocialMediaBinding
import com.intelab.joblab.presentation.extensions.errorValidation
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileSocialMediaState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileSocialMediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSocialMediaFragment : Fragment(R.layout.fragment_profile_social_media) {

    private val viewModel: ProfileSocialMediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileSocialMediaBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.callInitialService()
    }

    private fun handleStateChange(state: ProfileSocialMediaState) {
        when (state) {
            is ProfileSocialMediaState.Init -> Unit
            is ProfileSocialMediaState.ErrorStates -> errorValidation(state.rawResponse)
            is ProfileSocialMediaState.ExitScreen -> findNavController().navigateUp()
            is ProfileSocialMediaState.IsLoading -> updateProgressDialog(state.isLoading)
        }
    }
}
