package com.intelab.joblab.presentation.ui.home.profile.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentProfilePersonalBinding
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfilePersonalState
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfilePersonalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePersonalFragment : Fragment(R.layout.fragment_profile_personal) {

    private lateinit var binding: FragmentProfilePersonalBinding
    private val viewModel: ProfilePersonalViewModel by viewModels()
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    getArrayFromUri(uri)?.let {
                        viewModel.userPhoto = DataArray(it)
                        viewModel.sendPhotoToCloud()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfilePersonalBinding.bind(view)
        binding.viewModel = viewModel
        setProgress()
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = ProfilePersonalState.Init
    }

    private fun handleStateChange(state: ProfilePersonalState) {
        when (state) {
            is ProfilePersonalState.Init -> Unit
            is ProfilePersonalState.OpenProfileDomicileScreen -> navigateSafe(state.direction)
            is ProfilePersonalState.IsLoading -> updateProgressDialog(state.isLoading)
            is ProfilePersonalState.BackHomeScreen -> findNavController().navigateUp()
            is ProfilePersonalState.ErrorStates -> errorValidation(state.rawResponse)
            is ProfilePersonalState.OpenBottomSheetDialog -> {
                showBottomSheetDialog(viewModel) { selectImageFromGalleryResult.launch(it) }
                viewModel.state.value = ProfilePersonalState.Init
            }
            is ProfilePersonalState.ShowDialog -> {
                showJoblabDialog { errorDialogEmpty(getString(state.messageId)) }.show()
                viewModel.state.value = ProfilePersonalState.Init
            }
        }
    }

    private fun setProgress() {
        binding.headerRegisterPhoto.ccvPhoto.setCompletionPercentage(99.99f)
    }
}
