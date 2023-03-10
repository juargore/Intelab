package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.EducationLvlUI
import com.intelab.joblab.domain.entities.EducationStatusUI
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.requests.Educations
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfileAcademicFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils._delay50
import com.intelab.joblab.presentation.base.utils._indexEight
import com.intelab.joblab.presentation.base.utils._indexFour
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexThree
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._profileAcademicNo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileAcademicViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val candidateUseCase: CandidateUseCase,
    val catalogUseCase: CatalogUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileAcademicState>(ProfileAcademicState.Init)
    var counterScreen = _profileAcademicNo
    private var listOfRecords = mutableListOf<Int>()
    private var academicInitial = Educations()
    private var recordId = _indexZero

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var educationLevelsList by bindDelegate<List<EducationLvlUI>>(listOf())

    @get:Bindable
    var educationLevel by bindDelegate<EducationLvlUI?>(null)

    @get:Bindable
    var educationStatusList by bindDelegate<List<EducationStatusUI>>(listOf())

    @get:Bindable
    var educationStatus by bindDelegate<EducationStatusUI?>(null)

    @get:Bindable
    var institution by bindDelegate("")

    @get:Bindable
    var profesion by bindDelegate("")

    @get:Bindable
    var professionId by bindDelegate("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                userFullName = getFullName(cr)
                getAcademicInformationFromServer()
            }
        }
    }

    private fun getAcademicInformationFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            val educationLevelsAsync = async {
                catalogUseCase.getEducationLevels()
                    .onStart { loadingWithDelay(this@ProfileAcademicViewModel, true) }
                    .collect { result ->
                        when (result) {
                            is BaseResult.Error -> state.value = ProfileAcademicState.IsLoading(false)
                            is BaseResult.Success -> {
                                result.data.forEach {
                                    if (it.id == _indexFour) { // long name
                                        it.educationLvlName =
                                            "${it.educationLvlName.substringBefore("(")}\n(${
                                                it.educationLvlName.substringAfter("(")
                                            }"
                                        return@forEach
                                    }
                                }
                                educationLevelsList = result.data
                            }
                        }
                    }
            }

            val educationStatusAsync = async {
                catalogUseCase.getEducationStatus().collect { result ->
                    when (result) {
                        is BaseResult.Error -> loadingWithDelay(this@ProfileAcademicViewModel, false)
                        is BaseResult.Success -> educationStatusList = result.data
                    }
                    delay(_delay50)
                    getCandidateInformationFromServer()
                }
            }

            educationLevelsAsync.await()
            educationStatusAsync.await()
        }
    }

    private fun getCandidateInformationFromServer() {
        viewModelScope.launch {
            candidateUseCase.getAcademicInformation().collect { result ->
                when (result) {
                    is BaseResult.Error -> {
                        loadingWithDelay(this@ProfileAcademicViewModel, false)
                        state.value = ProfileAcademicState.OnError(result.rawResponse)
                    }
                    is BaseResult.Success -> {
                        with(result.data) {
                            if (isNotEmpty()) {
                                if (size == 1) {
                                    val cr = this[_indexZero]
                                    academicInitial = this[_indexZero]
                                    recordId = cr.recordId ?: _indexZero
                                    cr.levelId?.let { educationLevel = educationLevelsList[it.toInt() - 1] }
                                    cr.statusId?.let { educationStatus = educationStatusList[it.toInt() - 1] }
                                    cr.institutionName?.let { institution = it }
                                    cr.speciality?.let { profesion = it }
                                    cr.professionalIdentificationCode?.let { professionId = it }
                                } else {
                                    result.data.forEach { educations ->
                                        educations.recordId?.let { listOfRecords.add(it) }
                                    }
                                }
                            }
                        }
                    }
                }
                loadingWithDelay(this@ProfileAcademicViewModel, false)
            }
        }
    }

    fun onSaveAndExitClicked() {
        if (professionId.isNotEmpty() && professionId.length < _indexEight) {
            state.value = ProfileAcademicState.OnErrorValidation(R.string.et_error_message_min_length_profession_id)
            return
        }
        if (isInformationValid()) {
            listOfRecords.forEach { deleteUnnecessaryRecords(it) }
            val academic = Educations(
                levelId = educationLevel?.id.toString(),
                statusId = educationStatus?.id.toString(),
                speciality = profesion,
                institutionName = institution,
                professionalIdentificationCode = professionId,
                startDate = "",
                endDate = "",
                recordId = if (recordId == _indexZero) null else recordId
            )
            viewModelScope.launch {
                val request = if (recordId == _indexZero) {
                    candidateUseCase.addNewAcademicInformation(academic)
                } else {
                    candidateUseCase.sendAcademicInformationUpdate(academic)
                }
                request
                    .onStart { loadingWithDelay(this@ProfileAcademicViewModel, true) }
                    .collect { result ->
                        loadingWithDelay(this@ProfileAcademicViewModel, false)
                        when (result) {
                            is BaseResult.Error -> state.value = ProfileAcademicState.OnError(result.rawResponse)
                            is BaseResult.Success -> state.value = ProfileAcademicState.ExitScreen
                        }
                    }
            }
        } else {
            state.value = ProfileAcademicState.OnErrorValidation(R.string.tv_title_complete_mandatory)
        }
    }

    private fun isInformationValid() = !(institution.isEmpty() || if (isWithoutProfession()) false else profesion.isEmpty())

    fun onNextClicked() {
        if (professionId.isNotEmpty() && professionId.length < _indexEight) {
            state.value = ProfileAcademicState.OnErrorValidation(R.string.et_error_message_min_length_profession_id)
            return
        }
        if (isInformationValid()) {
            listOfRecords.forEach { deleteUnnecessaryRecords(it) }
            val direction = ProfileAcademicFragmentDirections.actionProfileAcademicToProfileJobReference()
            val academic = Educations(
                levelId = educationLevel?.id.toString(),
                statusId = educationStatus?.id.toString(),
                speciality = profesion,
                institutionName = institution,
                professionalIdentificationCode = professionId,
                startDate = "",
                endDate = "",
                recordId = if (recordId == _indexZero) null else recordId
            )
            if (academicInitial == academic) {
                // no changes -> just go to next screen
                state.value = ProfileAcademicState.OpenProfileJobReferenceScreen(direction)
            } else {
                // at least one change -> update on server
                viewModelScope.launch {
                    if (recordId == _indexZero) {
                        candidateUseCase.addNewAcademicInformation(academic)
                    } else {
                        candidateUseCase.sendAcademicInformationUpdate(academic)
                    }.onStart { loadingWithDelay(this@ProfileAcademicViewModel, true) }
                        .collect { result ->
                            loadingWithDelay(this@ProfileAcademicViewModel, false)
                            when (result) {
                                is BaseResult.Error -> state.value = ProfileAcademicState.OnError(result.rawResponse)
                                is BaseResult.Success -> state.value = ProfileAcademicState.OpenProfileJobReferenceScreen(direction)
                            }
                        }
                }
            }
        } else {
            state.value = ProfileAcademicState.OnErrorValidation(R.string.tv_title_complete_mandatory)
        }
    }

    private fun deleteUnnecessaryRecords(id: Int) {
        viewModelScope.launch {
            candidateUseCase.deleteAcademicInformation(id).collect {
                when (it) {
                    is BaseResult.Success -> Unit
                    is BaseResult.Error -> state.value = ProfileAcademicState.OnError(it.rawResponse)
                }
            }
        }
    }

    private fun isWithoutProfession(): Boolean {
        return educationLevel?.id == _indexOne || educationLevel?.id == _indexTwo
                || educationLevel?.id == _indexThree || educationLevel?.id == _indexFour
    }
}

sealed class ProfileAcademicState {
    object Init : ProfileAcademicState()
    object ExitScreen : ProfileAcademicState()
    data class IsLoading(val isLoading: Boolean) : ProfileAcademicState()
    data class OnError(val rawResponse: ErrorGenericResponse) : ProfileAcademicState()
    data class OnErrorValidation(@StringRes val message: Int) : ProfileAcademicState()
    data class OpenProfileJobReferenceScreen(val direction: NavDirections) : ProfileAcademicState()
}
