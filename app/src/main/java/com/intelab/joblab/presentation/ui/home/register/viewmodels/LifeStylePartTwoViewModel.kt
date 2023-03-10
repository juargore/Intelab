package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.IdRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorResponse
import com.intelab.joblab.domain.entities.ServiceUI
import com.intelab.joblab.domain.entities.TransportationMeanUI
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._economic
import com.intelab.joblab.presentation.base.utils._indexFive
import com.intelab.joblab.presentation.base.utils._indexFour
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexOneNegative
import com.intelab.joblab.presentation.base.utils._indexSeven
import com.intelab.joblab.presentation.base.utils._indexSix
import com.intelab.joblab.presentation.base.utils._indexThree
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.ui.home.register.fragment.LifeStylePartTwoFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LifeStylePartTwoViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase, val catalogUseCase: CatalogUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<LifeStylePartTwoState>(LifeStylePartTwoState.Init)
    private val serviceTop = ServiceUI(_indexZero, "")
    private var services = listOf<ServiceUI>()
    var screen = _indexSeven
    var step = _indexFive
    val advance = _indexFour
    var screenName = _economic

    @get:Bindable
    var selectedWater by bindDelegate(serviceTop)

    @get:Bindable
    var selectedLight by bindDelegate(serviceTop)

    @get:Bindable
    var selectedPhone by bindDelegate(serviceTop)

    @get:Bindable
    var selectedTv by bindDelegate(serviceTop)

    @get:Bindable
    var selectedGas by bindDelegate(serviceTop)

    @get:Bindable
    var selectedInternet by bindDelegate(serviceTop)

    @get:Bindable
    var transportations by bindDelegate(listOf<TransportationMeanUI>())

    @get:Bindable
    var transportationPosition by bindDelegate(_indexOneNegative)

    fun callServicesEndPoint() {
        launch {
            state.value = LifeStylePartTwoState.IsLoading(true)
            val services = async {
                catalogUseCase.getServices().collect { result ->
                    if (result is BaseResult.Success) services = result.data
                }
            }

            val transportation = async {
                catalogUseCase.getTransportationMeans().collect { result ->
                    if (result is BaseResult.Success) transportations = result.data
                }
            }

            services.await()
            transportation.await()
            loadDataFromDb()

            state.value = LifeStylePartTwoState.IsLoading(false)
        }
    }

    private fun loadDataFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.hasWater?.let { selectedWater = it }
                cr.hasElectricity?.let { selectedLight = it }
                cr.hasPhone?.let { selectedPhone = it }
                cr.hasTv?.let { selectedTv = it }
                cr.hasGas?.let { selectedGas = it }
                cr.hasInternet?.let { selectedInternet = it }
                cr.transportType?.let { transportationPosition = it.id - 1 }
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
                    hasWater = selectedWater,
                    hasElectricity = selectedLight,
                    hasPhone = selectedPhone,
                    hasTv = selectedTv,
                    hasGas = selectedGas,
                    hasInternet = selectedInternet,
                    transportType = transportations.firstOrNull { it.id == transportationPosition + 1 },
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), LifeStylePartTwoViewModel::class.simpleName
            )
        }

        state.value = LifeStylePartTwoState.OpenEconomicScreen(
            LifeStylePartTwoFragmentDirections.actionLifeStylePartTwoFragmentToEconomicFragment()
        )
    }

    fun onBackClicked() {
        state.value = LifeStylePartTwoState.BackLifeStyleScreen(R.id.lifeStyleFragment,
            LifeStylePartTwoFragmentDirections.actionLifeStylePartTwoFragmentToLifeStyleFragment()
        )
    }

    fun onWaterClicked() {
        selectedWater = if (selectedWater.id == _indexZero) {
            services.first { it.id == _indexOne }
        } else serviceTop
    }

    fun onLightClicked() {
        selectedLight = if (selectedLight.id == _indexZero) {
            services.first { it.id == _indexTwo }
        } else serviceTop
    }

    fun onPhoneClicked() {
        selectedPhone = if (selectedPhone.id == _indexZero) {
            services.first { it.id == _indexThree }
        } else serviceTop
    }

    fun onTvClicked() {
        selectedTv = if (selectedTv.id == _indexZero) {
            services.first { it.id == _indexFour }
        } else serviceTop
    }

    fun onGasClicked() {
        selectedGas = if (selectedGas.id == _indexZero) {
            services.first { it.id == _indexFive }
        } else serviceTop
    }

    fun onInternetClicked() {
        selectedInternet = if (selectedInternet.id == _indexZero) {
            services.first { it.id == _indexSix }
        } else serviceTop
    }
}

sealed class LifeStylePartTwoState {
    object Init : LifeStylePartTwoState()
    data class IsLoading(val isLoading: Boolean) : LifeStylePartTwoState()
    data class ErrorStates(val rawResponse: ErrorResponse) : LifeStylePartTwoState()
    data class OpenEconomicScreen(val direction: NavDirections) : LifeStylePartTwoState()
    data class BackLifeStyleScreen(@IdRes val id: Int, val directions: NavDirections) : LifeStylePartTwoState()
}
