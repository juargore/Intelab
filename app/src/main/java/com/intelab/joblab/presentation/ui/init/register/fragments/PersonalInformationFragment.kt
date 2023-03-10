package com.intelab.joblab.presentation.ui.init.register.fragments

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentPersonalInformationBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.base.utils._imageUpper
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationState.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalInformationFragment : Fragment(R.layout.fragment_personal_information) {

    private lateinit var binding: FragmentPersonalInformationBinding
    private val viewModel: PersonalInformationViewModel by viewModels()
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.userPhotoUri = uri
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPersonalInformationBinding.bind(view)
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: PersonalInformationState) {
        when (state) {
            is Init -> Unit
            is OpenPersonalInformationValidateScreen -> navigateSafe(state.direction)
            is BackAuthorizationScreen -> findNavController().navigateUp()
            is ErrorPersonalInformation -> showJoblabDialog { errorDialog(state.rawResponse) }.show()
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenBottomSheetDialog -> {
                showBottomSheetDialog()
                viewModel.state.value = Init
            }
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val cameraFragment = CameraFragment()
        cameraFragment.setFragmentCallBacks(object : CameraFragment.FragmentCallBacks {
            override fun onCallBack(data: Bundle?) {
                val item = data?.getString(_imageUpper.lowerCaseDefault())
                item?.let { Uri.parse(it)?.let { uri ->
                    viewModel.userPhotoUri = uri
                }}
            }
        })
        with(getSheetCameraGallery()) {
            tvCamera.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.nav_host_fragment, cameraFragment)
                    ?.addToBackStack(null)
                    ?.commit()
                bottomSheetDialog.dismiss()
            }
            tvPhotoOrExplorer.setOnClickListener {
                selectImageFromGalleryResult.launch(getPictureIntent())
                bottomSheetDialog.dismiss()
            }
            tvCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            if (viewModel.userPhotoUri != null) {
                tvDeletePhoto.show()
                vDelete.show()
                tvDeletePhoto.setOnClickListener {
                    showDeleteAvatarDialog(viewModel::deleteCloudPhoto)
                    bottomSheetDialog.dismiss()
                }
            } else {
                tvDeletePhoto.hide()
                vDelete.hide()
            }
            bottomSheetDialog.setContentView(root)
        }
        bottomSheetDialog.show()
    }
}
