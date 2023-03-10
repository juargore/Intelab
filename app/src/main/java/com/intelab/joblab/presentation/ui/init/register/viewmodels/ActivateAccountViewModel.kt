package com.intelab.joblab.presentation.ui.init.register.viewmodels

import android.text.Editable
import android.widget.EditText
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexSix
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.ui.init.register.fragments.ActivateAccountFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivateAccountViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ActivateAccountState>(ActivateAccountState.Init)

    @get:Bindable
    var nextButtonEnabled by bindDelegate(false)

    @get:Bindable
    var firstChar by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var secondChar by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var thirdChar by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var fourthChar by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var fifthChar by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var sixthChar by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    fun onNextClicked() {
        launch {
            val code = "$firstChar$secondChar$thirdChar$fourthChar$fifthChar$sixthChar"
            authUseCase.compareAndVerifyActivationCode(code)
                .onStart { loadingWithDelay(this@ActivateAccountViewModel, true) }
                .collect { result ->
                    loadingWithDelay(this@ActivateAccountViewModel, false)
                    when (result) {
                        is BaseResult.Error -> state.value = ActivateAccountState.ErrorCreateAccount(result.rawResponse)
                        is BaseResult.Success -> {
                            val direction = ActivateAccountFragmentDirections.actionActivateAccountFragmentToAuthorizationFragment()
                            state.value = ActivateAccountState.OpenAuthorizationScreen(direction)
                        }
                    }
                }
        }
    }

    fun onSendActivateCodeClicked() {
        launch {
            authUseCase.sendActivationCode()
                .onStart { loadingWithDelay(this@ActivateAccountViewModel, true) }
                .collect { result ->
                    loadingWithDelay(this@ActivateAccountViewModel, false)
                    when (result) {
                        is BaseResult.Error -> state.value = ActivateAccountState.ErrorCreateAccount(result.rawResponse)
                        is BaseResult.Success -> state.value = ActivateAccountState.OpenDialog(
                            R.string.dialog_title_activate_account,
                            R.string.dialog_description_resend_code
                        )
                    }
            }
        }
    }

    fun setEditTextFocus(editable: Editable, nextFocusEditText: EditText) {
        if (editable.toString().length == _indexOne)
            nextFocusEditText.requestFocus()
    }

    fun setActivationCodeFromClipBoard(code: String?) {
        code?.let {
            if (code.length == _indexSix) {
                firstChar  = code[0].toString()
                secondChar = code[1].toString()
                thirdChar  = code[2].toString()
                fourthChar = code[3].toString()
                fifthChar  = code[4].toString()
                sixthChar  = code[5].toString()
            }
        }
    }

    private fun isButtonEnabled(): Boolean {
        return firstChar.isNotEmpty() && secondChar.isNotEmpty() && thirdChar.isNotEmpty()
                && fourthChar.isNotEmpty() && fifthChar.isNotEmpty() && sixthChar.isNotEmpty()
    }
}

sealed class ActivateAccountState {
    object Init : ActivateAccountState()
    data class OpenAuthorizationScreen(val direction: NavDirections) : ActivateAccountState()
    data class IsLoading(val isLoading: Boolean) : ActivateAccountState()
    data class ErrorCreateAccount(val rawResponse: ErrorGenericResponse) : ActivateAccountState()
    data class OpenDialog(val title: Int, val description: Int) : ActivateAccountState()
}
