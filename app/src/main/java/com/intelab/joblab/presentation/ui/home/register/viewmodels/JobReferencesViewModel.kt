package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexEight
import com.intelab.joblab.presentation.base.utils._indexEleven
import com.intelab.joblab.presentation.base.utils._indexNine
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexSeven
import com.intelab.joblab.presentation.base.utils._indexTen
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._jobReferences
import com.intelab.joblab.presentation.base.utils._socialNetworks
import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem
import com.intelab.joblab.presentation.ui.home.register.fragment.JobReferencesFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobReferencesViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<JobReferencesState>(JobReferencesState.Init)
    var screenName = _socialNetworks
    var screen = _indexTen
    var step = _indexEight
    val advance = _indexSeven
    private var currentScreen = _indexNine
    private var currentStep = _indexSeven
    private var currentScreenName = _jobReferences
    private var listOfCompanies = listOf<String>()

    @get:Bindable
    var selectedWorkingNow by bindDelegate(_indexZero)

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var errorMessage by bindDelegate<Int?>(null)

    @get:Bindable
    var securitySocialNumberLabel by bindDelegate(R.string.tv_security_social_number)

    @get:Bindable
    var previousJobs by bindDelegate(listOf<PreviousJobItem>()) { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var securitySocialNumber by bindDelegate("") { _ , field ->
        nextBnEnabled = isNextBnEnabled()
        errorMessage = if (field.length < _indexEleven && field.isNotEmpty()) {
            R.string.et_error_message_min_length
        } else null
    }

    @get:Bindable
    var selectedExperience by bindDelegate(_indexZero) { _, field ->
        nextBnEnabled = isNextBnEnabled()
        securitySocialNumberLabel =
            if (field == _indexTwo) R.string.tv_security_social_number else R.string.tv_security_social_number_required
    }

    fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val getData = async {
                dbUseCase.getRegistrationData().collect { cr ->
                    cr.socialSecurityNumber?.let { securitySocialNumber = it }
                    cr.workExperience?.let { selectedExperience = it }
                    cr.currentlyWorking?.let { selectedWorkingNow = it }
                    cr.screen?.let {
                        if (it > screen) {
                            cr.screenName?.let { sn -> screenName = sn }
                            cr.step?.let { s -> step = s }
                            screen = it
                        }
                    }
                }
            }

            val getJobs = async {
                dbUseCase.getJobReferencesData().collect { list ->
                    previousJobs = list.map { it.toPreviousJobItem() }.toMutableList()
                    listOfCompanies = list.map { it.toCompaniesList() ?: "" }
                }
            }

            getData.await()
            getJobs.await()
        }
    }

    private fun isNextBnEnabled(): Boolean {
        if (selectedExperience == _indexTwo && if (securitySocialNumber.isNotEmpty()) securitySocialNumber.length == _indexEleven else true)
            return true

        if (selectedExperience == _indexOne && securitySocialNumber.length == _indexEleven && previousJobs.isNotEmpty())
            return true

        return false
    }

    fun onNextClicked() {
        saveUserInputData(true)
        state.value = JobReferencesState.OpenSocialMediaScreen(
            JobReferencesFragmentDirections.actionJobReferencesFragmentToSocialMediaFragment()
        )
    }

    fun onBackClicked() {
        state.value = JobReferencesState.BackAcademicScreen(R.id.academicFragment,
            JobReferencesFragmentDirections.actionJobReferencesFragmentToAcademicFragment()
        )
    }

    fun onAddClicked() {
        saveUserInputData(false)
        state.value = JobReferencesState.OpenPreviousJobInformationScreen(
            JobReferencesFragmentDirections.actionJobReferencesFragmentToPreviousJobInformationFragment(
                listOfCompanies = listOfCompanies.toTypedArray()
            )
        )
    }

    fun onWorkExperienceNoClicked() {
        selectedExperience = _indexTwo
        launch(Dispatchers.IO) { dbUseCase.deleteAllJobReferences() }
    }

    fun onWorkExperienceYesClicked() { selectedExperience = _indexOne }

    fun onWorkingNowYesClicked() { selectedWorkingNow = _indexOne }

    fun onWorkingNowNoClicked() { selectedWorkingNow = _indexTwo }

    val onShowDeleteJobReferenceDialog: (Int) -> Unit = {
        state.value = JobReferencesState.OpenDialog(R.string.dialog_message_delete_job_reference, it)
    }

    val onEditJobReference: (Int) -> Unit = { id ->
        saveUserInputData(false)
        state.value = JobReferencesState.OpenPreviousJobInformationScreen(
            JobReferencesFragmentDirections
                .actionJobReferencesFragmentToPreviousJobInformationFragment(
                    referenceJobId = id,
                    listOfCompanies = listOfCompanies.toTypedArray()
                )
        )
    }

    fun deleteJobReference(id: Int) {
        viewModelScope.launch(Dispatchers.IO) { dbUseCase.deleteJobReference(id) }
    }

    private fun saveUserInputData(onNext: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    socialSecurityNumber = securitySocialNumber,
                    workExperience = selectedExperience,
                    currentlyWorking = selectedWorkingNow,
                    screen = if (onNext) screen else currentScreen,
                    screenName = if (onNext) screenName else currentScreenName,
                    step = if (onNext) step else currentStep
                ), JobReferencesViewModel::class.simpleName
            )
        }
    }
}

sealed class JobReferencesState {
    object Init : JobReferencesState()
    data class OpenSocialMediaScreen(val direction: NavDirections) : JobReferencesState()
    data class OpenPreviousJobInformationScreen(val direction: NavDirections) : JobReferencesState()
    data class OpenDialog(@StringRes val stringId: Int, val jobReferenceId: Int) : JobReferencesState()
    data class BackAcademicScreen(@IdRes val id: Int, val directions: NavDirections) : JobReferencesState()
}
