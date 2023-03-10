package com.intelab.joblab.presentation.ui.init.splash.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentOnBoardingBinding
import com.intelab.joblab.databinding.FragmentOnBoardingFourthScreenBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.ui.init.splash.adapter.OnBoardingAdapter
import com.intelab.joblab.presentation.ui.init.splash.viewmodel.OnBoardingFourthScreenState
import com.intelab.joblab.presentation.ui.init.splash.viewmodel.OnBoardingFourthScreenViewModel

class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentOnBoardingBinding.bind(view).also {
            val adapt = OnBoardingAdapter(requireActivity())
            it.adapter = adapt
        }
    }
}

class OnBoardingFirstScreenFragment : Fragment(R.layout.fragment_on_boarding_first_screen)
class OnBoardingSecondScreenFragment : Fragment(R.layout.fragment_on_boarding_second_screen)
class OnBoardingThirdScreenFragment : Fragment(R.layout.fragment_on_boarding_third_screen)

class OnBoardingFourthScreenFragment : Fragment(R.layout.fragment_on_boarding_fourth_screen) {

    private val viewModel: OnBoardingFourthScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentOnBoardingFourthScreenBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = OnBoardingFourthScreenState.Init
    }

    private fun handleStateChange(state: OnBoardingFourthScreenState) {
        when (state) {
            is OnBoardingFourthScreenState.Init -> Unit
            is OnBoardingFourthScreenState.OpenLoginScreen -> navigateSafe(state.directions)
        }
    }
}
