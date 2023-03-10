@file:Suppress("UNUSED_PARAMETER")

package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.SpinnerItemUI
import com.intelab.joblab.domain.entities.TransportationMeanUI
import com.intelab.joblab.domain.entities.requests.Financial
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfileEconomicFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getCreditCards
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getOwnCars
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getSpinner
import com.intelab.joblab.presentation.base.utils._fivePlus
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexOneNegative
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._profileEconomicNo
import com.intelab.joblab.presentation.base.utils._sixAsStr
import com.intelab.joblab.presentation.base.utils._zeroAsStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEconomicViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val candidateUseCase: CandidateUseCase,
    val catalogUseCase: CatalogUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileEconomicState>(ProfileEconomicState.Init)
    val ownCars = getOwnCars()
    val creditCards = getCreditCards()
    var counterScreen = _profileEconomicNo
    private var financialInitial = Financial()

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var selectedCreditCard by bindDelegate(getSpinner(_zeroAsStr))

    @get:Bindable
    var selectedLoan by bindDelegate(_indexZero)

    @get:Bindable
    var selectedOwnCars by bindDelegate(getSpinner(_zeroAsStr))

    @get:Bindable
    var transportations by bindDelegate(listOf<TransportationMeanUI>())

    @get:Bindable
    var transportationPosition by bindDelegate(_indexOneNegative)

    init {
        creditCards.add(getSpinner(_fivePlus))
        ownCars.add(getSpinner(_fivePlus))
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect {
                userFullName = getFullName(it)
                getTransportationDataFromServer()
            }
        }
    }

    private fun getTransportationDataFromServer() {
        viewModelScope.launch {
            catalogUseCase.getTransportationMeans().collect { result ->
                if (result is BaseResult.Success) transportations = result.data
                getFinancialInformationFromServer()
            }
        }
    }

    private fun getFinancialInformationFromServer() {
        viewModelScope.launch(Dispatchers.IO) {
            candidateUseCase.getEconomicInformation()
                .onStart { state.value = ProfileEconomicState.IsLoading(true) }
                .collect { result ->
                    state.value = ProfileEconomicState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileEconomicState.OnError(result.rawResponse)
                        is BaseResult.Success -> {
                            val res = result.data
                            financialInitial = result.data
                            res.numberOfCreditCards?.let {
                                val cards = if (it == _sixAsStr) _fivePlus else it
                                selectedCreditCard = SpinnerItemUI(cards)
                            }
                            res.hasACreditOrLoanActive?.let { selectedLoan = if (it) 1 else 2 }
                            res.numberOfCarsAtHome?.let {
                                val cars = if (it == _sixAsStr) _fivePlus else it
                                selectedOwnCars = SpinnerItemUI(cars)
                            }
                            res.habitualTransportationMeanId?.let {
                                transportationPosition = it.toInt() - 1
                            }
                        }
                    }
                }
        }
    }

    fun onSaveAndExitClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            val transportationMean =
                if (transportationPosition == _indexOneNegative) _zeroAsStr else transportations[transportationPosition].transportationMeanName
            val totalCreditCards =
                if (selectedCreditCard.text == _fivePlus) _sixAsStr else selectedCreditCard.text
            val totalCarsAtHome = if (selectedOwnCars.text == _fivePlus) _sixAsStr else selectedOwnCars.text

            val financial = Financial(
                numberOfCreditCards = totalCreditCards,
                hasACreditOrLoanActive = selectedLoan == _indexOne,
                numberOfCarsAtHome = totalCarsAtHome,
                habitualTransportationMeanId = (transportationPosition + 1).toString(),
                habitualTransportationMean = transportationMean
            )

            candidateUseCase.sendEconomicInformationUpdate(financial)
                .onStart { state.value = ProfileEconomicState.IsLoading(true) }
                .collect { result ->
                    state.value = ProfileEconomicState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileEconomicState.OnError(result.rawResponse)
                        is BaseResult.Success -> state.value = ProfileEconomicState.ExitScreen
                    }
                }
        }
    }

    fun onLoanYesClicked(v: View?) { selectedLoan = _indexOne }

    fun onLoanNoClicked(v: View?) { selectedLoan = _indexTwo }

    fun onNextClicked() {
        val direction = ProfileEconomicFragmentDirections.actionProfileEconomicToProfileAcademic()

        if (transportations.isEmpty()) {
            state.value = ProfileEconomicState.OpenProfileAcademicScreen(direction)
        } else {
            val transportationMean =
                if (transportationPosition == _indexOneNegative) _zeroAsStr else transportations[transportationPosition].transportationMeanName
            val totalCreditCards =
                if (selectedCreditCard.text == _fivePlus) _sixAsStr else selectedCreditCard.text
            val totalCarsAtHome = if (selectedOwnCars.text == _fivePlus) _sixAsStr else selectedOwnCars.text
            val financial = Financial(
                numberOfCreditCards = totalCreditCards,
                hasACreditOrLoanActive = selectedLoan == _indexOne,
                numberOfCarsAtHome = totalCarsAtHome,
                habitualTransportationMeanId = (transportationPosition + 1).toString(),
                habitualTransportationMean = transportationMean
            )
            if (financialInitial == financial) {
                // no changes on screen -> just go to next screen
                state.value = ProfileEconomicState.OpenProfileAcademicScreen(direction)
            } else {
                // at least one change -> update on server
                viewModelScope.launch {
                    candidateUseCase.sendEconomicInformationUpdate(financial)
                        .onStart { state.value = ProfileEconomicState.IsLoading(true) }
                        .collect { result ->
                            state.value = ProfileEconomicState.IsLoading(false)
                            when (result) {
                                is BaseResult.Error -> state.value = ProfileEconomicState.OnError(result.rawResponse)
                                is BaseResult.Success -> state.value = ProfileEconomicState.OpenProfileAcademicScreen(direction)
                            }
                        }
                }
            }
        }
    }
}

sealed class ProfileEconomicState {
    object Init : ProfileEconomicState()
    object ExitScreen : ProfileEconomicState()
    data class IsLoading(val isLoading: Boolean) : ProfileEconomicState()
    data class OnError(val rawResponse: ErrorGenericResponse) : ProfileEconomicState()
    data class OpenProfileAcademicScreen(val direction: NavDirections) : ProfileEconomicState()
}
