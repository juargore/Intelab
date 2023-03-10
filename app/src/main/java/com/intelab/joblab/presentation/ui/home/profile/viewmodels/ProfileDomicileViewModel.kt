package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.StateUI
import com.intelab.joblab.domain.entities.requests.DomicileInformation
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfileDomicileFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils._foreign
import com.intelab.joblab.presentation.base.utils._indexFive
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._mxCountryId
import com.intelab.joblab.presentation.base.utils._profileDomicileNo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileDomicileViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileDomicileState>(ProfileDomicileState.Init)
    var counterScreen = _profileDomicileNo
    private val serviceValues = mutableListOf<String>()

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var cp by bindDelegate("")

    @get:Bindable
    var colony by bindDelegate("")

    @get:Bindable
    var mcpio by bindDelegate("")

    @get:Bindable
    var statesList by bindDelegate(listOf(StateUI(_indexZero, _foreign)))

    @get:Bindable
    var street by bindDelegate("")

    @get:Bindable
    var noExt by bindDelegate("")

    @get:Bindable
    var noInt by bindDelegate("")

    @get:Bindable
    var selectedState by bindDelegate(StateUI(_indexZero, _foreign))

    init {
        getProfileInformationFromServer()
    }

    private fun getProfileInformationFromServer() {
        launch {
            state.value = ProfileDomicileState.IsLoading(true)
            val personal = async {
                candidateUseCase.getProfileInformation().collect { result ->
                    when (result) {
                        is BaseResult.Success -> userFullName = getFullName(result.data)
                        is BaseResult.Error -> state.value = ProfileDomicileState.ErrorStates(result.rawResponse)
                    }
                }
            }

            val states = async {
                catalogUseCase.getStatesByCountryId(_mxCountryId).collect { result ->
                    if (result is BaseResult.Success) statesList = result.data
                }
            }

            personal.await()
            states.await()

            candidateUseCase.getDomicileInformation().collect { result ->
                when (result) {
                    is BaseResult.Error -> state.value = ProfileDomicileState.ErrorStates(result.rawResponse)
                    is BaseResult.Success -> {
                        val cr = result.data
                        street = cr.street ?: ""
                        noExt = cr.extNumber ?: ""
                        noInt = cr.intNumber ?: ""
                        colony = cr.town ?: ""
                        mcpio = cr.city ?: ""
                        cp = cr.postalCode ?: ""
                        setSelectedState(cr)
                        serviceValues.add(street)
                        serviceValues.add(noExt)
                        serviceValues.add(noInt)
                        serviceValues.add(colony)
                        serviceValues.add(mcpio)
                        serviceValues.add(selectedState.id.toString())
                        serviceValues.add(cp)
                    }
                }
            }
            state.value = ProfileDomicileState.IsLoading(false)
        }
    }

    private fun setSelectedState(cr: DomicileInformation) {
        selectedState = StateUI(
            id = cr.state?.toInt() ?: _indexZero,
            stateName = statesList.find { cr.state?.toInt() == it.id }?.stateName ?: _foreign
        )
    }

    private fun updateDomicileInformation(onSuccess: () -> Unit) {
        if (cp.length < _indexFive) {
            state.value = ProfileDomicileState.ShowDialog(R.string.et_error_message_postal_code)
            return
        }

        if (!isValidInformation()) {
            state.value = ProfileDomicileState.ShowDialog(R.string.dialog_profile_description_invalid_information)
            return
        }

        val currentValues =
            listOf(street, noExt, noInt, colony, mcpio, selectedState.id.toString(), cp)
        launch {
            if (currentValues != serviceValues) {
                candidateUseCase.sendDomicileInformationUpdate(
                    DomicileInformation(
                        street = street,
                        extNumber = noExt,
                        intNumber = noInt,
                        town = colony,
                        county = null,
                        city = mcpio,
                        state = selectedState.id.toString(),
                        postalCode = cp,
                        reference = null
                    )
                ).onStart { state.value = ProfileDomicileState.IsLoading(true) }.collect { result ->
                    state.value = ProfileDomicileState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileDomicileState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> onSuccess()
                    }
                }
            } else {
                onSuccess()
            }
        }
    }

    private fun isValidInformation(): Boolean {
        return cp.isNotEmpty() && colony.isNotEmpty() && mcpio.isNotEmpty() && street.isNotEmpty() && noExt.isNotEmpty()
    }

    fun onSaveAndExitClicked() {
        updateDomicileInformation {
            state.value = ProfileDomicileState.BackHomeScreen
        }
    }

    fun onNextClicked() {
        updateDomicileInformation {
            val direction = ProfileDomicileFragmentDirections.actionProfileDomicileToProfileCredit()
            state.value = ProfileDomicileState.OpenProfileCreditScreen(direction)
        }
    }
}

sealed class ProfileDomicileState {
    object Init : ProfileDomicileState()
    data class OpenProfileCreditScreen(val direction: NavDirections) : ProfileDomicileState()
    data class IsLoading(val isLoading: Boolean) : ProfileDomicileState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : ProfileDomicileState()
    object BackHomeScreen : ProfileDomicileState()
    data class ShowDialog(@StringRes val messageId: Int) : ProfileDomicileState()
}
