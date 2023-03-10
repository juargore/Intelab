@file:Suppress("unused")

package com.intelab.joblab.presentation.extensions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.intelab.joblab.R
import com.intelab.joblab.data.common.module.REFRESH_TOKEN_EXPIRED_CODE
import com.intelab.joblab.databinding.DialogChooseCameraGalleryBinding
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.presentation.ui.helpers.images.ImageGalleryOrCameraViewModel
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeViewModel
import com.intelab.joblab.presentation.base.utils._imageLower
import com.intelab.joblab.presentation.base.utils._imagePickerType
import com.intelab.joblab.presentation.base.utils._joblabTopicName
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfilePersonalViewModel
import com.intelab.joblab.presentation.ui.init.register.fragments.CameraFragment
import com.intelab.joblab.presentation.ui.views.JoblabDialog
import com.intelab.joblab.presentation.ui.views.JoblabProgressDialog
import com.intelab.joblab.presentation.ui.views.TYPES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun Fragment.showJoblabDialog(func: JoblabDialog.() -> Unit): AlertDialog =
    JoblabDialog(requireContext()).apply { func() }.create()

var progressDialog: AlertDialog? = null

fun Fragment.updateProgressDialog(show: Boolean, message: String? = null): AlertDialog? {
    if (progressDialog == null) {
        progressDialog = JoblabProgressDialog(requireContext()).create()
    }

    // show/hide alert dialog and define some properties
    progressDialog?.let { alert ->
        if (show) {
            alert.show()
        } else {
            alert.dismiss()
        }

        // hide bottom message when the value is null, otherwise set the value
        progressDialog?.findViewById<TextView>(R.id.progressMessage)?.let {
            if (message != null) {
                it.text = message; it.show()
            } else {
                it.hide()
            }
        }
    }

    return progressDialog
}

fun Fragment.navigateToDeepLink(uri: Uri, popUpTo: Int? = null, popUpToInclusive: Boolean = false) {
    popUpTo?.let {
        val v = NavOptions.Builder().setPopUpTo(it, popUpToInclusive).build()
        findNavController().navigate(uri, v)
    } ?: run {
        findNavController().navigate(uri)
    }
}

fun Fragment.navigatePreviousScreen(@IdRes id: Int, directions: NavDirections) {
    if (findNavController().isOnBackStack(id)) {
        findNavController().navigateUp()
    } else {
        navigateSafe(directions)
    }
}

fun Fragment.setUpBackNavigation(@IdRes id: Int, directions: NavDirections) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigatePreviousScreen(id, directions)
            }
        }
    )
}

fun Fragment.showSettingScreenDialog() {
    showJoblabDialog {
        setTypeDialog(TYPES.DOUBLE)
        cancelable = false
        title.text = getString(R.string.dialog_title_accutest)
        acceptButton.text = getString(R.string.dialog_bn_go_Settings)
        message.text = getString(R.string.dialog_description_need_settings_screen)
        cancelClickListener { }
        acceptClickListener {
            openSettings()
        }
    }.show()
}

fun Fragment.openSettings() {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        data = Uri.parse("package:${requireContext().packageName}")
    }.run(::startActivity)
}

fun Fragment.getSheetCameraGallery() = DataBindingUtil.inflate(
    layoutInflater,
    R.layout.dialog_choose_camera_gallery,
    null,
    false
) as DialogChooseCameraGalleryBinding

fun Fragment.showDeleteAvatarDialog(deleteAction: () -> Unit) {
    showJoblabDialog {
        setTypeDialog(TYPES.DOUBLE)
        title.text = getString(R.string.dialog_title_avatar)
        message.text = getString(R.string.dialog_description_delete_avatar)
        acceptClickListener {
            deleteAction()
        }
        cancelClickListener { }
    }.show()
}

fun Fragment.addAndRemoveOnWindowFocusChangeListener(callback: (hasFocus: Boolean) -> Unit) {
    view?.viewTreeObserver?.addOnWindowFocusChangeListener(callback)
    view?.viewTreeObserver?.removeOnWindowFocusChangeListener(callback)
}

fun Fragment.isFragmentInBackStack(destinationId: Int) =
    try {
        findNavController().getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        false
    }

fun Fragment.getExpiredRefreshTokenDialogInitialRegister(): AlertDialog {
    return showJoblabDialog {
        setTypeDialog(TYPES.SIMPLE)
        cancelable = false
        title.hide()
        acceptClickListener {
            if (isFragmentInBackStack(R.id.loginFragment)) {
                navigateToDeepLink(
                    getString(R.string.deep_link_login_screen).toUri(),
                    R.id.loginFragment,
                    true
                )
            } else {
                navigateToDeepLink(
                    getString(R.string.deep_link_login_screen).toUri(),
                    R.id.init_register_navigation,
                    true
                )
            }
        }
    }
}

