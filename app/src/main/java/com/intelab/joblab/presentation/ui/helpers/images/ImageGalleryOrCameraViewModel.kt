package com.intelab.joblab.presentation.ui.helpers.images

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase.FilesExpected
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.helpers.toMultiPartFile
import com.intelab.joblab.presentation.base.utils.FileNames
import com.intelab.joblab.presentation.base.utils._foreign
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageGalleryOrCameraViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val authUseCase: AuthUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<GalleryOrCameraViewModelState>(GalleryOrCameraViewModelState.Init)

    @get:Bindable
    var userPhoto by bindDelegate<DataArray?>(null)

    @get:Bindable
    var photoUrl by bindDelegate("")

    @get:Bindable
    var email by bindDelegate("")

    @get:Bindable
    var nationality by bindDelegate("")

    @get:Bindable
    var foreign by bindDelegate(false)

    fun openGalleryCameraDialog(@Suppress("UNUSED_PARAMETER") v: View?) {
        state.value = GalleryOrCameraViewModelState.OpenBottomSheetDialog
    }

    fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().first().let { cr ->
                cr.email?.let { email = it }
                cr.photoUrl?.let { photoUrl = it }
                cr.nationality?.let { foreign = it == _foreign }
            }
        }
    }

    fun sendPhotoToCloud() {
        launch {
            userPhoto?.value?.let {
                candidateUseCase.sendUserPhoto(
                    toMultiPartFile(FileNames.CANDIDATE.value, it)).collect { result ->
                    if (result is BaseResult.Success) {
                        photoUrl = result.data
                        updatePhotoOnInternalDb(photoUrl)
                    }
                }
            }
        }
    }

    private fun updatePhotoOnInternalDb(photo: String) {
        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(photoUrl = photo),
                ImageGalleryOrCameraViewModel::class.simpleName
            )
        }
    }

    fun deleteCloudPhoto() {
        launch {
            candidateUseCase.deleteFileFromServer(FilesExpected.AVATAR).collect { result ->
                if (result is BaseResult.Success) {
                    photoUrl = ""
                    updatePhotoOnInternalDb("")
                }
            }
        }
    }
}

sealed class GalleryOrCameraViewModelState {
    object Init : GalleryOrCameraViewModelState()
    object OpenBottomSheetDialog : GalleryOrCameraViewModelState()
}