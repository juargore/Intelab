package com.intelab.joblab.presentation.ui.home.accutest.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentAccutestInstructionBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.AccutestInstructionState
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.AccutestInstructionViewModel

class AccutestInstructionFragment : Fragment(R.layout.fragment_accutest_instruction) {

    private val viewModel: AccutestInstructionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentAccutestInstructionBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = AccutestInstructionState.Init
    }

    private fun handleStateChange(state: AccutestInstructionState) {
        when (state) {
            is AccutestInstructionState.Init -> Unit
            is AccutestInstructionState.OpenTestState -> navigateSafe(state.directions)
        }
    }
}
