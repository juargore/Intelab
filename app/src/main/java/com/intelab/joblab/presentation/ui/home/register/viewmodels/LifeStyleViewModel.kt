package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.IdRes
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorResponse
import com.intelab.joblab.domain.entities.HousingTypeUI
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getOwnCars
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getPeopleAtHome
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getSpinner
import com.intelab.joblab.presentation.base.utils._fivePlus
import com.intelab.joblab.presentation.base.utils._indexFour
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexOneNegative
import com.intelab.joblab.presentation.base.utils._indexSix
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._lifeStyle
import com.intelab.joblab.presentation.base.utils._oneAsStr
import com.intelab.joblab.presentation.base.utils._zeroAsStr
import com.intelab.joblab.presentation.ui.home.register.fragment.LifeStyleFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LifeStyleViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase, 
    val catalogUseCase: CatalogUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<LifeStyleState>(LifeStyleState.Init)
    val peopleAtHome = getPeopleAtHome()
    val ownCars = getOwnCars()
    var screen = _indexSix
    var screenName = _lifeStyle
    var step = _indexFour
    val advance = _indexFour

    @get:Bindable
    var selectedPeopleAtHome by bindDelegate(getSpinner(_oneAsStr))

    @get:Bindable
    var selectedOwnCars by bindDelegate(getSpinner(_zeroAsStr))

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var housings by bindDelegate(listOf<HousingTypeUI>())

    @get:Bindable
    var selectedPet by bindDelegate(_indexZero) { _, _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var housingPosition by bindDelegate(_indexOneNegative) { _, _ ->
        nextBnEnabled = isNextBnEnabled()
    }

    init {
        peopleAtHome.add(getSpinner(_fivePlus))
        ownCars.add(getSpinner(_fivePlus))
    }

    fun getHousingTypes() {
        launch {
            catalogUseCase.getHousingTypes()
                .onStart { state.value = LifeStyleState.IsLoading(true) }
                .collect { result ->
                    state.value = LifeStyleState.IsLoading(false)
                    if (result is BaseResult.Success) {
                        housings = result.data
                        loadDataFromDb()
                    }
                }
        }
    }


    fun loadDataFromDb() {
        launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.houseType?.let { housingPosition = it.id - 1 }
                cr.hasPets?.let { selectedPet = it }
                cr.totalCars?.let { selectedOwnCars = getSpinner(it) }
                cr.totalFamilyMembers?.let { selectedPeopleAtHome = getSpinner(it) }
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

    private fun isNextBnEnabled(): Boolean = housingPosition != -1 && selectedPet != 0

    fun onNextClicked() {
        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    houseType = housings.firstOrNull { it.id == housingPosition + 1 },
                    totalFamilyMembers = selectedPeopleAtHome.text,
                    hasPets = selectedPet,
                    totalCars = selectedOwnCars.text,
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), LifeStyleViewModel::class.simpleName
            )
        }
        state.value = LifeStyleState.OpenLifeStyleSecondPart(
            LifeStyleFragmentDirections.actionLifeStyleFragmentToLifeStylePartTwoFragment()
        )
    }

    fun onBackClicked() {
        state.value =
            LifeStyleState.BackCreditBureauValidateScreen(
                R.id.creditBureauValidateFragment,
                LifeStyleFragmentDirections.actionLifeStyleFragmentToCreditBureauValidateFragment()
            )
    }

    fun onPetYesClicked() { selectedPet = _indexOne }

    fun onPetNoClicked() { selectedPet = _indexTwo }
}

sealed class LifeStyleState {
    object Init : LifeStyleState()
    data class IsLoading(val isLoading: Boolean) : LifeStyleState()
    data class ErrorStates(val rawResponse: ErrorResponse) : LifeStyleState()
    data class OpenLifeStyleSecondPart(val direction: NavDirections) : LifeStyleState()
    data class BackCreditBureauValidateScreen(@IdRes val id: Int, val directions: NavDirections) : LifeStyleState()
}