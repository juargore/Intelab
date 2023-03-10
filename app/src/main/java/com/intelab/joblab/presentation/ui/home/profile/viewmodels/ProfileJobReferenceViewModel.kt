package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.requests.CandidateWorkExperience
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfileJobReferenceFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils._delay50
import com.intelab.joblab.presentation.base.utils._indexEleven
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._profileAddJobNo
import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileJobReferenceViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileJobReferenceState>(ProfileJobReferenceState.Init)
    var counterScreen = _profileAddJobNo
    private var candidateWorkExperience: CandidateWorkExperience? = null
    private var securitySocialNumberInitial = ""
    private var listOfCompanies = listOf<String>()

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var previousJobs by bindDelegate(listOf<PreviousJobItem>())

    @get:Bindable
    var errorMessage by bindDelegate<Int?>(null)

    @get:Bindable
    var securitySocialNumberLabel by bindDelegate(R.string.tv_security_social_number)

    @get:Bindable
    var selectedExperience by bindDelegate(_indexZero) { _, field ->
        securitySocialNumberLabel =
            if (field == _indexTwo) R.string.tv_security_social_number else R.string.tv_security_social_number_required
    }

    @get:Bindable
    var securitySocialNumber by bindDelegate("") { _ , field ->
        errorMessage = if (field.length < _indexEleven && field.isNotEmpty()) {
            R.string.et_error_message_min_length
        } else null
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                userFullName = getFullName(cr)
            }
        }
    }

    fun getJobReferencesFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            candidateUseCase.getJobReferencesInformation()
                .onStart { state.value = ProfileJobReferenceState.IsLoading(true) }
                .collect { result ->
                    state.value = ProfileJobReferenceState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileJobReferenceState.OnError(result.rawResponse)
                        is BaseResult.Success -> {
                            candidateWorkExperience = result.data
                            securitySocialNumber = result.data.socialIdentificationCode ?: ""
                            securitySocialNumberInitial = result.data.socialIdentificationCode ?: ""
                            selectedExperience = if (result.data.workExperiences.isEmpty()) 2 else 1
                            previousJobs = result.data.workExperiences.map { it.toPreviousJobItem() }
                            listOfCompanies = result.data.workExperiences.map { it.toOnlyCompanyName() ?: "" }
                        }
                    }
                }
        }
    }

    fun onWorkExperienceYesClicked() { selectedExperience = _indexOne }

    fun onWorkExperienceNoClicked() { selectedExperience = _indexTwo }

    val onShowDeleteJobReferenceDialog: (Int) -> Unit = {
        state.value = ProfileJobReferenceState.OpenDialog(R.string.dialog_message_delete_job_reference, it)
    }

    val onEditJobReference: (Int) -> Unit = { id ->
        candidateWorkExperience?.let { work ->
            previousJobs.forEach {
                if (it.id == id) {
                    state.value = ProfileJobReferenceState.OpenAddJobReferenceScreen(
                        ProfileJobReferenceFragmentDirections.actionProfileJobReferenceToProfileAddJobReference(
                            id, work, listOfCompanies.toTypedArray()
                        )
                    )
                    return@forEach
                }
            }
        }
    }

    fun deleteJobReference(id: Int, refreshScreen: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateUseCase.deleteJobReference(id)
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> if (refreshScreen) getJobReferencesFromServer()
                        is BaseResult.Error -> state.value = ProfileJobReferenceState.OnError(result.rawResponse)
                    }
                }
        }
    }

    fun onAddClicked() {
        candidateWorkExperience?.let { work ->
            state.value = ProfileJobReferenceState.OpenAddJobReferenceScreen(
                ProfileJobReferenceFragmentDirections.actionProfileJobReferenceToProfileAddJobReference(
                    _indexZero, work, listOfCompanies.toTypedArray()
                )
            )
        }
    }

    fun onSaveAndExitClicked() {
        if (isInformationValid()) {
            if (selectedExperience == _indexTwo) {
                previousJobs.forEach {
                    deleteJobReference(it.id)
                }
            }
            viewModelScope.launch(Dispatchers.IO) {
                candidateUseCase.updateSocialIdentificationCodeRequest(securitySocialNumber)
                    .onStart {
                        state.value = ProfileJobReferenceState.IsLoading(true)
                        if (selectedExperience == _indexTwo) delay(_delay50)
                    }
                    .collect { result ->
                        state.value = ProfileJobReferenceState.IsLoading(false)
                        when (result) {
                            is BaseResult.Success -> state.value = ProfileJobReferenceState.ExitScreen
                            is BaseResult.Error -> state.value = ProfileJobReferenceState.OnError(result.rawResponse)
                        }
                    }
            }
        } else {
            state.value = ProfileJobReferenceState.OnErrorValidation(R.string.tv_title_complete_mandatory)
        }
    }

    private fun isInformationValid(): Boolean {
        if (selectedExperience == _indexTwo && if (securitySocialNumber.isNotEmpty()) securitySocialNumber.length == _indexEleven else true)
            return true

        if (selectedExperience == _indexOne && securitySocialNumber.length == _indexEleven && previousJobs.isNotEmpty())
            return true

        return false
    }

    fun onNextClicked() {
        if (isInformationValid()) {
            if (selectedExperience == _indexTwo) {
                previousJobs.forEach {
                    deleteJobReference(it.id)
                }
            }
            val direction = ProfileJobReferenceFragmentDirections.actionProfileJobReferenceToProfileSocialMedia()
            if (securitySocialNumberInitial == securitySocialNumber) {
                // no changes on screen
                state.value = ProfileJobReferenceState.OpenProfileSocialMediaScreen(direction)
            } else {
                // changed the security social number and go to next screen
                viewModelScope.launch {
                    candidateUseCase.updateSocialIdentificationCodeRequest(securitySocialNumber)
                        .onStart {
                            state.value = ProfileJobReferenceState.IsLoading(true)
                            if (selectedExperience == _indexTwo) delay(_delay50)
                        }
                        .collect { result ->
                            state.value = ProfileJobReferenceState.IsLoading(false)
                            when (result) {
                                is BaseResult.Success -> state.value = ProfileJobReferenceState.OpenProfileSocialMediaScreen(direction)
                                is BaseResult.Error -> state.value = ProfileJobReferenceState.OnError(result.rawResponse)
                            }
                        }
                }
            }
        } else {
            state.value = ProfileJobReferenceState.OnErrorValidation(R.string.tv_title_complete_mandatory)
        }
    }
}

sealed class ProfileJobReferenceState {
    object Init : ProfileJobReferenceState()
    object ExitScreen : ProfileJobReferenceState()
    data class IsLoading(val isLoading: Boolean) : ProfileJobReferenceState()
    data class OpenProfileSocialMediaScreen(val direction: NavDirections) : ProfileJobReferenceState()
    data class OpenDialog(@StringRes val stringId: Int, val jobReferenceId: Int) : ProfileJobReferenceState()
    data class OpenAddJobReferenceScreen(val direction: NavDirections) : ProfileJobReferenceState()
    data class OnErrorValidation(@StringRes val message: Int) : ProfileJobReferenceState()
    data class OnError(val rawResponse: ErrorGenericResponse) : ProfileJobReferenceState()
}
