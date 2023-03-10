package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.helpers.generateDaysForYearAndMonth
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getChildrenNumber
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getYearList
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getMonthList
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.ui.home.register.fragment.PersonalInformationPartTwoFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PersonalInformationPartTwoViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<PersonalPtTwoState>(PersonalPtTwoState.Init)

    private val calendar: Calendar = Calendar.getInstance()
    private val actualYear = calendar[Calendar.YEAR]
    private val actualMonth = calendar[Calendar.MONTH]
    private val actualDay = calendar[Calendar.DATE]

    val yearsList = getYearList(actualYear)
    val childrenNumber = getChildrenNumber()
    var states = listOf<StateUI>()
    var screen: Int = _indexTwo
    val advance: Int = _indexOne
    var screenName = _domicile
    var step = _indexTwo

    @get:Bindable
    var statesList by bindDelegate<List<StateUI>>(listOf())

    @get:Bindable
    var daysList by bindDelegate(mutableListOf(SpinnerItemUI(_dayStr)))

    @get:Bindable
    var selectedState by bindDelegate<StateUI?>(null)

    @get:Bindable
    var selectedYear by bindDelegate(SpinnerItemUI(_yearStr)) { _, _ ->
        setMonths()
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var selectedMonth by bindDelegate(SpinnerItemUI(_monthStr)) { _, _ ->
        setDays()
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var selectedDay by bindDelegate(SpinnerItemUI(_dayStr)) { _, _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var selectedChildrenNumber by bindDelegate(SpinnerItemUI(_zeroAsStr))

    @get:Bindable
    var monthsList by bindDelegate(mutableListOf<SpinnerItemUI>())

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var maritals by bindDelegate<List<MaritalUI>>(listOf())

    @get:Bindable
    var genders by bindDelegate<List<GenderUI>>(listOf())

    @get:Bindable
    var genderPosition by bindDelegate(_indexOneNegative)

    @get:Bindable
    var selectedMaritalStatus by bindDelegate<MaritalUI?>(null)

    init {
        yearsList.add(_indexZero, SpinnerItemUI(_yearStr))
        monthsList.add(_indexZero, SpinnerItemUI(_monthStr))
        childrenNumber.add(SpinnerItemUI(_fivePlus))
        loadData()
    }

    fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    birthYear = selectedYear,
                    birthMonth = selectedMonth,
                    birthDay = selectedDay,
                    birthState = selectedState,
                    gender = genders.firstOrNull { it.id == genderPosition + 1 },
                    children = selectedChildrenNumber,
                    marital = selectedMaritalStatus,
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), PersonalInformationPartTwoViewModel::class.simpleName
            )
        }
        val directions = PersonalInformationPartTwoFragmentDirections
            .actionPersonalInformationPartTwoFragmentToDomicileFragment()
        state.value = PersonalPtTwoState.OpenDomicileScreen(directions)
    }

    fun onBackClicked() {
        state.value = PersonalPtTwoState.BackHomeScreen
    }

    private fun loadData() {
        launch {
            state.value = PersonalPtTwoState.IsLoading(true)
            val states = async {
                catalogUseCase.getStatesByCountryId(_mxCountryId).collect { result ->
                    if (result is BaseResult.Success) states = result.data
                }
            }
            val gender = async {
                catalogUseCase.getGenders().collect { result ->
                    if (result is BaseResult.Success) genders = result.data
                }
            }
            val marital = async {
                catalogUseCase.getMaritalStatus().collect { result ->
                    if (result is BaseResult.Success) maritals = result.data
                }
            }

            states.await()
            gender.await()
            marital.await()
            val dbData = async { loadDataFromDb() }
            dbData.await()
            state.value = PersonalPtTwoState.IsLoading(false)
        }
    }

    private fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.birthYear?.let { selectedYear = it }
                cr.birthMonth?.let { selectedMonth = it }
                cr.birthDay?.let { selectedDay = it }
                cr.birthState?.let { selectedState = it }
                cr.gender?.let { genderPosition = it.id - 1 }
                cr.children?.let { selectedChildrenNumber = it }
                cr.nationality?.let { nac ->
                    val foreign = nac == _foreign
                    statesList =
                        if (foreign) states.filter { it.id == 0 } else states.filter { it.id != 0 }
                }
                cr.marital?.let { selectedMaritalStatus = it }
                cr.screen?.let { no ->
                    if (no > screen) {
                        cr.screenName?.let { screenName = it }
                        cr.step?.let { s -> step = s }
                        screen = no
                    }
                }
                cr.nationality?.let { nac ->
                    val foreign = nac == _foreign
                    statesList = if (foreign) states.filter { it.id == 0 } else states.filter { it.id != 0 }
                }
            }
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

    private fun setMonths() {
        selectedYear.text.toIntOrNull()?.let {
            monthsList = getMonthList(it, actualYear, actualDay, actualMonth)
            monthsList.add(0, SpinnerItemUI(_monthStr))
        }
    }

    private fun isNextBnEnabled(): Boolean {
        return selectedYear.text != _yearStr && selectedMonth.text != _monthStr && selectedDay.text != _dayStr
    }
}

sealed class PersonalPtTwoState {
    object Init : PersonalPtTwoState()
    data class IsLoading(val isLoading: Boolean) : PersonalPtTwoState()
    data class ErrorStates(val rawResponse: ErrorResponse) : PersonalPtTwoState()
    data class OpenDomicileScreen(val direction: NavDirections) : PersonalPtTwoState()
    object BackHomeScreen : PersonalPtTwoState()
}
