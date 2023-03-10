package com.intelab.joblab.presentation.ui.home.main.viewmodels

import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.AccutestResultResponse
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.LoadedStatus
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.USER_COMPLEMENTARY_REGISTER_STATE
import com.intelab.joblab.presentation.ui.home.main.fragment.HomeFragmentDirections
import com.intelab.joblab.presentation.base.utils._accutestTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeTabTwoViewModel @Inject constructor(
    val candidateUseCase: CandidateUseCase,
    val authUseCase: AuthUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<HomeTabTwoState>(HomeTabTwoState.Init)
    var results: AccutestResultResponse? = null

    @get:Bindable
    var evaluationExpiredTitle by bindDelegate<Int?>(null)

    @get:Bindable
    var evaluationExpired by bindDelegate(true)

    @get:Bindable
    var complementaryRegisterFinished by bindDelegate(true)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            candidateUseCase.getAccutestResult().collect { result ->
                when (result) {
                    is BaseResult.Error -> {
                        evaluationExpired = true
                        getAccutestData()
                    }
                    is BaseResult.Success -> {
                        results = result.data
                        evaluationExpired = false
                        state.value = HomeTabTwoState.SendInfoChildFragments(result.data)
                    }
                }
            }
        }
    }

    private fun getAccutestData() {
        viewModelScope.launch {
            candidateUseCase.getItemsProfileHome().collect { result ->
                when (result) {
                    is BaseResult.Success -> {
                        callUserStateService()
                        result.data.forEach {
                            if (it.type == _accutestTitle) {
                                evaluationExpiredTitle = when (it.status) {
                                    LoadedStatus.EXPIRED -> R.string.tv_title_expired_evaluation
                                    else -> R.string.tv_title_pending_evaluation
                                }
                            }
                        }
                    }
                    is BaseResult.Error -> state.value = HomeTabTwoState.ErrorStates(result.rawResponse)
                }
            }
        }
    }

    private fun callUserStateService() {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.checkForUserState().collect { result ->
                if (result is BaseResult.Success) {
                    if (result.data.state == USER_COMPLEMENTARY_REGISTER_STATE) {
                        // user has not finished the complementary registration yet
                        complementaryRegisterFinished = false
                    }
                }
            }
        }
    }

    fun onAccutestClicked(@Suppress("UNUSED_PARAMETER") v: View?) {
        if (complementaryRegisterFinished) {
            val directions = HomeFragmentDirections.actionHomeFragmentToAccutestNavigation()
            state.value = HomeTabTwoState.OpenAccutestScreen(directions)
        } else {
            state.value = HomeTabTwoState.InformComplementaryRegisterIncomplete
        }
    }
}

sealed class HomeTabTwoState {
    object Init : HomeTabTwoState()
    object InformComplementaryRegisterIncomplete : HomeTabTwoState()
    data class SendInfoChildFragments(val results: AccutestResultResponse) : HomeTabTwoState()
    data class OpenAccutestScreen(val direction: NavDirections) : HomeTabTwoState()
    data class OpenComplementaryRegisterScreen(val direction: NavDirections) : HomeTabTwoState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : HomeTabTwoState()
}
