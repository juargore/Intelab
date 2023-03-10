package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.IdRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorResponse
import com.intelab.joblab.domain.entities.StateUI
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._financialInformation
import com.intelab.joblab.presentation.base.utils._foreign
import com.intelab.joblab.presentation.base.utils._indexFive
import com.intelab.joblab.presentation.base.utils._indexThree
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._mxCountryId
import com.intelab.joblab.presentation.ui.home.register.fragment.DomicileFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DomicileViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<DomicileState>(DomicileState.Init)
    var screenName = _financialInformation
    var screen = _indexThree
    var step = _indexThree
    val advance = _indexTwo
    private val stateTop = StateUI(_indexZero, _foreign)

    @get:Bindable
    var statesList by bindDelegate(listOf(stateTop))

    @get:Bindable
    var selectedState by bindDelegate(stateTop) { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var cp by bindDelegate("") { _ , field ->
        nextBnEnabled = isNextBnEnabled()
        errorMessage = if (field.isNotEmpty() && field.length < 5)
            R.string.et_error_message_postal_code else null
    }

    @get:Bindable
    var errorMessage by bindDelegate<Int?>(null)

    @get:Bindable
    var colony by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var mcpio by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var street by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var noExt by bindDelegate("") { _ , _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var noInt by bindDelegate("")

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    init {
        getStatesFromServer()
    }

    fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    postalCode = cp,
                    suburb = colony,
                    municipality = mcpio,
                    state = selectedState,
                    street = street,
                    extNumber = noExt,
                    intNumber = noInt,
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), DomicileViewModel::class.simpleName
            )
        }
        val directions = DomicileFragmentDirections.actionDomicileFragmentToCreditBureauFragment()
        state.value = DomicileState.OpenCreditBureauScreen(directions)
    }

    fun onBackClicked() {
        val directions =
            DomicileFragmentDirections.actionDomicileFragmentToPersonalInformationPartTwoFragment()
        state.value = DomicileState.BackPersonalInformationPartTwoScreen(R.id.personalInformationPartTwoFragment, directions)
    }

    private fun getStatesFromServer() {
        launch {
            catalogUseCase.getStatesByCountryId(_mxCountryId)
                .onStart { state.value = DomicileState.IsLoading(true) }
                .collect { result ->
                    state.value = DomicileState.IsLoading(false)
                    if (result is BaseResult.Success) {
                        statesList = result.data
                        loadDataFromDb()
                    }
                }
        }
    }

    private fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.postalCode?.let { cp = it }
                cr.suburb?.let { colony = it }
                cr.municipality?.let { mcpio = it }
                cr.state?.let {
                    if (it.stateName.isNotEmpty())
                        selectedState = it
                }
                cr.street?.let { street = it }
                cr.extNumber?.let { noExt = it }
                cr.intNumber?.let { noInt = it }
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

    private fun isNextBnEnabled(): Boolean {
        return cp.trim().length == _indexFive &&
                colony.isNotEmpty() &&
                mcpio.isNotEmpty() &&
                street.isNotEmpty() &&
                noExt.isNotEmpty()
    }
}

sealed class DomicileState {
    object Init : DomicileState()
    data class IsLoading(val isLoading: Boolean) : DomicileState()
    data class ErrorStates(val rawResponse: ErrorResponse) : DomicileState()
    data class OpenCreditBureauScreen(val direction: NavDirections) : DomicileState()
    data class BackPersonalInformationPartTwoScreen(
        @IdRes val id: Int,
        val directions: NavDirections
    ) : DomicileState()
}
