package com.intelab.joblab.presentation.ui.init.register.fragments

import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentActivateAccountBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.ActivateAccountState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.ActivateAccountState.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.ActivateAccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivateAccountFragment : Fragment(R.layout.fragment_activate_account) {

    private val viewModel: ActivateAccountViewModel by viewModels()
    private var clipboardManager: ClipboardManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentActivateAccountBinding.bind(view).also { it.viewModel = viewModel }
        clipboardManager = context?.getSystemService()
        addAndRemoveOnWindowFocusChangeListener { focus: Boolean ->
            if (focus) {
                context?.let { context ->
                    val code = clipboardManager?.getClipboardText(context)
                    viewModel.setActivationCodeFromClipBoard(code)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: ActivateAccountState) {
        when (state) {
            is Init -> Unit
            is OpenAuthorizationScreen -> navigateSafe(state.direction)
            is IsLoading -> updateProgressDialog(state.isLoading)
            is ErrorCreateAccount -> showJoblabDialog { errorDialog(state.rawResponse) }.show()
            is OpenDialog -> simpleDialog(state.title, state.description)
        }
    }
}
