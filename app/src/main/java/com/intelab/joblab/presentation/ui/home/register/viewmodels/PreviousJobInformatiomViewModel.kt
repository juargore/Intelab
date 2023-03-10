package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.JobReference
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.isValidEmail
import com.intelab.joblab.presentation.extensions.isValidPhone
import com.intelab.joblab.presentation.extensions.toStartedMonth
import com.intelab.joblab.presentation.extensions.toStartedYear
import com.intelab.joblab.presentation.ui.helpers.getMonthsAfterNumber
import com.intelab.joblab.presentation.ui.helpers.getMonthsBeforeNumber
import com.intelab.joblab.presentation.ui.helpers.getMonthsInRange
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getMonthsAsStringList
import com.intelab.joblab.presentation.base.utils.JOB_DATABASE_ID
import com.intelab.joblab.presentation.base.utils.NOW
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexSeven
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._maxEndMonth
import com.intelab.joblab.presentation.base.utils._minYear
import com.intelab.joblab.presentation.base.utils._patternDecimal
import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PreviousJobInformationViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    savedStateHandle: SavedStateHandle
) : ObservableViewModel() {

    val state = MutableStateFlow<PreviousJobInformationState>(PreviousJobInformationState.Init)
    private var jobReferenceDbId = _indexZero
    private val calendar: Calendar = Calendar.getInstance()
    private val currentMonth = calendar[Calendar.MONTH]
    private var listOfCompanies = listOf<String>()
    val currentYear = calendar[Calendar.YEAR]
    val advance = _indexSeven

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
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var errorMessage by bindDelegate<Int?>(null)

    @get:Bindable
    var errorMessagePhone by bindDelegate<Int?>(null)

    @get:Bindable
    var pickedStartedMonth by bindDelegate(currentMonth)

    @get:Bindable
    var companyName by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var jobName by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var jobStart by bindDelegate("") { _ , _ ->
        jobEnd = if (currentJobChecked) NOW else ""
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var jobEnd by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var lastBossName by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var currentJobChecked by bindDelegate(false) { _ , field ->
        jobEnd = if (field) NOW else ""
    }

    @get:Bindable
    var contactEmail by bindDelegate("") { _ , field ->
        errorMessage = if (field.isNotEmpty()) {
            if (field.isValidEmail()) null else R.string.dialog_profile_description_invalid_email
        } else null
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var contactPhone by bindDelegate("") { _ , field ->
        errorMessagePhone = if (field.isNotEmpty()) {
            if (field.isValidPhone()) null else R.string.dialog_profile_description_invalid_phone
        } else null
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var pickedStartedYear by bindDelegate(currentYear) { _ , field ->
        if (field == currentYear) {
            minStartedMonth = _indexZero
            maxStartedMonth = currentMonth
            displayStartedMonthValues = getMonthsBeforeNumber(currentMonth)
            pickedStartedMonth = _indexZero
        } else {
            minStartedMonth = _indexZero
            maxStartedMonth = _maxEndMonth
            displayStartedMonthValues = getMonthsAsStringList()
            pickedStartedMonth = _indexZero
        }
    }

    @get:Bindable
    var pickedEndedYear by bindDelegate(currentYear) { _ , field ->
        if (field == currentYear) {
            minEndedMonth = _indexZero
            displayEndedMonthValues = getMonthsBeforeNumber(currentMonth)
            maxEndedMonth = displayEndedMonthValues.size - _indexOne
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
                maxEndedMonth = displayEndedMonthValues.size - _indexOne
                pickedEndedMonth = _indexZero
            }
        }
    }

    init {
        jobReferenceDbId = savedStateHandle.get<Int>(JOB_DATABASE_ID) ?: _indexZero
        getJobReference()
    }

    fun initialize(listOfCompany: Array<String>) {
        listOfCompanies = listOfCompany.toList()
    }

    private fun getJobReference() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getJobReference(jobReferenceDbId)?.let {
                currentJobChecked = it.current ?: false
                companyName = it.companyName ?: ""
                jobName = it.position ?: ""
                jobStart = it.startDate ?: ""
                lastBossName = it.bossName ?: ""
                contactEmail = it.contactEmail ?: ""
                contactPhone = it.contactPhone ?: ""

                if (jobStart.isNotEmpty()) {
                    pickedStartedYear = jobStart.toStartedYear()
                    pickedStartedMonth = jobStart.toStartedMonth()
                    onDatePickerOkClicked(_indexZero)
                }

                jobEnd = if (currentJobChecked) NOW else it.endDate ?: ""
                if (jobEnd.isNotEmpty() && jobEnd != NOW) {
                    pickedEndedYear = jobEnd.toStartedYear()
                    pickedEndedMonth = jobEnd.toStartedMonth()
                }
            }
        }
    }

    fun onSaveClicked() {
        if (jobReferenceDbId == _indexZero) {
            if (listOfCompanies.contains(companyName.trim())) {
                state.value = PreviousJobInformationState.OnErrorValidation(R.string.tv_body_company_already_exists)
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateJobReference(
                JobReference(
                    companyName = companyName,
                    position = jobName,
                    startDate = jobStart,
                    endDate = jobEnd,
                    bossName = lastBossName,
                    contactEmail = contactEmail,
                    contactPhone = contactPhone,
                    dbId = jobReferenceDbId,
                    current = currentJobChecked
                )
            )
        }

        // after we store the data on internal db -> go back to previous screen
        state.value = PreviousJobInformationState.BackJobReferencesScreen(null)
    }

    fun onCancelClicked() {
        state.value = PreviousJobInformationState.BackJobReferencesScreen(null)
    }

    fun onInitialJobDateClicked() {
        state.value = PreviousJobInformationState.OpenDatePickerDialog(_indexZero)
    }

    fun onEndJobDateClicked() {
        if (jobStart.isEmpty() || currentJobChecked) return
        state.value = PreviousJobInformationState.OpenDatePickerDialog(_indexOne)
    }

    fun onDatePickerOkClicked(whichDate: Int) {
        when (whichDate) {
            _indexZero -> {
                jobStart = "$pickedStartedYear-${DecimalFormat(_patternDecimal).format(pickedStartedMonth + 1)}"
                if (pickedStartedYear == currentYear) {
                    minEndedMonth = pickedStartedMonth
                    minEndedYear = pickedStartedYear
                    displayEndedMonthValues = getMonthsInRange(pickedStartedMonth, currentMonth)
                    maxEndedMonth = displayEndedMonthValues.size + pickedStartedMonth - 1
                } else {
                    minEndedMonth = pickedStartedMonth
                    minEndedYear = pickedStartedYear
                    pickedEndedYear = pickedStartedYear
                    displayEndedMonthValues = getMonthsAfterNumber(pickedStartedMonth)
                    maxEndedMonth = _maxEndMonth
                }
            }
            _indexOne -> jobEnd = "$pickedEndedYear-${DecimalFormat(_patternDecimal).format(pickedEndedMonth + 1)}"
        }
    }

    private fun isNextBnEnabled(): Boolean {
        val validEmail = if (contactEmail.isNotEmpty()) contactEmail.isValidEmail() else true
        val validPhone = if (contactPhone.isNotEmpty()) contactPhone.isValidPhone() else true
        return companyName.isNotEmpty() && jobName.isNotEmpty() && jobStart.isNotEmpty()
                && (if (currentJobChecked) true else jobEnd.isNotEmpty())
                && lastBossName.isNotEmpty() && validEmail && validPhone
    }
}

sealed class PreviousJobInformationState {
    object Init : PreviousJobInformationState()
    data class BackJobReferencesScreen(val item: PreviousJobItem?) : PreviousJobInformationState()
    data class OpenDatePickerDialog(var whichDate: Int) : PreviousJobInformationState()
    data class OnErrorValidation(@StringRes val message: Int) : PreviousJobInformationState()
}
