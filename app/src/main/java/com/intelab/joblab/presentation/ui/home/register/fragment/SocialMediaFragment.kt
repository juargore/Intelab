package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentSocialMediaBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.SocialMediaState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.SocialMediaState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.SocialMediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialMediaFragment : Fragment(R.layout.fragment_social_media) {

    private val viewModel: SocialMediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentSocialMediaBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.jobReferencesFragment,
            SocialMediaFragmentDirections.actionSocialMediaFragmentToJobReferencesFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
        viewModel.getSocialNetworksFromServer()
    }

    private fun handleStateChange(state: SocialMediaState) {
        when (state) {
            is Init -> Unit
            is OpenConfirmationScreen -> navigateSafe(state.direction)
            is BackJobReferencesScreen -> navigatePreviousScreen(state.id, state.directions)
            is IsLoading -> updateProgressDialog(state.isLoading)
        }
    }
}
