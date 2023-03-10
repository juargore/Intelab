package com.intelab.joblab.presentation.ui.helpers.images

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentImageGalleryOrCameraBinding
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.presentation.extensions.getArrayFromUri
import com.intelab.joblab.presentation.extensions.showBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ImageGalleryOrCameraFragment : Fragment(R.layout.fragment_image_gallery_or_camera) {

    private lateinit var binding: FragmentImageGalleryOrCameraBinding
    private val viewModel: ImageGalleryOrCameraViewModel by viewModels()
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
        viewModel.state
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { handleStateChange(it) }
            .launchIn(lifecycleScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImageGalleryOrCameraBinding.bind(view)
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        resetState()
        viewModel.loadDataFromDb()
    }

    fun setShowStroke(value: Boolean) { binding.ccvPhoto.setMustShowStroke(value) }

    private fun resetState() {
        viewModel.state.value = GalleryOrCameraViewModelState.Init
    }

    private fun handleStateChange(state: GalleryOrCameraViewModelState) {
        when (state) {
            is GalleryOrCameraViewModelState.Init -> Unit
            is GalleryOrCameraViewModelState.OpenBottomSheetDialog -> {
                showBottomSheetDialog(viewModel) {
                    selectImageFromGalleryResult.launch(it)
                }; resetState()
            }
        }
    }
}
