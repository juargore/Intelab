package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentCreditBureauBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigatePreviousScreen
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.setUpBackNavigation
import com.intelab.joblab.presentation.ui.home.register.viewmodels.CreditBureauState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.CreditBureauState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.CreditBureauViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditBureauFragment : Fragment(R.layout.fragment_credit_bureau) {

    private val viewModel: CreditBureauViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentCreditBureauBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.domicileFragment,
            CreditBureauFragmentDirections.actionCreditBureauFragmentToDomicileFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
        viewModel.loadDataFromDb()
    }

    private fun handleStateChange(state: CreditBureauState) {
        when (state) {
            is Init -> Unit
            is OpenCreditBureauValidateScreen -> navigateSafe(state.direction)
            is BackDomicileScreen -> navigatePreviousScreen(state.id, state.directions)
        }
    }
}
