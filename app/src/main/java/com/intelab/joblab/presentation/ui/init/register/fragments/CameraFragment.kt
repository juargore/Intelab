package com.intelab.joblab.presentation.ui.init.register.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentCameraBinding
import com.intelab.joblab.presentation.base.utils._imageKey
import com.intelab.joblab.presentation.base.utils._imageLower
import com.intelab.joblab.presentation.base.utils._joblabApp
import com.intelab.joblab.presentation.base.utils._package
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.helpers.REQUIRED_CAMERA_PERMISSIONS
import com.intelab.joblab.presentation.ui.helpers.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CameraState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CameraViewModel
import com.intelab.joblab.presentation.ui.views.TYPES
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment(private val enableBackCamera: Boolean = false) : Fragment(R.layout.fragment_camera) {

    private lateinit var binding: FragmentCameraBinding
    private var cameraExecutor: ExecutorService? = null
    private val viewModel: CameraViewModel by viewModels()
    private var imageCapture: ImageCapture? = null
    private var fragmentCallBacks: FragmentCallBacks? = null
    private var cameraSelector: CameraSelector? = null
    private var mBundle: Bundle? = null

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value == true }
            if (granted) {
                startCamera()
            } else {
                showJoblabDialog {
                    setTypeDialog(TYPES.DOUBLE)
                    title.text = getString(R.string.dialog_title_error)
                    message.text = getString(R.string.tv_camera_permissions_denied)
                    cancelable = false
                    cancelClickListener {
                        activity?.supportFragmentManager?.popBackStack()
                    }
                    acceptClickListener {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts(_package, context.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        activity?.supportFragmentManager?.popBackStack()
                    }
                }.show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCameraBinding.bind(view)
        binding.viewModel = viewModel
        if (allCameraPermissionsGranted()) {
            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
        } else {
            requestPermissions.launch(REQUIRED_CAMERA_PERMISSIONS)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = CameraState.Init
    }

    private fun handleStateChange(state: CameraState) {
        when (state) {
            is CameraState.Init -> Unit
            is CameraState.OpenCamera -> {
                takePhoto()
                viewModel.state.value = CameraState.Init
            }
            is CameraState.BackPersonalInformationScreen -> {
                val bundle = bundleOf(
                    _imageLower to saveBitmapOnDevice(
                        requireContext(),
                        binding.ivPhoto.drawable.toBitmap()
                    ).toString()
                )

                try {
                    // used to return bundle with navigation architecture
                    setFragmentResult(_imageKey, bundle)
                    findNavController().navigateUp()
                } catch (e: Exception) {
                    // used to return bundle with traditional architecture
                    mBundle = bundle
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
            CameraState.TakeOtherPhoto -> {
                binding.group1.show()
                binding.group2.hide()
            }
        }
    }

    interface FragmentCallBacks {
        fun onCallBack(data: Bundle?)
    }

    fun setFragmentCallBacks(fragmentCallBacks: FragmentCallBacks) {
        this.fragmentCallBacks = fragmentCallBacks
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    image.image?.let {
                        val bitmap = rotateImage(
                            imageToBitmap(it),
                            image.imageInfo.rotationDegrees
                        )
                        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            showBitmap(bitmap)
                        } else {
                            showBitmap(flipBitmap(bitmap))
                        }
                    }
                    image.close()
                }
            })
    }

    private fun showBitmap(bitmap: Bitmap) {
        binding.group1.hide()
        binding.group2.show()
        binding.ivPhoto.setImageBitmap(bitmap)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()
            cameraSelector = if (enableBackCamera) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector!!, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(_joblabApp, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentCallBacks?.onCallBack(mBundle)
        cameraExecutor?.shutdown()
    }
}