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
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getCreditCards
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getDependents
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getSpinner
import com.intelab.joblab.presentation.base.utils._academic
import com.intelab.joblab.presentation.base.utils._fivePlus
import com.intelab.joblab.presentation.base.utils._indexEight
import com.intelab.joblab.presentation.base.utils._indexFive
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexSix
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._zeroAsStr
import com.intelab.joblab.presentation.ui.home.register.fragment.EconomicFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EconomicViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<EconomicState>(EconomicState.Init)
    val dependents = getDependents()
    val creditCards = getCreditCards()
    var screen = _indexEight
    var step = _indexSix
    val advance = _indexFive
    var screenName = _academic

    @get:Bindable
    var selectedDependents by bindDelegate(getSpinner(_zeroAsStr))

    @get:Bindable
    var selectedCreditCard by bindDelegate(getSpinner(_zeroAsStr))

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var selectedLoan by bindDelegate(_indexZero) { _, field ->
        nextBnEnabled = field != _indexZero
    }

    init {
        dependents.add(getSpinner(_fivePlus))
        creditCards.add(getSpinner(_fivePlus))
    }

    fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.dependents?.let { selectedDependents = getSpinner(it) }
                cr.creditCards?.let { selectedCreditCard = getSpinner(it) }
                cr.hasLoan?.let { selectedLoan = it }
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

    fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    dependents = selectedDependents.text,
                    creditCards = selectedCreditCard.text,
                    hasLoan = selectedLoan,
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), EconomicViewModel::class.simpleName
            )
        }
        val directions = EconomicFragmentDirections.actionEconomicFragmentToAcademicFragment()
        state.value = EconomicState.OpenAcademicScreen(directions)
    }

    fun onBackClicked() {
        val directions = EconomicFragmentDirections.actionEconomicFragmentToLifeStylePartTwoFragment()
        state.value = EconomicState.BackLifeStylePartTwoScreen(R.id.lifeStylePartTwoFragment, directions)
    }

    fun onLoanYesClicked() { selectedLoan = _indexOne }

    fun onLoanNoClicked() { selectedLoan = _indexTwo }
}

sealed class EconomicState {
    object Init : EconomicState()
    data class OpenAcademicScreen(val direction: NavDirections) : EconomicState()
    data class BackLifeStylePartTwoScreen(@IdRes val id: Int, val directions: NavDirections) : EconomicState()
}
