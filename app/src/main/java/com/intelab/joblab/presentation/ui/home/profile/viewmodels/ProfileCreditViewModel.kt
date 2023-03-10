package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.requests.FinancialInformation
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfileCreditFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.Constants.Companion.validateThreeConditionsBool
import com.intelab.joblab.presentation.base.utils.Constants.Companion.validateThreeConditionsInt
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._profileCreditNo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileCreditViewModel @Inject constructor(
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileCreditState>(ProfileCreditState.Init)
    var counterScreen = _profileCreditNo
    private val serviceValues = mutableListOf<Int>()

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var selectedAutomotiveCredit by bindDelegate(_indexZero)

    @get:Bindable
    var selectedMortgageCredit by bindDelegate(_indexZero)

    @get:Bindable
    var selectedCreditCard by bindDelegate(_indexZero)

    init {
        getCreditStateFromServer()
    }

    private fun getCreditStateFromServer() {
        launch {
            state.value = ProfileCreditState.IsLoading(true)
            val personal = async {
                candidateUseCase.getProfileInformation().collect { result ->
                    when (result) {
                        is BaseResult.Success -> userFullName = getFullName(result.data)
                        is BaseResult.Error -> state.value = ProfileCreditState.ErrorStates(result.rawResponse)
                    }
                }
            }

            val financial = async {
                candidateUseCase.getUserFinancialInformation().collect { result ->
                    when (result) {
                        is BaseResult.Error -> state.value =
                            ProfileCreditState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> {
                            val cr = result.data
                            selectedAutomotiveCredit = validateThreeConditionsBool(cr.hadAVehicleCreditInLast5Years)
                            selectedMortgageCredit = validateThreeConditionsBool(cr.hadAMortgageCreditInLast5Years)
                            selectedCreditCard = validateThreeConditionsBool(cr.hasACreditCard)
                            serviceValues.add(selectedAutomotiveCredit)
                            serviceValues.add(selectedMortgageCredit)
                            serviceValues.add(selectedCreditCard)
                        }
                    }
                }
            }

            personal.await()
            financial.await()
            state.value = ProfileCreditState.IsLoading(false)
        }
    }

    private fun updateFinancialInformation(onSuccess: () -> Unit) {
        val currentValues = listOf(
            selectedAutomotiveCredit,
            selectedMortgageCredit,
            selectedCreditCard
        )

        launch {
            if (currentValues != serviceValues) {
                candidateUseCase.updateUserFinancialInformation(
                    FinancialInformation(
                        validateThreeConditionsInt(selectedAutomotiveCredit),
                        validateThreeConditionsInt(selectedMortgageCredit),
                        validateThreeConditionsInt(selectedCreditCard)
                    )
                ).onStart { state.value = ProfileCreditState.IsLoading(true) }.collect { result ->
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileCreditState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> onSuccess()
                    }
                }
            } else {
                onSuccess()
            }
        }
    }

    fun onAutomotiveCreditYesClicked() { selectedAutomotiveCredit = _indexOne }

    fun onAutomotiveCreditNoClicked() { selectedAutomotiveCredit = _indexTwo }

    fun onMortgageCreditYesClicked() { selectedMortgageCredit = _indexOne }

    fun onMortgageCreditNoClicked() { selectedMortgageCredit = _indexTwo }

    fun onCreditCardYesClicked() { selectedCreditCard = _indexOne }

    fun onCreditCardNoClicked() { selectedCreditCard = _indexTwo }

    fun onSaveAndExitClicked() {
        updateFinancialInformation {
            state.value = ProfileCreditState.BackHomeScreen
        }
    }

    fun onNextClicked() {
        updateFinancialInformation {
            val direction = ProfileCreditFragmentDirections.actionProfileCreditToProfileLifestyle()
            state.value = ProfileCreditState.OpenProfileLifestyleScreen(direction)
        }
    }
}

sealed class ProfileCreditState {
    object Init : ProfileCreditState()
    object BackHomeScreen : ProfileCreditState()
    data class OpenProfileLifestyleScreen(val direction: NavDirections) : ProfileCreditState()
    data class IsLoading(val isLoading: Boolean) : ProfileCreditState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : ProfileCreditState()
}
