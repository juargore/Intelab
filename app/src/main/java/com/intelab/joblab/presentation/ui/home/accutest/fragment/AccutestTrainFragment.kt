package com.intelab.joblab.presentation.ui.home.accutest.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentAccutestTrainBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.simpleDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.home.accutest.adapter.TestTrainingAdapter
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.AccutestTrainViewModel
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.TrainAccutestState
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.TrainAccutestState.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccutestTrainFragment : Fragment(R.layout.fragment_accutest_train) {

    val viewModel: AccutestTrainViewModel by viewModels()
    private lateinit var binding: FragmentAccutestTrainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccutestTrainBinding.bind(view)
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: TrainAccutestState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenInstructionScreen -> navigateSafe(state.direction)
            is OpenVideosScreen -> navigateSafe(state.direction)
            is ErrorStates -> simpleDialog(R.string.dialog_title_error, R.string.dialog_description_error_generic_message)
            is GetAccutestPositions -> {
                val items = (binding.rvTest.adapter as TestTrainingAdapter).phrases
                viewModel.changeAccutestCard(items)
                viewModel.state.value = Init
            }
        }
    }
}
