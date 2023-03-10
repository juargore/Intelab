package com.intelab.joblab.presentation.ui.home.accutest.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentVideoBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.VideoState
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : Fragment(R.layout.fragment_video) {

    private val viewModel: VideoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentVideoBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = VideoState.Init
    }

    private fun handleStateChange(state: VideoState) {
        when (state) {
            is VideoState.Init -> Unit
            is VideoState.BackPreviousScreen -> findNavController().navigateUp()
        }
    }
}
