package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.IdRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.EducationLvlUI
import com.intelab.joblab.domain.entities.EducationStatusUI
import com.intelab.joblab.domain.entities.ErrorResponse
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexEight
import com.intelab.joblab.presentation.base.utils._indexFour
import com.intelab.joblab.presentation.base.utils._indexNine
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexSeven
import com.intelab.joblab.presentation.base.utils._indexSix
import com.intelab.joblab.presentation.base.utils._indexThree
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._jobReferences
import com.intelab.joblab.presentation.ui.home.register.fragment.AcademicFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcademicViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<AcademicState>(AcademicState.Init)
    var screen = _indexNine
    var step = _indexSeven
    val advance = _indexSix
    var screenName = _jobReferences

    @get:Bindable
    var educationLevelsList by bindDelegate(listOf<EducationLvlUI>())

    @get:Bindable
    var educationLevel by bindDelegate<EducationLvlUI?>(null) { _, _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var educationStatusList by bindDelegate(listOf<EducationStatusUI>())

    @get:Bindable
    var educationStatus by bindDelegate<EducationStatusUI?>(null) { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var institution by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var profesion by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var professionId by bindDelegate("") { _ , field ->
        errorMessage = if (field.isNotEmpty() && field.length < _indexEight)
            R.string.et_error_message_min_length_profession_id else null
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var errorMessage by bindDelegate<Int?>(null)

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    init {
        getEducationLevelsAndStatus()
    }

    fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    educationLevel = educationLevel,
                    educationStatus = educationStatus,
                    institution = institution,
                    profession = profesion,
                    professionCode = professionId,
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), AcademicViewModel::class.simpleName
            )
        }
        val directions = AcademicFragmentDirections.actionAcademicFragmentToJobReferencesFragment()
        state.value = AcademicState.OpenJobReferenceScreen(directions)
    }

    fun onBackClicked() {
        val directions = AcademicFragmentDirections.actionAcademicFragmentToEconomicFragment()
        state.value = AcademicState.BackEconomicScreen(R.id.economicFragment, directions)
    }

    private fun getEducationLevelsAndStatus() {
        launch {
            val educationLevelsAsync = async {
                catalogUseCase.getEducationLevels()
                    .onStart { state.value = AcademicState.IsLoading(true) }
                    .collect { result ->
                        if (result is BaseResult.Success) {
                            result.data.forEach {
                                if (it.id == _indexFour) {
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

            val educationStatusAsync = async {
                catalogUseCase.getEducationStatus()
                    .onStart { state.value = AcademicState.IsLoading(true) }
                    .collect { result ->
                        if (result is BaseResult.Success) {
                            educationStatusList = result.data
                        }
                    }
            }

            educationLevelsAsync.await()
            educationStatusAsync.await()
            loadDataFromDb()

            state.value = AcademicState.IsLoading(false)
        }
    }

    private fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.educationLevel?.let { educationLevel = it }
                cr.educationStatus?.let { educationStatus = it }
                cr.institution?.let { institution = it }
                cr.profession?.let { profesion = it }
                cr.professionCode?.let { professionId = it }
                cr.screen?.let {
                    if (it > screen) {
                        cr.screenName?.let { sn -> screenName = sn }
                        cr.step?.let { s -> step = s }
                        screen = it
                    }
                }
            }
        }
    }

    private fun isNextBnEnabled(): Boolean {
        return educationLevel != null && educationStatus != null && institution.isNotEmpty()
                && if (isWithoutProfession()) true else profesion.isNotEmpty() && errorMessage == null
    }

    private fun isWithoutProfession(): Boolean {
        return educationLevel?.id == _indexOne || educationLevel?.id == _indexTwo
                || educationLevel?.id == _indexThree || educationLevel?.id == _indexFour
    }
}

sealed class AcademicState {
    object Init : AcademicState()
    data class IsLoading(val isLoading: Boolean) : AcademicState()
    data class ErrorStates(val rawResponse: ErrorResponse) : AcademicState()
    data class OpenJobReferenceScreen(val direction: NavDirections) : AcademicState()
    data class BackEconomicScreen(@IdRes val id: Int, val directions: NavDirections) : AcademicState()
}
