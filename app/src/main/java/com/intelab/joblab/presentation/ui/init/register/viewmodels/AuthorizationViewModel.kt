package com.intelab.joblab.presentation.ui.init.register.viewmodels

import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.BR
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexTwo
import com.intelab.joblab.presentation.ui.init.register.fragments.AuthorizationFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor() : ObservableViewModel() {

    val state = MutableStateFlow<AuthorizationState>(AuthorizationState.Init)

    @get:Bindable
    var nextBnState by bindDelegate(false)

    @Bindable
    var cbPrivacyNotice: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.cbPrivacyNotice)
            nextBnState = value && cbConsentAccept
        }

    @Bindable
    var cbConsentAccept: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.cbConsentAccept)
            nextBnState = cbPrivacyNotice && value
        }

    fun onNextClicked() {
        val direction = AuthorizationFragmentDirections.actionAuthorizationFragmentToPersonalInformationFragment()
        state.value = AuthorizationState.OpenPostulationScreen(direction)
    }

    fun onPrivacyLinkClicked() {
        state.value = AuthorizationState.OpenPrivacyAndConsentScreen(
            AuthorizationFragmentDirections.actionAuthorizationFragmentToPrivacityAndConsentFragment(
                _indexOne
            )
        )
    }

    fun onConsentLinkClicked() {
        state.value = AuthorizationState.OpenPrivacyAndConsentScreen(
            AuthorizationFragmentDirections.actionAuthorizationFragmentToPrivacityAndConsentFragment(
                _indexTwo
            )
        )
    }
}

sealed class AuthorizationState {
    object Init : AuthorizationState()
    data class OpenPostulationScreen(val direction: NavDirections) : AuthorizationState()
    data class OpenPrivacyAndConsentScreen(val directions: NavDirections) : AuthorizationState()
}
