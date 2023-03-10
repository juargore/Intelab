@file:Suppress("UNUSED_PARAMETER")

package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.requests.CandidateWorkExperience
import com.intelab.joblab.domain.entities.requests.Contact
import com.intelab.joblab.domain.entities.requests.WorkExperiences
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.helpers.getMonthsAfterNumber
import com.intelab.joblab.presentation.ui.helpers.getMonthsBeforeNumber
import com.intelab.joblab.presentation.ui.helpers.getMonthsInRange
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getMonthsAsStringList
import com.intelab.joblab.presentation.base.utils.NOW
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._maxEndMonth
import com.intelab.joblab.presentation.base.utils._minYear
import com.intelab.joblab.presentation.base.utils._patternDecimal
import com.intelab.joblab.presentation.base.utils._profileAddJobNo
import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileAddJobReferenceViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileAddJobReferenceState>(ProfileAddJobReferenceState.Init)
    private val calendar: Calendar = Calendar.getInstance()
    private val currentMonth = calendar[Calendar.MONTH]
    private var candidateWorkExperience = CandidateWorkExperience()
    private var currentJobSelected: PreviousJobItem? = null
    private var listOfCompanies = listOf<String>()
    private var currentJobId: Int? = null
    private var jobEndToCompare: String = ""
    val currentYear = calendar[Calendar.YEAR]
    var counterScreen = _profileAddJobNo

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var companyName by bindDelegate("")

    @get:Bindable
    var jobName by bindDelegate("")

    @get:Bindable
    var jobEnd by bindDelegate("")

    @get:Bindable
    var lastBossName by bindDelegate("")

    @get:Bindable
    var contactEmail by bindDelegate("")

    @get:Bindable
    var contactPhone by bindDelegate("")

    @get:Bindable
    var pickedStartedMonth by bindDelegate(currentMonth)

    @get:Bindable
    var pickedEndedMonth by bindDelegate(currentMonth)

    @get:Bindable
    var displayStartedMonthValues by bindDelegate(getMonthsBeforeNumber(currentMonth))

    @get:Bindable
    var displayEndedMonthValues by bindDelegate<Array<String>>(emptyArray())

    @get:Bindable
    var minStartedMonth by bindDelegate(_indexZero)

    @get:Bindable
    var minEndedYear by bindDelegate(_minYear)

    @get:Bindable
    var minEndedMonth by bindDelegate(_indexZero)

    @get:Bindable
    var maxEndedMonth by bindDelegate(displayEndedMonthValues.size - 1)

    @get:Bindable
    var maxStartedMonth by bindDelegate(displayStartedMonthValues.size - 1)

    @get:Bindable
    var nextBnEnabled by bindDelegate(true)

    @get:Bindable
    var currentJobChecked by bindDelegate(true) { _ , field ->
        jobEnd = if (field) NOW else ""
    }

    @get:Bindable
    var jobStart by bindDelegate("") { _ , _ ->
        jobEnd = if (currentJobChecked) NOW else ""
    }

    @get:Bindable
    var pickedStartedYear by bindDelegate(currentYear) { _ , field ->
        minStartedMonth = _indexZero
        pickedStartedMonth = _indexZero
        if (field == currentYear) {
            maxStartedMonth = currentMonth
            displayStartedMonthValues = getMonthsBeforeNumber(currentMonth)
        } else {
            maxStartedMonth = _maxEndMonth
            displayStartedMonthValues = getMonthsAsStringList()
        }
    }

    @get:Bindable
    var pickedEndedYear by bindDelegate(currentYear) { _ , field ->
        if (field == currentYear) {
            minEndedMonth = _indexZero
            displayEndedMonthValues = getMonthsBeforeNumber(currentMonth)
            maxEndedMonth = displayEndedMonthValues.size - 1
            pickedEndedMonth = _indexZero
        } else {
            if (pickedStartedYear == field) {
                minEndedMonth = pickedStartedMonth
                displayEndedMonthValues = getMonthsAfterNumber(pickedStartedMonth)
                maxEndedMonth = _maxEndMonth
                pickedEndedMonth = pickedStartedMonth
            } else {
                minEndedMonth = _indexZero
                displayEndedMonthValues = getMonthsAsStringList()
                maxEndedMonth = displayEndedMonthValues.size - 1
                pickedEndedMonth = _indexZero
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                userFullName = getFullName(cr)
            }
        }
    }

    fun initialize(jobId: Int, fullJobObject: CandidateWorkExperience, listOfCompany: Array<String>) {
        if (jobId == 0) {
            // User is adding new record -> currentJobSelected is null
            currentJobSelected = null
            currentJobId = null
        } else {
            // User is updating an existing record -> currentJobSelected must not be null
            fullJobObject.workExperiences.forEach {
                if (it.recordId == jobId) {
                    currentJobSelected = it.toPreviousJobItem()
                    currentJobId = jobId
                    return@forEach
                }
            }
        }

        // in both cases we must send the full object to Server
        listOfCompanies = listOfCompany.toList()
        candidateWorkExperience = fullJobObject
        setInitialInformationIfEditingJob()
    }

    private fun setInitialInformationIfEditingJob() {
        currentJobSelected?.let { job ->
            companyName = job.company
            jobName = job.jobName
            currentJobChecked = job.current
            jobStart = job.jobStart
            lastBossName = job.bossName
            contactEmail = job.contactEmail ?: ""
            contactPhone = job.contactPhone ?: ""
            if (jobStart.isNotEmpty()) {
                pickedStartedYear = jobStart.toStartedYear()
                pickedStartedMonth = jobStart.toStartedMonth()
                onDatePickerOkClicked(_indexZero)
            }
            jobEnd = if (currentJobChecked) NOW else job.jobEnd
            jobEndToCompare = job.jobEnd
            if (jobEnd.isNotEmpty() && jobEnd != NOW) {
                pickedEndedYear = jobEnd.toStartedYear()
                pickedEndedMonth = jobEnd.toStartedMonth()
            }
        }
    }

    fun onInitialJobDateClicked() {
        state.value = ProfileAddJobReferenceState.OpenDatePickerDialog(_indexZero)
    }

    fun onEndJobDateClicked() {
        if (jobStart.isEmpty() || currentJobChecked) return
        state.value = ProfileAddJobReferenceState.OpenDatePickerDialog(_indexOne)
    }

    fun onDatePickerOkClicked(whichDate: Int) {
        when (whichDate) {
            0 -> {
                jobStart =
                    "$pickedStartedYear-${DecimalFormat(_patternDecimal).format(pickedStartedMonth + 1)}"
                if (pickedStartedYear == currentYear) {
                    minEndedMonth = pickedStartedMonth
                    minEndedYear = pickedStartedYear
                    displayEndedMonthValues =
                        getMonthsInRange(pickedStartedMonth, currentMonth)
                    maxEndedMonth = displayEndedMonthValues.size + pickedStartedMonth - 1
                } else {
                    minEndedMonth = pickedStartedMonth
                    minEndedYear = pickedStartedYear
                    pickedEndedYear = pickedStartedYear
                    displayEndedMonthValues = getMonthsAfterNumber(pickedStartedMonth)
                    maxEndedMonth = _maxEndMonth
                }
            }
            1 -> jobEnd = "$pickedEndedYear-${DecimalFormat(_patternDecimal).format(pickedEndedMonth + 1)}"
        }
    }

    fun onSaveClicked(v: View?) {
        val newJobItem = PreviousJobItem(
            company = companyName,
            jobName = jobName,
            jobStart = jobStart,
            jobEnd = jobEndToCompare,
            id = currentJobId ?: _indexZero,
            current = currentJobChecked,
            bossName = lastBossName,
            contactEmail = contactEmail,
            contactPhone = contactPhone
        )
        if (referenceIsTheSame(currentJobSelected, newJobItem)) {
            // The reference was not modified -> return to previous screen
            state.value = ProfileAddJobReferenceState.BackJobReferencesScreen(null)
        } else {
            // The reference was modified -> Continue normal process
            if (contactEmail.isNotEmpty()) {
                if (!contactEmail.isValidEmail()) {
                    state.value = ProfileAddJobReferenceState.OnErrorValidation(R.string.dialog_profile_description_invalid_email)
                    return
                }
            }

            if (contactPhone.isNotEmpty()) {
                if (!contactPhone.isValidPhone()) {
                    state.value = ProfileAddJobReferenceState.OnErrorValidation(R.string.dialog_profile_description_invalid_phone)
                    return
                }
            }
            continueAfterValidation()
        }
    }

    private fun continueAfterValidation() {
        // if user is adding new record -> check if company exists previously
        if (currentJobId == null) {
            if (listOfCompanies.contains(companyName.trim())) {
                state.value = ProfileAddJobReferenceState.OnErrorValidation(R.string.tv_body_company_already_exists)
                return
            }
        }

        if (companyName.isEmpty() || jobName.isEmpty() ||
            jobStart.isEmpty() || jobEnd.isEmpty() || lastBossName.isEmpty()
        ) {
            state.value = ProfileAddJobReferenceState.OnErrorValidation(R.string.tv_title_complete_mandatory)
        } else {
            nextBnEnabled = false
            viewModelScope.launch {
                candidateUseCase.deleteAllJobReferences()
                    .collect { result ->
                        when (result) {
                            is BaseResult.Error -> {
                                state.value = ProfileAddJobReferenceState.OnError(result.rawResponse)
                                nextBnEnabled = true
                            }
                            is BaseResult.Success -> addNewValues()
                        }
                    }
            }
        }
    }

    private fun addNewValues() {
        viewModelScope.launch {
            val newOrUpdatedWork = WorkExperiences(
                companyName = companyName,
                position = jobName,
                startDate = jobStart.appendDayAtEnd(),
                endDate = jobEnd.appendDayAtEnd(),
                contact = Contact(
                    employerName = lastBossName,
                    email = contactEmail,
                    phoneNumber = contactPhone
                ),
                current = currentJobChecked,
                recordId = currentJobId
            )
            val reference = candidateWorkExperience.apply {
                if (currentJobId == null) {
                    // add new record to workExperiencesList
                    workExperiences.add(newOrUpdatedWork)
                } else {
                    // update existing record on workExperiencesList
                    workExperiences.forEach { work ->
                        if (work.recordId == currentJobId) {
                            workExperiences.remove(work)
                            workExperiences.add(newOrUpdatedWork)
                            return@forEach
                        }
                    }
                }
            }

            candidateUseCase.addNewJobReferenceInformation(reference)
                .onStart { ProfileAddJobReferenceState.IsLoading(true) }
                .collect { result ->
                    ProfileAddJobReferenceState.IsLoading(false)
                    nextBnEnabled = true
                    when (result) {
                        is BaseResult.Success -> state.value =
                            ProfileAddJobReferenceState.BackJobReferencesScreen(null)
                        is BaseResult.Error -> state.value =
                            ProfileAddJobReferenceState.OnError(result.rawResponse)
                    }
                }
        }
    }

    private fun referenceIsTheSame(v1: PreviousJobItem?, v2: PreviousJobItem) =
        v1?.id == v2.id &&
                v1.company.trim() == v2.company.trim() &&
                v1.jobEnd.trim() == v2.jobEnd.trim() &&
                v1.jobStart.trim() == v2.jobStart.trim() &&
                v1.current == v1.current &&
                v1.contactEmail?.trim() == v1.contactEmail?.trim() &&
                v1.contactPhone?.trim() == v2.contactPhone?.trim() &&
                v1.bossName.trim() == v2.bossName.trim() &&
                v1.jobName.trim() == v2.jobName.trim()

    fun onCancelClicked(v: View?) {
        state.value = ProfileAddJobReferenceState.BackJobReferencesScreen(null)
    }
}

sealed class ProfileAddJobReferenceState {
    object Init : ProfileAddJobReferenceState()
    data class IsLoading(val isLoading: Boolean) : ProfileAddJobReferenceState()
    data class OnError(val rawResponse: ErrorGenericResponse) : ProfileAddJobReferenceState()
    data class OpenDatePickerDialog(var whichDate: Int) : ProfileAddJobReferenceState()
    data class OnErrorValidation(@StringRes val message: Int) : ProfileAddJobReferenceState()
    data class BackJobReferencesScreen(val item: PreviousJobItem?) : ProfileAddJobReferenceState()
}
