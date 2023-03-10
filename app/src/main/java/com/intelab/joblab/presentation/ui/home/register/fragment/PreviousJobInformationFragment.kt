package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentPreviousJobInformationBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.showJoblabDialog
import com.intelab.joblab.presentation.base.utils._monthYearPickerDialogTag
import com.intelab.joblab.presentation.ui.home.register.viewmodels.PreviousJobInformationState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.PreviousJobInformationState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.PreviousJobInformationViewModel
import com.intelab.joblab.presentation.ui.views.MonthYearPickerDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviousJobInformationFragment : Fragment(R.layout.fragment_previous_job_information) {

    private val viewModel: PreviousJobInformationViewModel by viewModels()
    private val args: PreviousJobInformationFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentPreviousJobInformationBinding.bind(view).also { it.viewModel = viewModel }
        viewModel.initialize(args.listOfCompanies)
    }

    override fun onResume() {
        super.onResume()
        resetState()
    }

    private fun resetState() { viewModel.state.value = Init }

    private fun handleStateChange(state: PreviousJobInformationState) {
        when (state) {
            is Init -> Unit
            is BackJobReferencesScreen -> findNavController().navigateUp()
            is OnErrorValidation -> {
                showJoblabDialog { errorDialogEmpty(getString(state.message)) }.show()
                resetState()
            }
            is OpenDatePickerDialog -> {
                MonthYearPickerDialog(state.whichDate).show(
                    this@PreviousJobInformationFragment.childFragmentManager,
                    _monthYearPickerDialogTag
                ); resetState()
            }
        }
    }
}
