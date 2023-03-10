package com.intelab.joblab.presentation.ui.init.register.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentPrivacyAndConsentBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PrivacyAndConsentState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PrivacyAndConsentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacyAndConsentFragment : Fragment(R.layout.fragment_privacy_and_consent) {

    private val viewModel: PrivacyAndConsentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentPrivacyAndConsentBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = PrivacyAndConsentState.Init
    }

    private fun handleStateChange(state: PrivacyAndConsentState) {
        when (state) {
            is PrivacyAndConsentState.Init -> Unit
            is PrivacyAndConsentState.IsLoading -> updateProgressDialog(state.isLoading)
        }
    }
}
