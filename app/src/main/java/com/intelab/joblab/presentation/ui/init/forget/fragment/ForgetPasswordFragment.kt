package com.intelab.joblab.presentation.ui.init.forget.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentForgetPasswordBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.showJoblabDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.ForgetPasswordState
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.ForgetPasswordState.*
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.ForgetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment(R.layout.fragment_forget_password) {

    private val viewModel: ForgetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentForgetPasswordBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: ForgetPasswordState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is BackLoginScreen -> findNavController().navigateUp()
            is OpenVerificationCodeScreen -> navigateSafe(state.directions)
            is ErrorForgetPassword -> showJoblabDialog { errorDialog(state.rawResponse) }.show()
            is NoValidData -> {
                showJoblabDialog { errorDialogEmpty(resources.getString(state.resourceId)) }.show()
                viewModel.state.value = Init
            }
        }
    }
}
