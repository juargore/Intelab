package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.HousingTypeUI
import com.intelab.joblab.domain.entities.ServiceUI
import com.intelab.joblab.domain.entities.SpinnerItemUI
import com.intelab.joblab.domain.entities.requests.HousingInformation
import com.intelab.joblab.domain.entities.requests.Services
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfileLifestyleFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getDependents
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getPeopleAtHome
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getServiceUI
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getSpinnerUI
import com.intelab.joblab.presentation.base.utils.ServicesIds
import com.intelab.joblab.presentation.base.utils._fivePlus
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexOneNegative
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._oneAsStr
import com.intelab.joblab.presentation.base.utils._profileLifeStyleNo
import com.intelab.joblab.presentation.base.utils._sixAsStr
import com.intelab.joblab.presentation.base.utils._zeroAsStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileLifestyleViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileLifestyleState>(ProfileLifestyleState.Init)
    val peopleAtHome = getPeopleAtHome()
    val dependents = getDependents()
    private var clientServices = listOf<ServiceUI>()
    private val serviceValues = mutableListOf<String>()
    private val service = ServiceUI(_indexZero, "")
    var counterScreen = _profileLifeStyleNo

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var housings by bindDelegate<List<HousingTypeUI>>(listOf())

    @get:Bindable
    var housingPosition by bindDelegate(_indexOneNegative)

    @get:Bindable
    var selectedPeopleAtHome by bindDelegate(SpinnerItemUI(_oneAsStr))

    @get:Bindable
    var selectedWater by bindDelegate(service)

    @get:Bindable
    var selectedLight by bindDelegate(service)

    @get:Bindable
    var selectedPhone by bindDelegate(service)

    @get:Bindable
    var selectedTv by bindDelegate(service)

    @get:Bindable
    var selectedGas by bindDelegate(service)

    @get:Bindable
    var selectedInternet by bindDelegate(service)

    @get:Bindable
    var selectedDependents by bindDelegate(SpinnerItemUI(_zeroAsStr))

    init {
        peopleAtHome.add(SpinnerItemUI(_fivePlus))
        dependents.add(SpinnerItemUI(_fivePlus))
        loadServiceData()
    }

    private fun loadServiceData() {
        launch {
            state.value = ProfileLifestyleState.IsLoading(true)
            val personal = async {
                candidateUseCase.getProfileInformation()
                    .collect { result ->
                        when (result) {
                            is BaseResult.Success -> userFullName = getFullName(result.data)
                            is BaseResult.Error -> state.value = ProfileLifestyleState.ErrorStates(result.rawResponse)
                        }
                    }
            }

            val housing = async {
                catalogUseCase.getHousingTypes().collect { result ->
                    when (result) {
                        is BaseResult.Error -> Unit
                        is BaseResult.Success -> housings = result.data
                    }
                }
            }

            val services = async {
                catalogUseCase.getServices().collect { result ->
                    when (result) {
                        is BaseResult.Error -> Unit
                        is BaseResult.Success -> clientServices = result.data
                    }
                }
            }

            personal.await()
            housing.await()
            services.await()

            val lifeStyleInfo = async {
                candidateUseCase.getLifeStyleInformation().collect { result ->
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileLifestyleState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> {
                            val cr = result.data
                            cr.housingTypeId?.let { name ->
                                try {
                                    housingPosition = name.toInt() - 1
                                } catch (e: Exception) {
                                    housings.forEachIndexed { i, v ->
                                        if (v.housingTypeName.trim() == name.trim()) {
                                            housingPosition = i
                                            return@forEachIndexed
                                        }
                                    }
                                }
                            }
                            cr.numberOfPersonsAtHome?.let {
                                selectedPeopleAtHome = getSpinnerUI(it)
                            }

                            cr.services.find { it.id?.toInt() == ServicesIds.WATER.value }?.let {
                                selectedWater = getServiceUI(it)
                            }

                            cr.services.find { it.id?.toInt() == ServicesIds.LIGHT.value }?.let {
                                selectedLight = getServiceUI(it)
                            }

                            cr.services.find { it.id?.toInt() == ServicesIds.PHONE.value }?.let {
                                selectedPhone = getServiceUI(it)
                            }

                            cr.services.find { it.id?.toInt() == ServicesIds.TV.value }?.let {
                                selectedTv = getServiceUI(it)
                            }

                            cr.services.find { it.id?.toInt() == ServicesIds.GAS.value }?.let {
                                selectedGas = getServiceUI(it)
                            }

                            cr.services.find { it.id?.toInt() == ServicesIds.INTERNET.value }?.let {
                                selectedInternet = getServiceUI(it)
                            }

                            cr.numberOfDependents?.let {
                                selectedDependents = getSpinnerUI(it)
                            }

                            serviceValues.add(cr.housingTypeId ?: _zeroAsStr)
                            serviceValues.add(selectedPeopleAtHome.text)
                            serviceValues.add(selectedWater.id.toString())
                            serviceValues.add(selectedLight.id.toString())
                            serviceValues.add(selectedPhone.id.toString())
                            serviceValues.add(selectedTv.id.toString())
                            serviceValues.add(selectedGas.id.toString())
                            serviceValues.add(selectedInternet.id.toString())
                            serviceValues.add(selectedDependents.text)
                        }
                    }
                }
            }

            lifeStyleInfo.await()
            state.value = ProfileLifestyleState.IsLoading(false)
        }
    }

    private fun updateHousingInformation(onSuccess: () -> Unit) {
        val currentValues =
            listOf(
                "${housingPosition + _indexOne}",
                selectedPeopleAtHome.text,
                selectedWater.id.toString(),
                selectedLight.id.toString(),
                selectedPhone.id.toString(),
                selectedTv.id.toString(),
                selectedGas.id.toString(),
                selectedInternet.id.toString(),
                selectedDependents.text
            )
        launch {
            if (currentValues != serviceValues) {
                candidateUseCase.updateLifeStyleInformation(
                    HousingInformation(
                        housingTypeId = "${housingPosition + _indexOne}",
                        numberOfPersonsAtHome = if (selectedPeopleAtHome.text == _fivePlus) _sixAsStr else selectedPeopleAtHome.text,
                        numberOfDependents = if (selectedDependents.text == _fivePlus) _sixAsStr else selectedDependents.text,
                        services = mutableListOf<Services>().apply {
                            if (selectedWater.id == ServicesIds.WATER.value) {
                                add(Services(selectedWater.id.toString(), selectedWater.serviceName))
                            }
                            if (selectedLight.id == ServicesIds.LIGHT.value) {
                                add(Services(selectedLight.id.toString(), selectedLight.serviceName))
                            }
                            if (selectedPhone.id == ServicesIds.PHONE.value) {
                                add(Services(selectedPhone.id.toString(), selectedPhone.serviceName))
                            }
                            if (selectedTv.id == ServicesIds.TV.value) {
                                add(Services(selectedTv.id.toString(), selectedTv.serviceName))
                            }
                            if (selectedGas.id == ServicesIds.GAS.value) {
                                add(Services(selectedGas.id.toString(), selectedGas.serviceName))
                            }
                            if (selectedInternet.id == ServicesIds.INTERNET.value) {
                                add(Services(selectedInternet.id.toString(), selectedInternet.serviceName))
                            }
                        }
                    )
                ).onStart {
                    state.value = ProfileLifestyleState.IsLoading(true)
                }.collect { result ->
                    state.value = ProfileLifestyleState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileLifestyleState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> onSuccess()
                    }
                }
            } else {
                onSuccess()
            }
        }
    }

    fun onWaterClicked() {
        selectedWater = if (selectedWater.id == _indexZero) {
            clientServices.first { it.id == ServicesIds.WATER.value }
        } else service
    }

    fun onLightClicked() {
        selectedLight = if (selectedLight.id == _indexZero) {
            clientServices.first { it.id == ServicesIds.LIGHT.value }
        } else service
    }

    fun onPhoneClicked() {
        selectedPhone = if (selectedPhone.id == _indexZero) {
            clientServices.first { it.id == ServicesIds.PHONE.value }
        } else service
    }

    fun onTvClicked() {
        selectedTv = if (selectedTv.id == _indexZero) {
            clientServices.first { it.id == ServicesIds.TV.value }
        } else service
    }

    fun onGasClicked() {
        selectedGas = if (selectedGas.id == _indexZero) {
            clientServices.first { it.id == ServicesIds.GAS.value }
        } else service
    }

    fun onInternetClicked() {
        selectedInternet = if (selectedInternet.id == _indexZero) {
            clientServices.first { it.id == ServicesIds.INTERNET.value }
        } else service
    }

    fun onSaveAndExitClicked() {
        updateHousingInformation { state.value = ProfileLifestyleState.BackHomeScreen }
    }

    fun onNextClicked() {
        updateHousingInformation {
            val directions = ProfileLifestyleFragmentDirections.actionProfileLifestyleToProfileEconomic()
            state.value = ProfileLifestyleState.OpenProfileEconomicScreen(directions)
        }
    }
}

sealed class ProfileLifestyleState {
    object Init : ProfileLifestyleState()
    object BackHomeScreen : ProfileLifestyleState()
    data class OpenProfileEconomicScreen(val direction: NavDirections) : ProfileLifestyleState()
    data class IsLoading(val isLoading: Boolean) : ProfileLifestyleState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : ProfileLifestyleState()
}
