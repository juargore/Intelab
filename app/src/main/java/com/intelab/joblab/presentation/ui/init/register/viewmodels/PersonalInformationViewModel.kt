package com.intelab.joblab.presentation.ui.init.register.viewmodels

import android.net.Uri
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.extensions.isValidCurp
import com.intelab.joblab.presentation.extensions.isValidPhone
import com.intelab.joblab.presentation.extensions.upperCaseDefault
import com.intelab.joblab.presentation.ui.init.register.fragments.PersonalInformationFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalInformationViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val authUseCase: AuthUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<PersonalInformationState>(PersonalInformationState.Init)
    var birthCountryId = _oneAsStr
    private var firstName = ""
    private var secondName = ""
    var nationality = _national
    var foreign: Boolean = false

    @get:Bindable
    var nextButtonEnabled by bindDelegate(false)

    @get:Bindable
    var userPhotoUri by bindDelegate<Uri?>(null)

    @get:Bindable
    var email by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var radioChecked by bindDelegate(R.id.radioNational){ oldValue, field ->
        if (oldValue != field) {
            (field != R.id.radioNational).also { foreign = it }
            minCurpLength = if (foreign) _minCurpLengthForeign else _minCurpLengthNational
            birthCountryId = if (foreign) _zeroAsStr else _oneAsStr
            showCurpField = !foreign
            if (foreign) passport = "" else curp = ""
            errorMessage = null
            nationality = if (field == R.id.radioNational) _national else _foreign
        }
    }

    @get:Bindable
    var names by bindDelegate("") { _ , field ->
        nextButtonEnabled = isButtonEnabled()
        val namesList = field.trim().split("\\s+".toRegex())
        firstName = namesList[_indexZero]
        secondName = namesList.subList(1, namesList.size).joinToString(" ")
    }

    @get:Bindable
    var lastName by bindDelegate("") { _ , _ ->
        nextButtonEnabled = isButtonEnabled()
    }

    @get:Bindable
    var secondLastName by bindDelegate("")

    @get:Bindable
    var phone by bindDelegate("") { _ , field ->
        nextButtonEnabled = isButtonEnabled()
        errorMessagePhone = if (field.isValidPhone()) null
        else R.string.dialog_profile_description_invalid_phone
    }

    @get:Bindable
    var curp by bindDelegate("") { _ , field ->
        nextButtonEnabled = isButtonEnabled()
        errorMessage =
            if ((field.length < _minCurpLengthNational || !field.isValidCurp()) && field.isNotEmpty()) {
                R.string.dialog_profile_description_invalid_curp
            } else null
    }

    @get:Bindable
    var minCurpLength by bindDelegate(_minCurpLengthNational)

    @get:Bindable
    var errorMessage by bindDelegate<Int?>(null)

    @get:Bindable
    var errorMessagePhone by bindDelegate<Int?>(null)

    @get:Bindable
    var showCurpField by bindDelegate(true)

    @get:Bindable
    var passport by bindDelegate("") { _ , field ->
        nextButtonEnabled = isButtonEnabled()
        errorMessage = if (field.trim().isEmpty()) {
            R.string.et_error_message_min_length
        } else null
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                email = cr.email ?: _noEmail
            }
        }
    }

    private fun isButtonEnabled(): Boolean {
        if (foreign) {
            if (passport.trim().isEmpty())
                return false
        } else {
            if (!curp.isValidCurp())
                return false
        }
        return email.isNotEmpty() && nationality.isNotEmpty() &&
                names.isNotEmpty() && lastName.isNotEmpty() &&
                phone.isNotEmpty() && phone.isValidPhone()
    }

    fun deleteCloudPhoto() { userPhotoUri = null }

    fun onSavedClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    photoUri = userPhotoUri,
                    nationality = nationality,
                    firstName = firstName.trim(),
                    otherNames = secondName.trim(),
                    fatherLastName = lastName.trim(),
                    motherLastName = secondLastName.trim(),
                    phone = phone.trim(),
                    birthCountryId = birthCountryId,
                    curp = if (foreign) passport.upperCaseDefault().trim() else curp.upperCaseDefault().trim()
                ), PersonalInformationViewModel::class.simpleName
            )
        }
        state.value = PersonalInformationState.OpenPersonalInformationValidateScreen(
            PersonalInformationFragmentDirections.actionPersonalInformationValidateFragmentToPostulationFragment()
        )
    }

    fun onCancelClicked() { state.value = PersonalInformationState.BackAuthorizationScreen }

    fun onPhotoClicked() { state.value = PersonalInformationState.OpenBottomSheetDialog }
}

sealed class PersonalInformationState {
    object Init : PersonalInformationState()
    object BackAuthorizationScreen : PersonalInformationState()
    object OpenBottomSheetDialog : PersonalInformationState()
    data class IsLoading(val isLoading: Boolean) : PersonalInformationState()
    data class ErrorPersonalInformation(val rawResponse: ErrorGenericResponse) : PersonalInformationState()
    data class OpenPersonalInformationValidateScreen(val direction: NavDirections) : PersonalInformationState()
}
