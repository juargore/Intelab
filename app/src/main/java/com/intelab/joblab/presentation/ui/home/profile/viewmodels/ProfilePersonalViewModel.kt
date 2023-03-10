package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.entities.requests.CandidateRequest
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.Constants
import com.intelab.joblab.presentation.extensions.isValidCurp
import com.intelab.joblab.presentation.extensions.isValidPhone
import com.intelab.joblab.presentation.extensions.replaceDoubleSpace
import com.intelab.joblab.presentation.extensions.upperCaseDefault
import com.intelab.joblab.presentation.ui.helpers.generateDaysForYearAndMonth
import com.intelab.joblab.presentation.ui.helpers.toMultiPartFile
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfilePersonalFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getChildrenNumber
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getYearList
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.Constants.Companion.validateThreeConditionsInt
import com.intelab.joblab.presentation.base.utils.FileNames
import com.intelab.joblab.presentation.base.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfilePersonalViewModel @Inject constructor(
    val candidateUseCase: CandidateUseCase,
    val catalogUseCase: CatalogUseCase,
    val authUseCase: AuthUseCase,
    val preferencesUseCase: PreferencesUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfilePersonalState>(ProfilePersonalState.Init)

    private val calendar: Calendar = Calendar.getInstance()
    private val actualYear = calendar[Calendar.YEAR]
    private val actualMonth = calendar[Calendar.MONTH]
    private val actualDay = calendar[Calendar.DATE]
    private val personalServiceValues = mutableListOf<String?>()
    private val personalInfoServiceValues = mutableListOf<String?>()

    var birthCountryId = _oneAsStr
    var counterScreen = _profilePersonalNo
    var states = listOf<StateUI>()
    val yearsList = getYearList(actualYear)
    val childrenNumber = getChildrenNumber()
    var userPhoto: DataArray? = null
    var foreign: Boolean = false
    private var firstName = ""
    private var secondName = ""

    @get:Bindable
    var email by bindDelegate("")

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var hintCurp by bindDelegate<Int?>(null)

    @get:Bindable
    var photoUrl by bindDelegate("")

    @get:Bindable
    var lastName by bindDelegate("")

    @get:Bindable
    var secondLastName by bindDelegate("")

    @get:Bindable
    var phone by bindDelegate("")

    @get:Bindable
    var curp by bindDelegate("")

    @get:Bindable
    var minCurpLength by bindDelegate(_minCurpLengthNational)

    @get:Bindable
    var maxCurpLength by bindDelegate(_minCurpLengthNational)

    @get:Bindable
    var statesList by bindDelegate(listOf<StateUI>())

    @get:Bindable
    var genders by bindDelegate(listOf<GenderUI>())

    @get:Bindable
    var maritals by bindDelegate(listOf<MaritalUI>())

    @get:Bindable
    var selectedDay by bindDelegate(SpinnerItemUI(_dayStr))

    @get:Bindable
    var radioChecked by bindDelegate(R.id.radioNational) { oldValue, field ->
        if (oldValue != field) {
            (field != R.id.radioNational).also { foreign = it }
            minCurpLength = if (foreign) _minCurpLengthForeign else _minCurpLengthNational
            maxCurpLength = if (foreign) _maxCurpLengthForeign else _maxCurpLengthNational
            birthCountryId = if (foreign) _zeroAsStr else _oneAsStr
            hintCurp = if (foreign) R.string.et_hint_passport else R.string.et_hint_curp
            curp = if (foreign) "" else curp
        }
    }

    @get:Bindable
    var monthsList by bindDelegate(mutableListOf<SpinnerItemUI>())

    @get:Bindable
    var daysList by bindDelegate(mutableListOf(SpinnerItemUI(_dayStr)))

    @get:Bindable
    var selectedState by bindDelegate<StateUI?>(null)

    @get:Bindable
    var genderPosition by bindDelegate(_indexOneNegative)

    @get:Bindable
    var selectedChildrenNumber by bindDelegate(SpinnerItemUI(_zeroAsStr))

    @get:Bindable
    var selectedPet by bindDelegate(_indexZero)

    @get:Bindable
    var selectedYear by bindDelegate(SpinnerItemUI(_yearStr)) { _, _ ->
        setMonths()
    }

    @get:Bindable
    var selectedMonth by bindDelegate(SpinnerItemUI(_monthStr)) { _, _ ->
        setDays()
    }

    @get:Bindable
    var names by bindDelegate("") { _, field ->
        val namesList = field.trim().split("\\s+".toRegex())
        firstName = namesList[_indexZero]
        secondName = namesList.subList(1, namesList.size).joinToString(" ")
    }

    @get:Bindable
    var selectedMaritalStatus by bindDelegate<MaritalUI?>(null)

    init {
        yearsList.add(_indexZero, SpinnerItemUI(_yearStr))
        childrenNumber.add(SpinnerItemUI(_fivePlus))
        getServiceData()
    }

    private fun getServiceData() {
        launch {
            state.value = ProfilePersonalState.IsLoading(true)
            candidateUseCase.getProfileInformation().collect { result ->
                when (result) {
                    is BaseResult.Error -> state.value =
                        ProfilePersonalState.ErrorStates(result.rawResponse)
                    is BaseResult.Success -> {
                        val cr = result.data
                        firstName = cr.firstName ?: ""
                        secondName = cr.middleName ?: ""
                        names = "$firstName $secondName".replaceDoubleSpace()
                        lastName = cr.surnamePaternal ?: ""
                        secondLastName = cr.surnameMaternal ?: ""
                        userFullName = getFullName(cr)
                        phone = cr.phoneNumber ?: ""
                        foreign = cr.foreign ?: false
                        radioChecked = if (foreign) R.id.radioForeign else R.id.radioNational
                        delay(_delay10)
                        curp = cr.identificationCode.upperCaseDefault()
                        photoUrl = cr.avatarURL ?: ""
                        personalServiceValues.add(firstName)
                        personalServiceValues.add(secondName)
                        personalServiceValues.add(lastName)
                        personalServiceValues.add(secondLastName)
                        personalServiceValues.add(phone)
                        personalServiceValues.add(foreign.toString())
                        personalServiceValues.add(curp)
                    }
                }
            }

            catalogUseCase.getStatesByCountryId(_mxCountryId).collect { result ->
                if (result is BaseResult.Success) states = result.data
            }

            catalogUseCase.getGenders().collect { result ->
                if (result is BaseResult.Success) genders = result.data
            }

            catalogUseCase.getMaritalStatus().collect { result ->
                if (result is BaseResult.Success) maritals = result.data
            }

            statesList = if (foreign) states.filter { it.id == 0 } else states.filter { it.id != 0 }
            email = preferencesUseCase.getEmail()
            candidateUseCase.getCandidatePersonalInformation().collect { result ->
                when (result) {
                    is BaseResult.Error -> state.value = ProfilePersonalState.ErrorStates(result.rawResponse)
                    is BaseResult.Success -> {
                        val cr = result.data
                        cr.birthDate?.let { date ->
                            val dates: List<String> = date.split("-")
                            selectedYear = SpinnerItemUI(dates[0])
                            selectedMonth = SpinnerItemUI(dates[1])
                            selectedDay = SpinnerItemUI(dates[2])
                            personalInfoServiceValues.add(date)
                        }
                        cr.birthStateId?.let { id ->
                            selectedState = StateUI(
                                id.toInt(), statesList.find { it.id == id.toInt() }?.stateName ?: ""
                            )
                        }
                        cr.genderId?.let { id -> genderPosition = id.toInt() - 1 }
                        cr.maritalStatusId?.let {
                            selectedMaritalStatus =
                                MaritalUI(id = it.toInt(), maritalName = cr.maritalStatus ?: "")
                        }
                        cr.numberOfChildren?.let {
                            selectedChildrenNumber = SpinnerItemUI(text = if (it == _sixAsStr) _fivePlus else it)
                        }
                        cr.hasPet?.let { selectedPet = if (it) 1 else 2 }
                        cr.birthCountryId?.let { birthCountryId = it }
                        personalInfoServiceValues.add(cr.birthStateId)
                        personalInfoServiceValues.add(cr.genderId ?: "${genderPosition + 1}")
                        personalInfoServiceValues.add(
                            cr.maritalStatusId ?: selectedMaritalStatus?.id.toString()
                        )
                        personalInfoServiceValues.add(selectedChildrenNumber.text)
                        personalInfoServiceValues.add(
                            when (cr.hasPet) {
                                true -> _oneAsStr
                                false -> _twoAsStr
                                else -> null
                            }
                        )
                        personalInfoServiceValues.add(cr.birthCountryId ?: birthCountryId)
                    }
                }
            }
            state.value = ProfilePersonalState.IsLoading(false)
        }
    }

    private fun setMonths() {
        selectedYear.text.toIntOrNull()?.let {
            monthsList = Constants.getMonthList(it, actualYear, actualDay, actualMonth)
            monthsList.add(0, SpinnerItemUI(_monthStr))
        }
    }

    private fun setDays() {
        val yearValue = selectedYear.text.toIntOrNull()
        val monthValue = selectedMonth.text.toIntOrNull()
        if (yearValue != null && monthValue != null) {
            daysList = generateDaysForYearAndMonth(
                yearValue, monthValue, actualMonth + 1, actualDay, actualYear - 18
            ).map { SpinnerItemUI(it) }.toMutableList()
            daysList.add(0, SpinnerItemUI(_dayStr))
        }
    }

    private fun sendInformationToServer(onSuccess: () -> Unit) {
        if (foreign) {
            if (curp.trim().isEmpty()) {
                state.value = ProfilePersonalState.ShowDialog(R.string.dialog_profile_description_invalid_passport)
                return
            }
        } else {
            if (!curp.isValidCurp()) {
                state.value = ProfilePersonalState.ShowDialog(R.string.dialog_profile_description_invalid_curp)
                return
            }
        }

        if (!phone.isValidPhone()) {
            state.value = ProfilePersonalState.ShowDialog(R.string.dialog_profile_description_invalid_phone)
            return
        }

        if (!isValidInformation()) {
            state.value = ProfilePersonalState.ShowDialog(R.string.dialog_profile_description_invalid_information)
            return
        }

        val personalValues = listOf<String?>(
            firstName, secondName, lastName, secondLastName,
            phone, foreign.toString(), curp.upperCaseDefault()
        )

        val personalInfoValues =
            listOf(
                "${selectedYear.text}-${selectedMonth.text}-${selectedDay.text}",
                selectedState?.id.toString(),
                (genderPosition + 1).toString(),
                (selectedMaritalStatus?.id).toString(),
                selectedChildrenNumber.text,
                when (selectedPet) {
                    1 -> _oneAsStr
                    2 -> _twoAsStr
                    else -> null
                },
                birthCountryId
            )

        launch {
            var successSendInfo = false
            state.value = ProfilePersonalState.IsLoading(true)
            if (personalValues != personalServiceValues) {
                candidateUseCase.registerNewCandidateOnServer(
                    CandidateRequest(
                        firstName = firstName.trim(),
                        middleName = secondName.trim(),
                        surnamePaternal = lastName.trim(),
                        surnameMaternal = secondLastName.trim(),
                        identificationCode = if (curp.isEmpty()) null else curp.upperCaseDefault().trim(),
                        phoneNumber = phone,
                        foreign = foreign
                    )
                ).collect { result ->
                    when (result) {
                        is BaseResult.Error -> state.value = ProfilePersonalState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> successSendInfo = true
                    }
                }
            } else {
                successSendInfo = true
            }

            if (personalInfoValues != personalInfoServiceValues) {
                candidateUseCase.sendPersonalInformationUpdate(
                    PersonalInformationRequest(
                        birthDate = "${selectedYear.text}-${selectedMonth.text}-${selectedDay.text}",
                        maritalStatusId = selectedMaritalStatus?.id.toString(),
                        numberOfChildren = if (selectedChildrenNumber.text == _fivePlus) _sixAsStr else selectedChildrenNumber.text,
                        birthCountryId = birthCountryId,
                        birthStateId = selectedState?.id.toString(),
                        genderId = (genderPosition + 1).toString(),
                        hasPet = validateThreeConditionsInt(selectedPet)
                    )
                ).collect { result ->
                    when (result) {
                        is BaseResult.Error -> state.value = ProfilePersonalState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> if (successSendInfo) onSuccess()
                    }
                }
            } else {
                if (successSendInfo) onSuccess(); delay(_delay10)
            }
            state.value = ProfilePersonalState.IsLoading(false)
        }
    }

    fun sendPhotoToCloud() {
        launch {
            userPhoto?.value?.let {
                candidateUseCase.sendUserPhoto(toMultiPartFile(FileNames.CANDIDATE.value, it))
                    .collect { result ->
                        when (result) {
                            is BaseResult.Error -> state.value = ProfilePersonalState.ErrorStates(result.rawResponse)
                            is BaseResult.Success -> photoUrl = result.data
                        }
                    }
            }
        }
    }

    fun deleteCloudPhoto() {
        launch {
            candidateUseCase.deleteFileFromServer(CandidateUseCase.FilesExpected.AVATAR)
                .collect { result ->
                    when (result) {
                        is BaseResult.Error -> state.value = ProfilePersonalState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> photoUrl = ""
                    }
                }
        }
    }

    private fun isValidInformation(): Boolean {
        return names.isNotEmpty() && lastName.isNotEmpty() &&
                (if (foreign) true else curp.length == _indexEighteen) && phone.isNotEmpty()
                && selectedYear.text != _yearStr && selectedMonth.text != _monthStr && selectedDay.text != _dayStr
    }

    fun onSaveAndExitClicked() {
        sendInformationToServer { state.value = ProfilePersonalState.BackHomeScreen }
    }

    fun onNextClicked() {
        sendInformationToServer {
            state.value = ProfilePersonalState.OpenProfileDomicileScreen(
                ProfilePersonalFragmentDirections.actionProfilePersonalToProfileDomicile()
            )
        }
    }

    fun onPetYesClicked() {
        selectedPet = _indexOne
    }

    fun onPetNoClicked() {
        selectedPet = _indexTwo
    }

    fun onPhotoClicked() {
        state.value = ProfilePersonalState.OpenBottomSheetDialog
    }
}

sealed class ProfilePersonalState {
    object Init : ProfilePersonalState()
    object BackHomeScreen : ProfilePersonalState()
    object OpenBottomSheetDialog : ProfilePersonalState()
    data class OpenProfileDomicileScreen(val direction: NavDirections) : ProfilePersonalState()
    data class IsLoading(val isLoading: Boolean) : ProfilePersonalState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : ProfilePersonalState()
    data class ShowDialog(@StringRes val messageId: Int) : ProfilePersonalState()
}
