package com.intelab.joblab.presentation.ui.home.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentTabStrengthsBinding
import com.intelab.joblab.presentation.ui.home.main.viewmodels.TabStrengthsViewModel
import com.intelab.joblab.presentation.base.utils._data
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabStrengthsFragment : Fragment(R.layout.fragment_tab_strengths) {

    private val viewModel: TabStrengthsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentTabStrengthsBinding.bind(view).also { it.viewModel = viewModel }
        viewModel.strengths = arguments?.getParcelableArrayList(_data) ?: listOf()
    }
}
