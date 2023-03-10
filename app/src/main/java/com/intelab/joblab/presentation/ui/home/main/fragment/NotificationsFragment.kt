package com.intelab.joblab.presentation.ui.home.main.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentNotificationsBinding
import com.intelab.joblab.presentation.extensions.errorValidation
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.home.main.viewmodels.NotificationsState
import com.intelab.joblab.presentation.ui.home.main.viewmodels.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentNotificationsBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = NotificationsState.Init
    }

    private fun handleStateChange(state: NotificationsState) {
        when (state) {
            is NotificationsState.Init -> Unit
            is NotificationsState.BackScreen -> findNavController().navigateUp()
            is NotificationsState.IsLoading -> updateProgressDialog(state.isLoading)
            is NotificationsState.OnError -> errorValidation(state.rawResponse) {
                viewModel.state.value = NotificationsState.Init
            }
        }
    }
}
