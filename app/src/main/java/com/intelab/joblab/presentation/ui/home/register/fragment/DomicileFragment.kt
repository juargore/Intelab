package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentDomicileBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.DomicileState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.DomicileState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.DomicileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DomicileFragment : Fragment(R.layout.fragment_domicile) {

    private val viewModel: DomicileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentDomicileBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.personalInformationPartTwoFragment,
            DomicileFragmentDirections.actionDomicileFragmentToPersonalInformationPartTwoFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: DomicileState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenCreditBureauScreen -> navigateSafe(state.direction)
            is BackPersonalInformationPartTwoScreen -> navigatePreviousScreen(state.id, state.directions)
            is ErrorStates -> simpleDialog(R.string.dialog_title_error, R.string.dialog_description_error_generic_message)
        }
    }
}
