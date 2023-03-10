package com.intelab.joblab.presentation.ui.home.main.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentHomeTabTwoBinding
import com.intelab.joblab.domain.entities.AccutestResultResponse
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.ui.home.main.adapter.HomeViewPagerAdapter
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeState
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeTabTwoState
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeTabTwoViewModel
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeTabTwoFragment : Fragment(R.layout.fragment_home_tab_two) {

    private lateinit var binding: FragmentHomeTabTwoBinding
    private val viewModel: HomeTabTwoViewModel by viewModels()
    private val parentViewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeTabTwoBinding.bind(view)
        binding.viewModel = viewModel
    }

    private fun setupViewPager(data: AccutestResultResponse?) {
        if (data != null) {
            if (::binding.isInitialized) {
                binding.viewPager.adapter =
                    HomeViewPagerAdapter(childFragmentManager).also {
                        it.addFragment(TabStrengthsFragment().apply {
                            arguments = Bundle().apply {
                                putParcelableArrayList("data", data.developed)
                            }
                        }, getString(R.string.tab_title_strengths))
                        it.addFragment(TabToImproveFragment().apply {
                            arguments = Bundle().apply {
                                putParcelableArrayList("data", data.toBeDeveloped)
                            }
                        }, getString(R.string.tab_title_improve))
                        it.addFragment(TabWeaknessesFragment().apply {
                            arguments = Bundle().apply {
                                putParcelableArrayList("data", data.opportunityAreas)
                            }
                        }, getString(R.string.tab_title_weaknesses))
                    }; binding.tabs.setupWithViewPager(binding.viewPager)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupViewPager(viewModel.results)
        viewModel.state.value = HomeTabTwoState.Init
    }

    private fun handleStateChange(state: HomeTabTwoState) {
        when (state) {
            is HomeTabTwoState.Init -> Unit
            is HomeTabTwoState.SendInfoChildFragments -> setupViewPager(state.results)
            is HomeTabTwoState.OpenAccutestScreen -> navigateSafe(state.direction)
            is HomeTabTwoState.InformComplementaryRegisterIncomplete -> showPopupIncompleteRegister()
            is HomeTabTwoState.ErrorStates -> parentViewModel.state.value =
                HomeState.ErrorState(state.rawResponse)
            is HomeTabTwoState.OpenComplementaryRegisterScreen ->
                navigateSafe(state.direction)
        }
    }

    private fun showPopupIncompleteRegister() {
        val builder =
            AlertDialog.Builder(requireContext(), R.style.JoblabProgressDialogStyle).create()
        val view = layoutInflater.inflate(R.layout.popup_register_incomplete, null)
        view.findViewById<Button>(R.id.btnGoEvaluation).setOnClickListener {
            val directions =
                HomeFragmentDirections.actionHomeFragmentToRegisterComplementaryNavigation()
            viewModel.state.value = HomeTabTwoState.OpenComplementaryRegisterScreen(directions)
            builder.dismiss()
        }; builder.setView(view); builder.show()
        viewModel.state.value = HomeTabTwoState.Init
    }
}
