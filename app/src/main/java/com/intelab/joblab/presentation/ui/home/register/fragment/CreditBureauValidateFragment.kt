package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentCreditBureauValidateBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigatePreviousScreen
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.setUpBackNavigation
import com.intelab.joblab.presentation.ui.home.register.viewmodels.CreditBureauValidateState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.CreditBureauValidateState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.CreditBureauValidateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditBureauValidateFragment : Fragment(R.layout.fragment_credit_bureau_validate) {

    private val viewModel: CreditBureauValidateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentCreditBureauValidateBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.creditBureauFragment,
            CreditBureauValidateFragmentDirections.actionCreditBureauValidateFragmentToCreditBureauFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDataFromDb(
            requireContext().resources.getString(R.string.tv_yes),
            requireContext().resources.getString(R.string.tv_no)
        )
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: CreditBureauValidateState) {
        when (state) {
            is Init -> Unit
            is OpenLifeStyleScreen -> navigateSafe(state.direction)
            is BackCreditBureauScreen -> navigatePreviousScreen(state.id, state.directions)
        }
    }
}
