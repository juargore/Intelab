package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.IdRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._financialInformation
import com.intelab.joblab.presentation.base.utils._indexFour
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexThree
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.ui.home.register.fragment.CreditBureauFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditBureauViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<CreditBureauState>(CreditBureauState.Init)
    var screenName = _financialInformation
    var screen = _indexFour
    var step = _indexThree
    val advance = _indexThree

    @get:Bindable
    var selectedAutomotiveCredit by bindDelegate(_indexZero) { _, _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var selectedMortgageCredit by bindDelegate(_indexZero) { _, _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var selectedCreditCard by bindDelegate(_indexZero) { _, _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.selectedAutomotiveCredit?.let { selectedAutomotiveCredit = it }
                cr.selectedMortgageCredit?.let { selectedMortgageCredit = it }
                cr.selectedCreditCard?.let { selectedCreditCard = it }
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

    private fun isNextBnEnabled() : Boolean =
        selectedAutomotiveCredit != 0 && selectedMortgageCredit != 0 && selectedCreditCard != 0
    

    fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    selectedAutomotiveCredit = selectedAutomotiveCredit,
                    selectedMortgageCredit = selectedMortgageCredit,
                    selectedCreditCard = selectedCreditCard,
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), CreditBureauViewModel::class.simpleName
            )
        }

        state.value = CreditBureauState.OpenCreditBureauValidateScreen(
            CreditBureauFragmentDirections.actionCreditBureauFragmentToCreditBureauValidateFragment()
        )
    }

    fun onBackClicked() {
        state.value = CreditBureauState.BackDomicileScreen(R.id.domicileFragment,
            CreditBureauFragmentDirections.actionCreditBureauFragmentToDomicileFragment()
        )
    }

    fun onAutomotiveCreditYesClicked() { selectedAutomotiveCredit = _indexOne }

    fun onAutomotiveCreditNoClicked() { selectedAutomotiveCredit = _indexTwo }

    fun onMortgageCreditYesClicked() { selectedMortgageCredit = _indexOne }

    fun onMortgageCreditNoClicked() { selectedMortgageCredit = _indexTwo }

    fun onCreditCardYesClicked() { selectedCreditCard = _indexOne }

    fun onCreditCardNoClicked() { selectedCreditCard = _indexTwo }
}

sealed class CreditBureauState {
    object Init : CreditBureauState()
    data class OpenCreditBureauValidateScreen(val direction: NavDirections) : CreditBureauState()
    data class BackDomicileScreen(@IdRes val id: Int, val directions: NavDirections) : CreditBureauState()
}