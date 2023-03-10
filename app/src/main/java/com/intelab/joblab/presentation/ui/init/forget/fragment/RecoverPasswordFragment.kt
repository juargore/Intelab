package com.intelab.joblab.presentation.ui.init.forget.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentRecoverPasswordBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.showJoblabDialog
import com.intelab.joblab.presentation.extensions.simpleDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.RecoverPasswordState
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.RecoverPasswordState.*
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.RecoverPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverPasswordFragment : Fragment(R.layout.fragment_recover_password) {

    private val viewModel: RecoverPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentRecoverPasswordBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: RecoverPasswordState) {
        when (state) {
            is Init -> Unit
            is OpenLoginScreen -> navigateSafe(state.directions)
            is ErrorRecoverPassword -> showJoblabDialog { errorDialog(state.rawResponse) }.show()
            is IsLoading -> updateProgressDialog(state.isLoading)
            is NotValidData -> {
                showJoblabDialog { errorDialogEmpty(resources.getString(state.resourceId)) }.show()
                viewModel.state.value = Init
            }
            is OpenSuccessDialog ->
                simpleDialog(R.string.dialog_title_forget_password, state.resourceId, _cancelable = false) {
                    val directions = RecoverPasswordFragmentDirections.actionRecoverPasswordFragmenttoLoginFragment()
                    navigateSafe(directions)
                }
        }
    }
}
