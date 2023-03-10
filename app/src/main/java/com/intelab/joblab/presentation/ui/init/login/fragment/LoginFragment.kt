package com.intelab.joblab.presentation.ui.init.login.fragment

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentLoginBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.init.login.viewmodels.LoginState
import com.intelab.joblab.presentation.ui.init.login.viewmodels.LoginViewModel
import com.intelab.joblab.presentation.ui.views.TYPES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentLoginBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = LoginState.Init
    }

    private fun handleStateChange(state: LoginState) {
        when (state) {
            is LoginState.Init -> Unit
            is LoginState.ErrorLogin -> showJoblabDialog { errorDialog(state.rawResponse) }.show()
            is LoginState.SuccessLogin -> navigateSafe(state.direction)
            is LoginState.IsLoading -> updateProgressDialog(state.isLoading)
            is LoginState.OpenRegistrationScreen -> navigateSafe(state.direction)
            is LoginState.OpenForgetPasswordScreen -> navigateSafe(state.direction)
            is LoginState.OpenActivateAccountScreen -> navigateToDeepLink(getString(state.deepLink).toUri())
            is LoginState.OpenAuthorizationScreen -> navigateToDeepLink(getString(state.deepLink).toUri())
            is LoginState.NoValidData -> {
                showJoblabDialog { errorDialogEmpty(getString(state.resourceId)) }.show()
                viewModel.state.value = LoginState.Init
            }
            is LoginState.OpenCreateAccountDialog -> {
                showJoblabDialog {
                    setTypeDialog(TYPES.DOUBLE)
                    title.text = getString(R.string.tv_create_account)
                    message.text = getString(R.string.dialog_message_go_to_create_account, state.email)
                    acceptButton.text = getString(R.string.bn_text_yes)
                    cancelButton.text = getString(R.string.bn_text_no)
                    cancelClickListener { }
                    acceptClickListener {
                        val directions =
                            LoginFragmentDirections.actionLoginFragmentToCreateAccountFragment(state.email)
                        navigateSafe(directions)
                    }
                }.show()
            }
        }
    }
}
