package com.intelab.joblab.presentation.ui.init.register.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentPostulationBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.simpleDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PostulationState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PostulationState.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PostulationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostulationFragment : Fragment(R.layout.fragment_postulation) {

    private val viewModel: PostulationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentPostulationBinding.bind(view).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: PostulationState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenBureauScreen -> navigateSafe(state.direction)
            is ErrorJobs -> simpleDialog(R.string.dialog_title_error, R.string.dialog_description_error_generic_message)
        }
    }
}
