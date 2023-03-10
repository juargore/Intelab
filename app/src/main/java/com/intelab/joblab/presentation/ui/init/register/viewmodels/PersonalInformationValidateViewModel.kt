package com.intelab.joblab.presentation.ui.init.register.viewmodels

import android.net.Uri
import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.requests.CandidateRequest
import com.intelab.joblab.domain.entities.requests.PreferableJobs
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.extensions.upperCaseDefault
import com.intelab.joblab.presentation.ui.helpers.images.ImageGalleryOrCameraViewModel
import com.intelab.joblab.presentation.ui.helpers.toMultiPartFile
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.FileNames
import com.intelab.joblab.presentation.base.utils._national
import com.intelab.joblab.presentation.ui.init.register.fragments.PersonalInformationValidateFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInformationValidateViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val candidateUseCase: CandidateUseCase,
    val authUseCase: AuthUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<PersonalInformationValidateState>(PersonalInformationValidateState.Init)
    var firstName = ""
    var otherNames = ""
    var fatherLastName = ""
    var motherLastName = ""
    var userPhoto: DataArray? = null

    @get:Bindable
    var userName by bindDelegate("")

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var userEmail by bindDelegate("")

    @get:Bindable
    var userPhone by bindDelegate("")

    @get:Bindable
    var userCurp by bindDelegate("")

    @get:Bindable
    var jobPostulations by bindDelegate("")

    @get:Bindable
    var nationality by bindDelegate("")

    fun getDbRegistrationData() {
        launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                firstName = cr.firstName ?: ""
                otherNames = cr.otherNames ?: ""
                fatherLastName = cr.fatherLastName ?: ""
                motherLastName = cr.motherLastName ?: ""
                userFullName = getFullName(cr)
                userName = cr.email ?: ""
                userEmail = cr.email ?: ""
                userPhone = cr.phone ?: ""
                userCurp = cr.curp ?: ""
                nationality = cr.nationality ?: ""
                if (cr.photoUri != null)
                    state.value = PersonalInformationValidateState.UriLoaded(cr.photoUri)
                getAllJobPostulations()
            }
        }
    }

    private fun getAllJobPostulations() {
        launch(Dispatchers.IO) {
            dbUseCase.getAllJobPostulation().collect { result ->
                result.forEachIndexed { i, value ->
                    if (i == 0) {
                        jobPostulations = value.description
                    } else {
                        jobPostulations += ", ${value.description}"
                    }
                }
            }
        }
    }

    private fun sendPreferableJobsAndPhoto() {
        launch {
            var preferableJobsSuccess = false
            val preferableJobs = async {
                dbUseCase.getAllJobPostulation().flowOn(Dispatchers.IO).collect { postulations ->
                    val ids = postulations.map { it.id.toString() }
                    candidateUseCase.sendPreferableJobs(ids.map { PreferableJobs(id = it) })
                        .collect { result ->
                            when (result) {
                                is BaseResult.Error -> state.value = PersonalInformationValidateState
                                    .ErrorPersonalInfoValidation(result.rawResponse)
                                is BaseResult.Success -> {
                                    preferableJobsSuccess = true
                                    deleteAllStoredPostulations()
                                }
                            }
                        }
                }
            }
            val updateAvatar = async {
                userPhoto?.value?.let {
                    candidateUseCase.sendUserPhoto(toMultiPartFile(FileNames.CANDIDATE.value, it))
                        .collect { result ->
                            when (result) {
                                is BaseResult.Error -> state.value =
                                    PersonalInformationValidateState.ErrorPersonalInfoValidation(result.rawResponse)
                                is BaseResult.Success -> saveUrlPhotoOnLocalDb(result.data)
                            }
                        }
                }
            }

            preferableJobs.await()
            updateAvatar.await()
            state.value = PersonalInformationValidateState.IsLoading(false)

            if (preferableJobsSuccess) {
                state.value = PersonalInformationValidateState.OpenHomeScreen(R.string.deep_link_home_screen, R.id.app_navigation, true)
            }
        }
    }

    private fun deleteAllStoredPostulations() {
        launch(Dispatchers.IO) { dbUseCase.deleteAllJobPostulation() }
    }

    private fun saveUrlPhotoOnLocalDb(data: String) {
        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(photoUrl = data),
                ImageGalleryOrCameraViewModel::class.simpleName
            )
        }
    }

    fun onYesClicked() {
        viewModelScope.launch {
            val foreign = nationality != _national
            val candidateRequest = CandidateRequest(
                firstName = firstName,
                middleName = otherNames,
                surnameMaternal = motherLastName,
                surnamePaternal = fatherLastName,
                identificationCode = if (userCurp.isEmpty()) null else userCurp.upperCaseDefault(),
                phoneNumber = userPhone,
                foreign = foreign
            )
            candidateUseCase.registerNewCandidateOnServer(candidateRequest)
                .onStart { loadingWithDelay(this@PersonalInformationValidateViewModel, true) }
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> sendPreferableJobsAndPhoto()
                        is BaseResult.Error -> {
                            loadingWithDelay(this@PersonalInformationValidateViewModel, false)
                            state.value = PersonalInformationValidateState.ErrorPersonalInfoValidation(result.rawResponse)
                        }
                    }
                }
        }
    }

    fun onNoClicked() {
        state.value = PersonalInformationValidateState.BackPersonalInformationScreen(
            PersonalInformationValidateFragmentDirections.
            actionPersonalInformationValidateFragmentToPersonalInformationFragment()
        )
    }
}

sealed class PersonalInformationValidateState {
    object Init : PersonalInformationValidateState()
    data class IsLoading(val isLoading: Boolean) : PersonalInformationValidateState()
    data class UriLoaded(val uri: Uri) : PersonalInformationValidateState()
    data class BackPersonalInformationScreen(val directions: NavDirections) : PersonalInformationValidateState()
    data class ErrorPersonalInfoValidation(val rawResponse: ErrorGenericResponse) : PersonalInformationValidateState()
    data class OpenHomeScreen(
        @StringRes val deepLink: Int,
        val popUpTo: Int?,
        val popUpToInclusive: Boolean
    ) : PersonalInformationValidateState()
}
