package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentLifeStylePartTwoBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.LifeStylePartTwoState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.LifeStylePartTwoState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.LifeStylePartTwoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LifeStylePartTwoFragment : Fragment(R.layout.fragment_life_style_part_two) {

    private val viewModel: LifeStylePartTwoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentLifeStylePartTwoBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.lifeStyleFragment,
            LifeStylePartTwoFragmentDirections.actionLifeStylePartTwoFragmentToLifeStyleFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
        viewModel.callServicesEndPoint()
    }

    private fun handleStateChange(state: LifeStylePartTwoState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenEconomicScreen -> navigateSafe(state.direction)
            is BackLifeStyleScreen -> navigatePreviousScreen(state.id, state.directions)
            is ErrorStates -> simpleDialog(R.string.dialog_title_error, R.string.dialog_description_error_generic_message)
        }
    }
}
