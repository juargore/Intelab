package com.intelab.joblab.presentation.ui.init.register.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentCreateAccountBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.showJoblabDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CreateAccountState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CreateAccountState.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CreateAccountViewModel
import com.intelab.joblab.presentation.ui.views.TYPES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentCreateAccountBinding.bind(view).also { it.viewModel = viewModel }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onBackClicked()
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: CreateAccountState) {
        when (state) {
            is Init -> Unit
            is OpenActivateAccountScreen -> navigateSafe(state.direction)
            is BackLoginScreen -> showJoblabDialog {
                setTypeDialog(TYPES.DOUBLE)
                title.text = getString(R.string.tv_create_account)
                message.text = getString(R.string.dialog_error_create_account_description)
                acceptClickListener {
                    findNavController().navigateUp()
                }
                cancelClickListener { }
            }.show()
            is NotValidData -> {
                showJoblabDialog { errorDialogEmpty(getString(state.resourceId)) }.show()
                viewModel.state.value = Init
            }
            is IsLoading -> updateProgressDialog(state.isLoading)
            is ErrorCreateAccount -> showJoblabDialog { errorDialog(state.rawResponse) }.show()
            is OpenDialog -> {
                showJoblabDialog {
                    setTypeDialog(TYPES.DOUBLE)
                    title.text = getString(state.title)
                    message.text = getString(state.description)
                    acceptClickListener {
                        findNavController().navigateUp()
                    }
                    cancelClickListener { }
                }.show()
                viewModel.state.value = Init
            }
        }
    }
}
