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
import com.intelab.joblab.presentation.base.utils._indexFive
import com.intelab.joblab.presentation.base.utils._indexFour
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._lifeStyle
import com.intelab.joblab.presentation.ui.home.register.fragment.CreditBureauValidateFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreditBureauValidateViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<CreditBureauValidateState>(CreditBureauValidateState.Init)
    var screen = _indexFive
    var step = _indexFour
    var screenName = _lifeStyle

    @get:Bindable
    var automotiveCredit by bindDelegate("")

    @get:Bindable
    var mortgageCredit by bindDelegate("")

    @get:Bindable
    var creditCard by bindDelegate("")

    fun loadDataFromDb(yes: String, no: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.selectedAutomotiveCredit?.let {
                    automotiveCredit = if (it == _indexOne) yes else no
                }
                cr.selectedMortgageCredit?.let {
                    mortgageCredit = if (it == _indexOne) yes else no
                }
                cr.selectedCreditCard?.let {
                    creditCard = if (it == _indexOne) yes else no
                }
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
        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(screen = screen, screenName = screenName, step = step),
                CreditBureauValidateState::class.simpleName
            )
        }
        val directions =
            CreditBureauValidateFragmentDirections.actionCreditBureauValidateFragmentToLifeStyleFragment()
        state.value = CreditBureauValidateState.OpenLifeStyleScreen(directions)
    }

    fun onBackClicked() {
        val directions =
            CreditBureauValidateFragmentDirections.actionCreditBureauValidateFragmentToCreditBureauFragment()
        state.value =
            CreditBureauValidateState.BackCreditBureauScreen(R.id.creditBureauFragment, directions)
    }
}

sealed class CreditBureauValidateState {
    object Init : CreditBureauValidateState()
    data class OpenLifeStyleScreen(val direction: NavDirections) : CreditBureauValidateState()
    data class BackCreditBureauScreen(@IdRes val id: Int, val directions: NavDirections) : CreditBureauValidateState()
}
