package com.intelab.joblab.presentation.ui.home.main.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentHomeTabOneBinding
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeState
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeTabOneState
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeTabOneViewModel
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeViewModel
import com.intelab.joblab.presentation.base.utils._allFilesType
import com.intelab.joblab.presentation.base.utils._appPdfType
import com.intelab.joblab.presentation.base.utils._imagePickerType
import com.intelab.joblab.presentation.base.utils._maxFileSizeAllowed
import com.intelab.joblab.presentation.ui.init.register.fragments.CameraFragment
import com.intelab.joblab.presentation.ui.views.TYPES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeTabOneFragment : Fragment(R.layout.fragment_home_tab_one) {

    private val viewModel: HomeTabOneViewModel by viewModels()
    private val parentViewModel: HomeViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
        setFragmentResultListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentHomeTabOneBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = HomeTabOneState.Init
        viewModel.getPositionsAndRefreshViews()
    }

    private fun handleStateChange(state: HomeTabOneState) {
        when (state) {
            is HomeTabOneState.Init -> Unit
            is HomeTabOneState.IsLoading -> updateProgressDialog(state.isLoading)
            is HomeTabOneState.InformComplementaryRegisterIncomplete -> showPopupIncompleteRegister()
            is HomeTabOneState.OpenComplementaryRegisterScreen -> navigateSafe(state.direction)
            is HomeTabOneState.OpenResumptionScreen -> navigateToDeepLink(getString(state.deepLink).toUri())
            is HomeTabOneState.OpenAccutestScreen -> navigateSafe(state.direction)
            is HomeTabOneState.OpenJobsScreen -> navigateSafe(state.direction)
            is HomeTabOneState.AskForUpdateWhenCompletedDocument -> {
                showJoblabDialog {
                    setTypeDialog(TYPES.DOUBLE)
                    title.text = getString(R.string.tv_title_ask_attached_file)
                    message.text = getString(R.string.tv_body_ask_attached_file)
                    acceptClickListener {
                        showBottomSheetDialog(isForFiles = true)
                        viewModel.state.value = HomeTabOneState.Init
                    }
                    cancelClickListener { }
                }.show()
                viewModel.state.value = HomeTabOneState.Init
            }
            is HomeTabOneState.PickDocument -> {
                showBottomSheetDialog(isForFiles = true)
                viewModel.state.value = HomeTabOneState.Init
            }
            is HomeTabOneState.BackLoginScreen -> navigateToDeepLink(
                getString(state.deepLink).toUri(),
                R.id.homeFragment,
                true
            )
            is HomeTabOneState.OpenBottomSheetDialog -> {
                showBottomSheetDialog(isForFiles = false)
                viewModel.state.value = HomeTabOneState.Init
            }
            is HomeTabOneState.ErrorStates ->
                parentViewModel.state.value = HomeState.ErrorState(state.rawResponse)
            is HomeTabOneState.UpdateUserName -> {
                if (parentFragment is HomeFragment) {
                    (parentFragment as HomeFragment).updateUserName(viewModel.userFullName)
                }
                parentViewModel.updateSideMenu()
            }
        }
    }

    private fun showPopupIncompleteRegister() {
        val builder =
            AlertDialog.Builder(requireContext(), R.style.JoblabProgressDialogStyle).create()
        val view = layoutInflater.inflate(R.layout.popup_register_incomplete, null)
        view.findViewById<Button>(R.id.btnGoEvaluation).setOnClickListener {
            viewModel.onRegisterClicked(null)
            builder.dismiss()
        }; builder.setView(view); builder.show()
        viewModel.state.value = HomeTabOneState.Init
    }


    /**
     * ------- Starts pick file section -------
     */
    private fun pickFile() {
        val mimeTypes = arrayOf(_imagePickerType, _appPdfType)
        val pickFile = Intent()
            .setType(_allFilesType)
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            .setAction(Intent.ACTION_GET_CONTENT)
        pickFileResult.launch(pickFile)
    }

    private val pickFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val selectedFile: Uri = uri
                    if (allValidationsPassedForFile(selectedFile, false)) {
                        viewModel.attachedFile = selectedFile
                        viewModel.deletePendingFileOnServer(requireContext())
                        toast(R.string.tv_title_attached_file)
                    }
                }
            }
        }

    private fun allValidationsPassedForFile(selectedFile: Uri, fromCamera: Boolean): Boolean {
        // First, validate only images and pdf formats (do not include .gif files)
        val typesList = listOf("jpg", "jpeg", "png", "webp", "bmp", "heif", "pdf")
        val mimeType = viewModel.getMimeType(requireContext(), selectedFile)
        var isValidType = false

        typesList.forEach { extension ->
            if (mimeType.equals(extension, true)) {
                isValidType = true
                return@forEach
            }
        }

        if (fromCamera && !isValidType) {
            isValidType = true
        }

        if (isValidType) {
            // Now, validate the size < 10MB
            val fileDescriptor =
                requireContext().contentResolver.openAssetFileDescriptor(selectedFile, "r")
            val fileSize = fileDescriptor!!.length
            return if (fileSize > _maxFileSizeAllowed) { // 10Mb is the limit allowed on files
                showJoblabDialog { errorDialogEmpty(getString(R.string.tv_title_attached_file_heavy)) }.show()
                viewModel.state.value = HomeTabOneState.Init
                false
            } else {
                // File has valid format and size < 10mb -> Send to server!
                true
            }
        } else {
            // File is not Image or PDF -> Show error to User
            showJoblabDialog { errorDialogEmpty(getString(R.string.tv_title_attached_file_extension)) }.show()
            viewModel.state.value = HomeTabOneState.Init
            return false
        }
    }

    /** ------- End of pick file section ------- */


    /**
     * ------- Starts camera and gallery section -------
     */
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

    private fun showBottomSheetDialog(isForFiles: Boolean) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val cameraFragment = CameraFragment(isForFiles)
        cameraFragment.setFragmentCallBacks(object : CameraFragment.FragmentCallBacks {
            override fun onCallBack(data: Bundle?) {
                val item: String? = data?.getString("image")
                item?.let {
                    val uri = Uri.parse(it)
                    getArrayFromUri(uri)?.let { b ->
                        if (isForFiles) {
                            if (allValidationsPassedForFile(uri, true)) {
                                viewModel.attachedFile = uri
                                viewModel.deletePendingFileOnServer(requireContext())
                                toast(R.string.tv_title_attached_file)
                            }
                        } else {
                            viewModel.userPhoto = DataArray(b)
                            viewModel.sendPhotoToCloud()
                        }
                    }
                }
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
            tvPhotoOrExplorer.text = if (isForFiles) {
                getString(R.string.tv_file_explorer)
            } else {
                getString(R.string.tv_photo_album)
            }
            tvPhotoOrExplorer.setOnClickListener {
                if (isForFiles) {
                    pickFile()
                } else {
                    selectImageFromGalleryResult.launch(getPictureIntent())
                }
                bottomSheetDialog.dismiss()
            }
            tvCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            if (!isForFiles) {
                if (viewModel.photoUrl.isNotEmpty()) {
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
            }
            bottomSheetDialog.setContentView(root)
        }; bottomSheetDialog.show()
    }

    /** ------- End of camera and gallery section ------- */

    private fun setFragmentResultListeners() {
        setFragmentResultListener("updatePhotoKey") { _, bundle ->
            val updatedPhoto = bundle.getBoolean("newPhoto")
            if (updatedPhoto) {
                viewModel.getPositionsAndRefreshViews()
            }
        }
    }
}
