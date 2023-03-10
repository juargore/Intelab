package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileJobReferenceBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileJobReferenceState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileJobReferenceViewModel
import com.intelab.joblab.presentation.ui.views.TYPES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileJobReferenceFragment : Fragment(R.layout.fragment_profile_job_reference) {

    private val viewModel: ProfileJobReferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileJobReferenceBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getJobReferencesFromServer()
        resetState()
    }

    private fun resetState() {
        viewModel.state.value = ProfileJobReferenceState.Init
    }

    private fun handleStateChange(state: ProfileJobReferenceState) {
        when (state) {
            is ProfileJobReferenceState.Init -> Unit
            is ProfileJobReferenceState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileJobReferenceState.OpenProfileSocialMediaScreen -> navigateSafe(state.direction)
            is ProfileJobReferenceState.OpenAddJobReferenceScreen -> navigateSafe(state.direction)
            is ProfileJobReferenceState.OnError -> errorValidation(state.rawResponse)
            is ProfileJobReferenceState.ExitScreen -> findNavController().navigateUp()
            is ProfileJobReferenceState.OnErrorValidation -> {
                showJoblabDialog { errorDialogEmpty(getString(state.message)) }.show()
                resetState()
            }
            is ProfileJobReferenceState.OpenDialog -> showJoblabDialog {
                setTypeDialog(TYPES.DOUBLE)
                title.text =
                    requireContext().resources.getString(R.string.dialog_title_delete_job_reference)
                message.text = getString(state.stringId)
                acceptClickListener {
                    viewModel.deleteJobReference(state.jobReferenceId, true)
                }
                cancelClickListener { }
                resetState()
            }.show()
        }
    }
}
