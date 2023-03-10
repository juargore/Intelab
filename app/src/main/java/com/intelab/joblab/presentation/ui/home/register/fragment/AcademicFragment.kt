package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentAcademicBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.AcademicState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.AcademicState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.AcademicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AcademicFragment : Fragment(R.layout.fragment_academic) {

    private val viewModel: AcademicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentAcademicBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.economicFragment,
            AcademicFragmentDirections.actionAcademicFragmentToEconomicFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: AcademicState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenJobReferenceScreen -> navigateSafe(state.direction)
            is BackEconomicScreen -> navigatePreviousScreen(state.id, state.directions)
            is ErrorStates -> simpleDialog(R.string.dialog_title_error, R.string.dialog_description_error_generic_message)
        }
    }
}
