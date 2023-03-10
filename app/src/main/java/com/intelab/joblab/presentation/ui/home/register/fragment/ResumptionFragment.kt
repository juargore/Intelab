package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentResumptionBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.ui.home.register.viewmodels.ResumptionState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.ResumptionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResumptionFragment : Fragment(R.layout.fragment_resumption) {

    private val viewModel: ResumptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentResumptionBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = ResumptionState.Init
    }

    private fun handleStateChange(state: ResumptionState) {
        when (state) {
            is ResumptionState.Init -> Unit
            is ResumptionState.BackHomeScreen -> findNavController().navigateUp()
            is ResumptionState.OpenResumptionScreen -> navigateSafe(state.direction)
        }
    }
}
