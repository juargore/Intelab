package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentLifeStyleBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.LifeStyleState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.LifeStyleState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.LifeStyleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LifeStyleFragment : Fragment(R.layout.fragment_life_style) {

    private val viewModel: LifeStyleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentLifeStyleBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.creditBureauValidateFragment,
            LifeStyleFragmentDirections.actionLifeStyleFragmentToCreditBureauValidateFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
        viewModel.getHousingTypes()
    }

    private fun handleStateChange(state: LifeStyleState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenLifeStyleSecondPart -> navigateSafe(state.direction)
            is BackCreditBureauValidateScreen -> navigatePreviousScreen(state.id, state.directions)
            is ErrorStates -> simpleDialog(R.string.dialog_title_error, R.string.dialog_description_error_generic_message)
        }
    }
}
