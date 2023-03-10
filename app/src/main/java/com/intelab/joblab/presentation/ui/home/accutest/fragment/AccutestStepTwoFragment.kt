package com.intelab.joblab.presentation.ui.home.accutest.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentAccutestStepTwoBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.helpers.REQUIRED_CAMERA_PERMISSIONS
import com.intelab.joblab.presentation.ui.helpers.allCameraPermissionsGranted
import com.intelab.joblab.presentation.ui.home.accutest.adapter.AccutestAdapter
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.AccutestStepTwoState
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.AccutestStepTwoState.*
import com.intelab.joblab.presentation.ui.home.accutest.viewmodels.AccutestStepTwoViewModel
import com.intelab.joblab.presentation.ui.views.TYPES
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class AccutestStepTwoFragment : Fragment(R.layout.fragment_accutest_step_two) {

    lateinit var binding: FragmentAccutestStepTwoBinding
    private val viewModel: AccutestStepTwoViewModel by viewModels()
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var alertDialogTakePhoto: AlertDialog? = null

    enum class AccutestState {
        INIT, END, OTHER
    }

    var accutestState = AccutestState.INIT

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value == true }
            when {
                granted -> startCamera()
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        || shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> { showGrantPermissionDialog() }
                else -> showSettingScreenDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccutestStepTwoBinding.bind(view)
        binding.viewModel = viewModel
        viewModel.firstPhoto = true
        alertDialogTakePhoto = showJoblabDialog {
            setTypeDialog(TYPES.SIMPLE)
            cancelable = false
            title.text = getString(R.string.dialog_title_accutest)
            message.text = getString(R.string.dialog_photo_taking_notification)
            acceptButton.text = getString(R.string.bn_text_accept)
            acceptClickListener {
                if (!allCameraPermissionsGranted())
                    requestPermissions.launch(REQUIRED_CAMERA_PERMISSIONS)
                else {
                    startCamera()
                }
            }
        }
        if (viewModel.isLoadingSecondTest) {
            alertDialogTakePhoto?.show()
            viewModel.isLoadingSecondTest = false
        } else {
            if (!allCameraPermissionsGranted())
                requestPermissions.launch(REQUIRED_CAMERA_PERMISSIONS)
            else {
                startCamera()
            }
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        requireActivity().backConfirmation(this, true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
        if (allCameraPermissionsGranted()) startCamera()
    }

    private fun handleStateChange(state: AccutestStepTwoState) {
        when (state) {
            Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is ErrorAccutestStepTwo -> {
                if (alertDialogTakePhoto?.isShowing == true) alertDialogTakePhoto?.dismiss()
                errorValidation(state.rawResponse)
            }
            TakeCandidatePhoto -> {
                val adapter = binding.rvTest.adapter as AccutestAdapter
                val answers = adapter.phrases.map { it.id }
                viewModel.answers = answers.joinToString(separator = "/")
                if (answers == viewModel.originalList || adapter.currentMovements < 6) {
                    showJoblabDialog {
                        setTypeDialog(TYPES.DOUBLE)
                        title.text = getString(R.string.dialog_title_accutest)
                        message.text = getString(R.string.dialog_message_order_phrases)
                        acceptButton.text = getString(R.string.bn_text_yes)
                        cancelButton.text = getString(R.string.bn_text_no)
                        acceptClickListener {
                            if (!allCameraPermissionsGranted()) {
                                viewModel.state.value = IsLoading(true)
                                viewModel.sendCandidateAnswers()
                            } else {
                                accutestState = AccutestState.END
                                takeCandidatePhoto()
                            }
                        }
                        cancelClickListener { }
                    }.show()
                    viewModel.state.value = Init
                } else {
                    if (!allCameraPermissionsGranted()) {
                        viewModel.state.value = IsLoading(true)
                        viewModel.sendCandidateAnswers()
                    } else {
                        accutestState = AccutestState.END
                        takeCandidatePhoto()
                    }
                }
            }
            is OpenHomeScreen -> navigateSafe(state.directions)
            is GetAccutestPositions -> {
                val items = (binding.rvTest.adapter as AccutestAdapter).phrases
                viewModel.changeAccutestCard(items)
                viewModel.state.value = Init
            }
            is OpenVideoScreen -> {
                val adapter = binding.rvTest.adapter as AccutestAdapter
                viewModel.phrases = adapter.phrases
                navigateSafe(state.directions)
            }
            is OpenDialogToRedirectPartOne -> {
                showJoblabDialog {
                    setUpAccutestErrorDialog(
                        state.rawResponse.message,
                        !state.rawResponse.details.isNullOrEmpty(),
                        getDetailsFromList(state.rawResponse.details ?: listOf())
                    ) {
                        lifecycleScope.launchWhenResumed {
                            val directions =
                                AccutestStepTwoFragmentDirections.actionAccutestStepTwoFragmentToAccutestStepOneFragment()
                            navigateSafe(directions)
                        }
                    }
                }.show()
            }
            is OpenDialogToRedirectHome -> {
                showJoblabDialog {
                    setUpAccutestErrorDialog(
                        state.rawResponse.message,
                        !state.rawResponse.details.isNullOrEmpty(),
                        getDetailsFromList(state.rawResponse.details ?: listOf())
                    ) {
                        lifecycleScope.launchWhenResumed {
                            val directions =
                                AccutestStepTwoFragmentDirections.actionAccutestStepTwoFragmentToHomeFragment()
                            navigateSafe(directions)
                        }
                    }
                }.show()
            }
            is OpenAccutestSuccessDialog ->
                simpleDialog(R.string.dialog_title_accutest, state.message, _cancelable = false) {
                    val directions = AccutestStepTwoFragmentDirections.actionAccutestStepTwoFragmentToHomeFragment()
                    navigateSafe(directions)
                }
        }
    }

    private fun showGrantPermissionDialog() {
        simpleDialog(
            _title = R.string.dialog_title_accutest,
            _message = R.string.dialog_description_accutest_permission_required,
            _cancelable = false,
            _acceptBtn = getString(R.string.dialog_bn_accutest_grant_permission)
        ) {
            requestPermissions.launch(REQUIRED_CAMERA_PERMISSIONS)
        }
    }

    private fun takeCandidatePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    when (accutestState) {
                        AccutestState.INIT -> {
                            accutestState = AccutestState.OTHER
                            viewModel.sendAccutestPhoto(
                                requireContext(),
                                image
                            )
                        }
                        AccutestState.END -> {
                            accutestState = AccutestState.OTHER
                            viewModel.sendAnswersToServer(
                                requireContext(),
                                image
                            )
                        }
                        AccutestState.OTHER -> {
                            image.close()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("JoblabApp", "Photo capture failed: ${exception.message}", exception)
                }
            })
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

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            binding.viewFinder.previewStreamState.observe(viewLifecycleOwner) { state ->
                if (state == PreviewView.StreamState.STREAMING)
                    takeCandidatePhoto()
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}