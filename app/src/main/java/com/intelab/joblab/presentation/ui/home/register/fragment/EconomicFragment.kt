package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentEconomicBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigatePreviousScreen
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.setUpBackNavigation
import com.intelab.joblab.presentation.ui.home.register.viewmodels.EconomicState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.EconomicState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.EconomicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EconomicFragment : Fragment(R.layout.fragment_economic) {

    private val viewModel: EconomicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentEconomicBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.lifeStylePartTwoFragment,
            EconomicFragmentDirections.actionEconomicFragmentToLifeStylePartTwoFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
        viewModel.loadDataFromDb()
    }

    private fun handleStateChange(state: EconomicState) {
        when (state) {
            is Init -> Unit
            is OpenAcademicScreen -> navigateSafe(state.direction)
            is BackLifeStylePartTwoScreen -> navigatePreviousScreen(state.id, state.directions)
        }
    }
}
