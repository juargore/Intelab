@file:Suppress("UNUSED_PARAMETER")

package com.intelab.joblab.presentation.ui.home.main.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase.FilesExpected
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.rotateDown
import com.intelab.joblab.presentation.extensions.rotateUp
import com.intelab.joblab.presentation.ui.helpers.images.ImageGalleryOrCameraViewModel
import com.intelab.joblab.presentation.ui.helpers.toMultiPartFile
import com.intelab.joblab.presentation.ui.helpers.toPDFMultiPartFile
import com.intelab.joblab.presentation.ui.home.main.fragment.HomeFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.FileNames
import com.intelab.joblab.presentation.base.utils.USER_COMPLEMENTARY_REGISTER_STATE
import com.intelab.joblab.presentation.base.utils._accutestTitle
import com.intelab.joblab.presentation.base.utils._delay20
import com.intelab.joblab.presentation.base.utils._delay250
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._pdf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeTabOneViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val authUseCase: AuthUseCase,
    val candidateUseCase: CandidateUseCase,
    val catalogUseCase: CatalogUseCase,
    val preferencesUseCase: PreferencesUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<HomeTabOneState>(HomeTabOneState.Init)
    private var typeFileSelected: FilesExpected? = null
    private var lastScreen = _indexOne

    @get:Bindable
    var userPhoto by bindDelegate<DataArray?>(null)

    @get:Bindable
    var attachedFile by bindDelegate<Uri?>(null)

    @get:Bindable
    var photoUrl by bindDelegate("")

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var userPositions by bindDelegate("")

    @get:Bindable
    var complementaryRegisterFinished by bindDelegate(true)

    @get:Bindable
    var profileCompleted by bindDelegate(_indexZero)

    @get:Bindable
    var homeDocumentList by bindDelegate<List<ItemHomeDocument>>(listOf())

    @get:Bindable
    var homeProfileList by bindDelegate<List<ItemHomeProfile>>(listOf())

    @get:Bindable
    var evaluationExpiredTitle by bindDelegate<Int?>(null)

    @get:Bindable
    var evaluationExpired by bindDelegate(false)

    @get:Bindable
    var jobsListAsString by bindDelegate("")

    @get:Bindable
    var showProfileInfo by bindDelegate(true)

    @get:Bindable
    var jobsSelected by bindDelegate<List<JobPostulation>>(listOf())

    init {
        getPercentageCompleted()
    }

    private fun getPercentageCompleted() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.screen?.let { lastScreen = it }
            }
        }

        viewModelScope.launch(Dispatchers.Main) {
            candidateUseCase.getPercentageCompletedAtHome()
                .onStart {
                    state.value = HomeTabOneState.IsLoading(true)
                }.collect { result ->
                    when (result) {
                        is BaseResult.Success -> {
                            profileCompleted = result.data
                            getRestOfInformation()
                            delay(_delay250)
                            state.value = HomeTabOneState.IsLoading(false)
                        }
                        is BaseResult.Error -> {
                            state.value = HomeTabOneState.IsLoading(false)
                            state.value = HomeTabOneState.ErrorStates(result.rawResponse)
                        }
                    }
                }
        }
    }

    private fun getRestOfInformation() {
        viewModelScope.launch(Dispatchers.Main) {
            val documents = async {
                candidateUseCase.getItemsDocumentsHome().collect { homeDocumentList = it.data }
            }
            val profile = async {
                candidateUseCase.getItemsProfileHome().collect { result ->
                    when (result) {
                        is BaseResult.Success -> {
                            homeProfileList = result.data
                            homeProfileList.forEach {
                                if (it.type == _accutestTitle) {
                                    evaluationExpired = when (it.status) {
                                        LoadedStatus.EXPIRED -> {
                                            evaluationExpiredTitle = R.string.tv_title_expired_evaluation
                                            true
                                        }
                                        LoadedStatus.PENDING -> {
                                            evaluationExpiredTitle = R.string.tv_title_pending_evaluation
                                            true
                                        }
                                        else -> false
                                    }
                                }
                            }
                        }
                        is BaseResult.Error -> state.value = HomeTabOneState.ErrorStates(result.rawResponse)
                    }
                }
            }
            documents.await()
            profile.await()
            getNameAndPhoto()
        }
    }

    private fun getNameAndPhoto() {
        viewModelScope.launch(Dispatchers.Main) {
            candidateUseCase.getProfileInformation()
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> {
                            photoUrl = result.data.avatarURL ?: ""
                            userFullName = getFullName(result.data)
                            delay(_delay20)
                            state.value = HomeTabOneState.UpdateUserName
                        }
                        is BaseResult.Error -> state.value = HomeTabOneState.ErrorStates(result.rawResponse)
                    }
                }
        }
    }

    fun getPositionsAndRefreshViews() {
        viewModelScope.launch(Dispatchers.Main) {
            candidateUseCase.getPreferableJobs().collect { result ->
                when (result) {
                    is BaseResult.Success -> {
                        var positions = ""
                        jobsListAsString = ""
                        jobsSelected = result.data
                        jobsSelected.forEach {
                            positions = "$positions / ${it.description}"
                            jobsListAsString += "\n${it.description}\n"
                        }; userPositions = positions.replaceFirst("/", "")
                    }
                    is BaseResult.Error -> state.value =
                        HomeTabOneState.ErrorStates(result.rawResponse)
                }
            }
        }
        getRestOfInformation()
        callUserStateService()
    }

    fun onUpdatePhotoClicked(v: View?) {
        state.value = HomeTabOneState.OpenBottomSheetDialog
    }

    fun onDocumentItemClicked(item: ItemHomeDocument) {
        typeFileSelected = item.type
        if (item.status == LoadedStatus.COMPLETED) {
            state.value = HomeTabOneState.AskForUpdateWhenCompletedDocument(item)
        } else {
            state.value = HomeTabOneState.PickDocument(item)
        }
    }

    fun onEditJobsClicked(v: View?) {
        val directions = HomeFragmentDirections.actionHomeFragmentToInterestsPositions()
        state.value = HomeTabOneState.OpenJobsScreen(directions)
    }

    fun onShowHideProfileInfoClicked(v: View?) {
        showProfileInfo = if (showProfileInfo) {
            v?.rotateUp(); false
        } else {
            v?.rotateDown(); true
        }
    }

    fun onRegisterClicked(v: View?) {
        when (lastScreen) {
            _indexOne -> {
                val directions = HomeFragmentDirections.actionHomeFragmentToRegisterComplementaryNavigation()
                state.value = HomeTabOneState.OpenComplementaryRegisterScreen(directions)
            }
            else -> state.value = HomeTabOneState.OpenResumptionScreen(R.string.deep_link_resumption)
        }
    }

    fun onAccutestClicked(v: View?) {
        if (complementaryRegisterFinished) {
            val directions = HomeFragmentDirections.actionHomeFragmentToAccutestNavigation()
            state.value = HomeTabOneState.OpenAccutestScreen(directions)
        } else {
            state.value = HomeTabOneState.InformComplementaryRegisterIncomplete
        }
    }

    private fun callUserStateService() {
        viewModelScope.launch(Dispatchers.Main) {
            authUseCase.checkForUserState().collect { result ->
                if (result is BaseResult.Success) {
                    if (result.data.state == USER_COMPLEMENTARY_REGISTER_STATE) {
                        // user has not finished the complementary registration yet
                        complementaryRegisterFinished = false
                    }
                }
            }
        }
    }

    fun sendPhotoToCloud() {
        viewModelScope.launch(Dispatchers.IO) {
            userPhoto?.value?.let {
                candidateUseCase.sendUserPhoto(
                    toMultiPartFile(FileNames.CANDIDATE.value, it)
                ).onStart {
                    state.value = HomeTabOneState.IsLoading(true)
                }.collect { result ->
                    state.value = HomeTabOneState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = HomeTabOneState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> {
                            photoUrl = result.data
                            getPercentageCompleted()
                            updatePhotoUrl(photoUrl)
                        }
                    }
                }
            }
        }
    }

    fun deletePendingFileOnServer(c: Context) {
        typeFileSelected?.let { type ->
            state.value = HomeTabOneState.IsLoading(true)
            viewModelScope.launch(Dispatchers.IO) {
                candidateUseCase.deleteFileFromServer(type).collect { result ->
                    when (result) {
                        is BaseResult.Success -> uploadNewFileToServer(c)
                        is BaseResult.Error -> {
                            state.value = HomeTabOneState.IsLoading(false)
                            state.value = HomeTabOneState.ErrorStates(result.rawResponse)
                        }
                    }
                }
            }
        }
    }

    private fun uploadNewFileToServer(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = context.contentResolver.openInputStream(attachedFile!!)!!.readBytes()
            val mimeType = getMimeType(context, attachedFile!!)
            val fileToSend = if (mimeType == _pdf) {
                toPDFMultiPartFile(typeFileSelected?.name ?: FileNames.TESTING.value, file)
            } else {
                toMultiPartFile(typeFileSelected?.name ?: FileNames.TESTING.value, file)
            }

            candidateUseCase.uploadFileToServer(fileToSend, typeFileSelected!!)
                .collect { result ->
                    state.value = HomeTabOneState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = HomeTabOneState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> getPercentageCompleted()
                    }
                }
        }
    }

    fun getMimeType(context: Context, uri: Uri): String? =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(uri.path?.let { File(it) }).toString())
        }

    fun deleteCloudPhoto() {
        viewModelScope.launch {
            candidateUseCase.deleteFileFromServer(FilesExpected.AVATAR).collect { result ->
                when (result) {
                    is BaseResult.Error -> state.value = HomeTabOneState.ErrorStates(result.rawResponse)
                    is BaseResult.Success -> {
                        photoUrl = ""
                        getPercentageCompleted()
                        updatePhotoUrl("")
                    }
                }
            }
        }
    }

    private fun updatePhotoUrl(url: String) {
        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(photoUrl = url),
                ImageGalleryOrCameraViewModel::class.simpleName
            )
        }
    }
}

sealed class HomeTabOneState {
    object Init : HomeTabOneState()
    object OpenBottomSheetDialog : HomeTabOneState()
    object UpdateUserName : HomeTabOneState()
    object InformComplementaryRegisterIncomplete : HomeTabOneState()
    data class IsLoading(val isLoading: Boolean) : HomeTabOneState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : HomeTabOneState()
    data class BackLoginScreen(@StringRes val deepLink: Int) : HomeTabOneState()
    data class PickDocument(val item: ItemHomeDocument) : HomeTabOneState()
    data class OpenComplementaryRegisterScreen(val direction: NavDirections) : HomeTabOneState()
    data class AskForUpdateWhenCompletedDocument(val item: ItemHomeDocument) : HomeTabOneState()
    data class OpenResumptionScreen(@StringRes val deepLink: Int) : HomeTabOneState()
    data class OpenAccutestScreen(val direction: NavDirections) : HomeTabOneState()
    data class OpenJobsScreen(val direction: NavDirections) : HomeTabOneState()
}
