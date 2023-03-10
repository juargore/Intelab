package com.intelab.joblab.presentation.ui.init.register.viewmodels

import com.intelab.joblab.presentation.base.ObservableViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CameraViewModel : ObservableViewModel() {

    val state = MutableStateFlow<CameraState>(CameraState.Init)

    fun onCameraClicked() { state.value = CameraState.OpenCamera }

    fun onCheckClicked() { state.value = CameraState.BackPersonalInformationScreen }

    fun onCloseClicked() { state.value = CameraState.TakeOtherPhoto }
}

sealed class CameraState {
    object Init : CameraState()
    object OpenCamera : CameraState()
    object TakeOtherPhoto : CameraState()
    object BackPersonalInformationScreen : CameraState()
}