package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexEight
import com.intelab.joblab.presentation.base.utils._indexNine
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.ui.home.register.fragment.ResumptionFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumptionViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ResumptionState>(ResumptionState.Init)
    var screen = _indexZero

    @get:Bindable
    var step by bindDelegate("")

    @get:Bindable
    var screenName by bindDelegate("")

    @get:Bindable
    var photoUrl by bindDelegate("")

    @get:Bindable
    var progress by bindDelegate(0)

    init {
        launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.screen?.let { screen = it }
                cr.screenName?.let { screenName = it }
                cr.photoUrl?.let { photoUrl = it }
                cr.step?.let {
                    if (it != _indexNine) {
                        step = "$it/$_indexEight"
                        progress = it
                    } else {
                        progress = _indexEight
                    }
                }
            }
        }
    }

    fun onNotClicked() { state.value = ResumptionState.BackHomeScreen }

    fun onYesClicked() {
        val directions: NavDirections? = when (screen) {
            2 -> ResumptionFragmentDirections.actionResumptionFragmentToDomicileFragment()
            3 -> ResumptionFragmentDirections.actionResumptionFragmentToCreditBureauFragment()
            4 -> ResumptionFragmentDirections.actionResumptionFragmentToCreditBureauValidateFragment()
            5 -> ResumptionFragmentDirections.actionResumptionFragmentToLifeStyleFragment()
            6 -> ResumptionFragmentDirections.actionResumptionFragmentToLifeStylePartTwoFragment()
            7 -> ResumptionFragmentDirections.actionResumptionFragmentToEconomicFragment()
            8 -> ResumptionFragmentDirections.actionResumptionFragmentToAcademicFragment()
            9 -> ResumptionFragmentDirections.actionResumptionFragmentToJobReferencesFragment()
            10 -> ResumptionFragmentDirections.actionResumptionFragmentToSocialMediaFragment()
            11 -> ResumptionFragmentDirections.actionResumptionFragmentToRegisterConfirmationFragment()
            else -> null
        }
        directions?.let {
            state.value = ResumptionState.OpenResumptionScreen(it)
        }
    }
}

sealed class ResumptionState {
    object Init : ResumptionState()
    object BackHomeScreen : ResumptionState()
    data class OpenResumptionScreen(val direction: NavDirections) : ResumptionState()
}
