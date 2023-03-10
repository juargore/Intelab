package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfileAddJobReferenceBinding
import com.intelab.joblab.presentation.extensions.errorValidation
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.showJoblabDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.base.utils._monthYearPickerDialogTag
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAddJobReferenceState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAddJobReferenceViewModel
import com.intelab.joblab.presentation.ui.views.MonthYearPickerDialogProfile
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileAddJobReferenceFragment : Fragment(R.layout.fragment_profile_add_job_reference) {

    private val viewModel: ProfileAddJobReferenceViewModel by viewModels()
    private val args: ProfileAddJobReferenceFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentProfileAddJobReferenceBinding.bind(view).also { it.viewModel = viewModel }
        viewModel.initialize(args.referenceJobId, args.selectedJob, args.listOfCompanies)
    }

    private fun handleStateChange(state: ProfileAddJobReferenceState) {
        when (state) {
            is ProfileAddJobReferenceState.Init -> Unit
            is ProfileAddJobReferenceState.OpenDatePickerDialog -> {
                MonthYearPickerDialogProfile(state.whichDate).show(
                    this@ProfileAddJobReferenceFragment.childFragmentManager,
                    _monthYearPickerDialogTag
                ); viewModel.state.value = ProfileAddJobReferenceState.Init
            }
            is ProfileAddJobReferenceState.BackJobReferencesScreen -> findNavController().navigateUp()
            is ProfileAddJobReferenceState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfileAddJobReferenceState.OnError -> errorValidation(state.rawResponse) {
                viewModel.state.value = ProfileAddJobReferenceState.Init
            }
            is ProfileAddJobReferenceState.OnErrorValidation -> {
                showJoblabDialog { errorDialogEmpty(getString(state.message)) }.show()
                viewModel.state.value = ProfileAddJobReferenceState.Init
            }
        }
    }
}
