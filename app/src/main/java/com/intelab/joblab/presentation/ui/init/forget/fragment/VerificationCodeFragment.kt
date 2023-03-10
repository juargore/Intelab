package com.intelab.joblab.presentation.ui.init.forget.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentVerificationCodeBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.showJoblabDialog
import com.intelab.joblab.presentation.extensions.simpleDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.VerificationCodeState
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.VerificationCodeState.*
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.VerificationCodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationCodeFragment : Fragment(R.layout.fragment_verification_code) {

    private val viewModel: VerificationCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentVerificationCodeBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: VerificationCodeState) {
        when (state) {
            is Init -> Unit
            is OpenRecoverPasswordScreen -> navigateSafe(state.directions)
            is ErrorVerificationCode -> showJoblabDialog { errorDialog(state.rawResponse) }.show()
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenDialog -> simpleDialog(state.title, state.description)
        }
    }
}
