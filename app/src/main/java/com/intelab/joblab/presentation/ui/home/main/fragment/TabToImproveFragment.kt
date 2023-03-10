package com.intelab.joblab.presentation.ui.home.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentTabToImproveBinding
import com.intelab.joblab.presentation.ui.home.main.viewmodels.TabToImproveViewModel
import com.intelab.joblab.presentation.base.utils._data
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabToImproveFragment : Fragment(R.layout.fragment_tab_to_improve) {

    private val viewModel: TabToImproveViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentTabToImproveBinding.bind(view).also { it.viewModel = viewModel }
        viewModel.toImprove = arguments?.getParcelableArrayList(_data) ?: listOf()
    }
}
