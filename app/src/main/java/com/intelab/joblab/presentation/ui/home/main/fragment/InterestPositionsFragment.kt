package com.intelab.joblab.presentation.ui.home.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentInteresPositionsBinding
import com.intelab.joblab.presentation.extensions.errorValidation
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.simpleDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.home.main.viewmodels.InterestPositionsViewModel
import com.intelab.joblab.presentation.ui.home.main.viewmodels.InterestState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterestPositionsFragment : Fragment(R.layout.fragment_interes_positions) {

    private val viewModel: InterestPositionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentInteresPositionsBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        resetState()
    }

    private fun resetState() {
        viewModel.state.value = InterestState.Init
    }

    private fun handleStateChange(state: InterestState) {
        when (state) {
            is InterestState.Init -> Unit
            is InterestState.IsLoading -> updateProgressDialog(state.isLoading)
            is InterestState.CloseScreen -> findNavController().navigateUp()
            is InterestState.ErrorPositions -> errorValidation(state.rawResponse) { resetState() }
            is InterestState.ErrorValidation -> {
                simpleDialog(R.string.dialog_title_error, state.message)
                resetState()
            }
        }
    }
}
