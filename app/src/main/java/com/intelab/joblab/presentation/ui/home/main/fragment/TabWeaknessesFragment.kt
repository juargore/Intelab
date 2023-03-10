package com.intelab.joblab.presentation.ui.home.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentTabWeaknessesBinding
import com.intelab.joblab.presentation.ui.home.main.viewmodels.TabWeaknessesViewModel
import com.intelab.joblab.presentation.base.utils._data
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TabWeaknessesFragment : Fragment(R.layout.fragment_tab_weaknesses) {

    private val viewModel: TabWeaknessesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentTabWeaknessesBinding.bind(view).also { it.viewModel = viewModel }
        viewModel.weaknesses = arguments?.getParcelableArrayList(_data) ?: listOf()
    }
}