fun Fragment.getExpiredRefreshTokenDialog(): AlertDialog {
    return showJoblabDialog {
        setTypeDialog(TYPES.SIMPLE)
        cancelable = false
        title.hide()
        acceptClickListener {
            Firebase.messaging.unsubscribeFromTopic(_joblabTopicName)
                .addOnCompleteListener { task ->
                    var msg = "Unsubscribed to $_joblabTopicName!"
                    if (!task.isSuccessful) {
                        msg = "Unsubscribed to $_joblabTopicName failed"
                    }; println("Firebase: $msg")
                }
            navigateToDeepLink(
                getString(R.string.deep_link_login_screen).toUri(),
                R.id.homeFragment,
                true
            )
        }
    }
}

fun Fragment.getEvaluationDialog(): AlertDialog {
    return showJoblabDialog {
        setTypeDialog(TYPES.SIMPLE)
        cancelable = false
        title.hide()
        acceptClickListener {
            navigateToDeepLink(
                getString(R.string.deep_link_login_screen).toUri(),
                R.id.homeFragment,
                true
            )
        }
    }
}

fun Fragment.navigateSafe(directions: NavDirections, navOptions: NavOptions? = null) {
    findNavController().currentDestination?.getAction(directions.actionId)?.let {
        findNavController().navigate(directions, navOptions)
    }
}

fun <T> MutableStateFlow<T>.flow(fr: Fragment, handleFunc: (T) -> Unit) {
    flowWithLifecycle(fr.lifecycle, Lifecycle.State.CREATED)
        .onEach { handleFunc.invoke(it) }
        .launchIn(fr.lifecycleScope)
}

fun Fragment.showBottomSheetDialog(vm: ViewModel, selectImageFromGallery: (Intent) -> Unit) {
    val bottomSheetDialog = BottomSheetDialog(requireContext())
    val cameraFragment = CameraFragment()
    cameraFragment.setFragmentCallBacks(object : CameraFragment.FragmentCallBacks {
        override fun onCallBack(data: Bundle?) {
            val item: String? = data?.getString(_imageLower)
            item?.let {
                getArrayFromUri(Uri.parse(it))?.let { b ->
                    when (vm) {
                        is HomeViewModel -> {
                            vm.userPhoto = DataArray(b)
                            vm.sendPhotoToCloud()
                        }
                        is ImageGalleryOrCameraViewModel -> {
                            vm.userPhoto = DataArray(b)
                            vm.sendPhotoToCloud()
                        }
                        else -> {
                            (vm as ProfilePersonalViewModel).userPhoto = DataArray(b)
                            vm.sendPhotoToCloud()
                        }
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
        tvPhotoOrExplorer.setOnClickListener {
            selectImageFromGallery.invoke(getPictureIntent())
            bottomSheetDialog.dismiss()
        }
        tvCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        val url = when (vm) {
            is HomeViewModel -> vm.url
            is ImageGalleryOrCameraViewModel -> vm.photoUrl
            else -> (vm as ProfilePersonalViewModel).photoUrl
        }
        if (url.isNotEmpty()) {
            tvDeletePhoto.show()
            vDelete.show()
            tvDeletePhoto.setOnClickListener {
                showDeleteAvatarDialog(
                    when (vm) {
                        is HomeViewModel -> vm::deleteCloudPhoto
                        is ImageGalleryOrCameraViewModel -> vm::deleteCloudPhoto
                        else -> (vm as ProfilePersonalViewModel)::deleteCloudPhoto
                    }
                )
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

fun Fragment.errorValidation(rawResponse: ErrorGenericResponse, funcX : (() -> Unit)? = null) {
    val expiredRefreshTokenAlertDialog = getExpiredRefreshTokenDialog()
    if (rawResponse.status == REFRESH_TOKEN_EXPIRED_CODE) {
        expiredRefreshTokenAlertDialog.show()
        expiredRefreshTokenAlertDialog.findViewById<TextView>(R.id.message)?.text = rawResponse.message
    } else {
        showJoblabDialog { errorDialog(rawResponse) }.show()
        funcX?.invoke()
    }
}

fun Fragment.simpleDialog(
    _title: Any,
    _message: Any,
    _cancelable: Boolean = true,
    _acceptBtn: String? = null,
    funcX : (() -> Unit)? = null)
{
    var t = ""
    var m = ""

    if (_title is String) t = _title
    else if (_title is Int) t = getString(_title)

    if (_message is String) m = _message
     else if (_message is Int) m = getString(_message)

    showJoblabDialog {
        setTypeDialog(TYPES.SIMPLE)
        cancelable = _cancelable
        title.text = t
        message.text = m

        if (_acceptBtn != null)
            acceptButton.text = _acceptBtn

        acceptClickListener {
            funcX?.invoke()
        }
    }.show()
}



fun Fragment.getArrayFromUri(uri: Uri): ByteArray? {
    return requireActivity().contentResolver?.openInputStream(uri)?.readBytes()
}

fun getPictureIntent() : Intent {
    return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
        type = _imagePickerType
    }
}