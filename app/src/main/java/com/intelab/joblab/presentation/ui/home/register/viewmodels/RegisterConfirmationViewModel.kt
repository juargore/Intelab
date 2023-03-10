package com.intelab.joblab.presentation.ui.home.register.viewmodels

import androidx.annotation.IdRes
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.JobReference
import com.intelab.joblab.domain.entities.requests.*
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.Constants.Companion.validateThreeConditionsInt
import com.intelab.joblab.presentation.base.utils.ServicesIds
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils._fiveAsStr
import com.intelab.joblab.presentation.base.utils._fivePlus
import com.intelab.joblab.presentation.base.utils._fourAdStr
import com.intelab.joblab.presentation.base.utils._oneAsStr
import com.intelab.joblab.presentation.base.utils._sixAsStr
import com.intelab.joblab.presentation.base.utils._threeAsStr
import com.intelab.joblab.presentation.base.utils._twoAsStr
import com.intelab.joblab.presentation.ui.home.register.fragment.RegisterConfirmationFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterConfirmationViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val candidateUseCase: CandidateUseCase,
    val authUseCase: AuthUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<RegisterConfirmationState>(RegisterConfirmationState.Init)

    @get:Bindable
    var userName by bindDelegate("")

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var userEmail by bindDelegate("")

    @get:Bindable
    var userPhone by bindDelegate("")

    @get:Bindable
    var userCurp by bindDelegate("")

    @get:Bindable
    var jobPostulations by bindDelegate("")

    @get:Bindable
    var nationality by bindDelegate("")

    init {
        launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                userFullName = getFullName(cr)
                userName = cr.email ?: ""
                userEmail = cr.email ?: ""
                userPhone = cr.phone ?: ""
                userCurp = cr.curp ?: ""
                nationality = cr.nationality ?: ""
            }
            dbUseCase.getAllJobPostulation().collect { result ->
                result.forEachIndexed { i, value ->
                    if (i == 0) {
                        jobPostulations = value.description
                    } else {
                        jobPostulations += ", ${value.description}"
                    }
                }
            }
        }
    }

    fun onYesClicked() {
        launch(Dispatchers.IO) {
            val dbRegister = async { dbUseCase.getRegistrationData() }
            val dbExperiences = async { dbUseCase.getJobReferencesData() }
            combine(
                dbRegister.await(),
                dbExperiences.await()
            ) { register: ComplementaryRegister, experiences: List<JobReference> ->

                withContext(Dispatchers.Main) {
                    candidateUseCase.registerNewCandidateComplementaryOnServer(
                        candidateInformation(register, experiences)
                    ).onStart {
                        state.value = RegisterConfirmationState.IsLoading(true)
                    }.collect { result ->
                        when (result) {
                            is BaseResult.Error -> state.value = RegisterConfirmationState.ErrorStates(result.rawResponse)
                            is BaseResult.Success -> {
                                val directions = RegisterConfirmationFragmentDirections.actionRegisterConfirmationFragmentToHomeFragment()
                                state.value = RegisterConfirmationState.BackHomeScreen(directions)
                                deleteCandidateInfo()
                            }
                        }
                        state.value = RegisterConfirmationState.IsLoading(false)
                    }
                }
            }.collect()
        }
    }

    fun onReviewClicked() {
        val directions = RegisterConfirmationFragmentDirections.actionRegisterConfirmationFragmentToSocialMediaFragment()
        state.value = RegisterConfirmationState.BackSocialMediaScreen(R.id.socialMediaFragment, directions)
    }

    private fun deleteCandidateInfo() {
        launch(Dispatchers.IO) {
            dbUseCase.deleteCurrentRegistrationData()
            dbUseCase.deleteAllJobReferences()
        }
    }

    private fun changeValueToSendToCloud(value: String?): String? {
        return if (value == _fivePlus) _sixAsStr else value
    }

    private fun candidateInformation(
        register: ComplementaryRegister,
        experiences: List<JobReference>
    ): CandidateComplementaryRequest {
        return CandidateComplementaryRequest(
            personal = Personal(
                birthDate = "${register.birthYear?.text}-${register.birthMonth?.text}-${register.birthDay?.text}",
                birthCountryId = register.birthCountryId,
                birthStateId = register.birthState?.id.toString(),
                genderId = register.gender?.id.toString(),
                hasPet = validateThreeConditionsInt (register.hasPets),
                maritalStatusId = register.marital?.id.toString(),
                numberOfChildren = changeValueToSendToCloud(register.children?.text)
            ),
            address = Address(
                street = register.street,
                extNumber = register.extNumber,
                intNumber = register.intNumber,
                town = register.suburb,
                county = null,
                city = register.municipality,
                state = register.state?.id.toString(),
                postalCode = register.postalCode,
                reference = null
            ),
            housing = Housing(
                housingTypeId = register.houseType?.id.toString(),
                numberOfPersonsAtHome = changeValueToSendToCloud(register.totalFamilyMembers),
                numberOfDependents = changeValueToSendToCloud(register.dependents),
                services = mutableListOf<Services>().apply {
                    if (register.hasWater?.id == ServicesIds.WATER.value)
                        add(Services(id = _oneAsStr))
                    if (register.hasElectricity?.id == ServicesIds.LIGHT.value)
                        add(Services(id = _twoAsStr))
                    if (register.hasPhone?.id == ServicesIds.PHONE.value)
                        add(Services(id = _threeAsStr))
                    if (register.hasTv?.id == ServicesIds.TV.value)
                        add(Services(id = _fourAdStr))
                    if (register.hasGas?.id == ServicesIds.GAS.value)
                        add(Services(id = _fiveAsStr))
                    if (register.hasInternet?.id == ServicesIds.INTERNET.value)
                        add(Services(id = _sixAsStr))
                }
            ),
            financial = Financial(
                numberOfCreditCards = changeValueToSendToCloud(register.creditCards),
                hasACreditOrLoanActive = validateThreeConditionsInt(register.hasLoan),
                numberOfCarsAtHome = changeValueToSendToCloud(register.totalCars),
                habitualTransportationMeanId = register.transportType?.id.toString()
            ),
            creditBureau = CreditBureau(
                hadAVehicleCreditInLast5Years = validateThreeConditionsInt(register.selectedAutomotiveCredit),
                hadAMortgageCreditInLast5Years = validateThreeConditionsInt(register.selectedMortgageCredit),
                hasACreditCard = validateThreeConditionsInt(register.selectedCreditCard),
                lastFourDigitsCreditCard = null
            ),
            educations = listOf(Educations(
                register.educationLevel?.id.toString(),
                register.educationStatus?.id.toString(),
                register.profession,
                register.institution,
                register.professionCode
            )),
            hasWorkExperience = validateThreeConditionsInt(register.workExperience),
            socialIdentificationCode = register.socialSecurityNumber,
            workExperiences = mutableListOf<WorkExperiences>().apply {
                experiences.forEach {
                    add(WorkExperiences(
                        companyName = it.companyName,
                        position = it.position,
                        startDate = "${it.startDate}-01",
                        endDate = if (it.current == true) null else "${it.endDate}-01",
                        contact = Contact(it.bossName, it.contactEmail, it.contactPhone),
                        current = it.current
                    ))
                }
            },
            socialNetworks = mutableListOf<SocialNetworks>().apply {
                if (register.facebook?.username?.isNotEmpty() == true) {
                    add(SocialNetworks(
                        id = register.facebook.id.toString(),
                        description = register.facebook.description,
                        username = register.facebook.username
                    ))
                }
                if (register.instagram?.username?.isNotEmpty() == true) {
                    add(SocialNetworks(
                        id = register.instagram.id.toString(),
                        description = register.instagram.description,
                        username = register.instagram.username
                    ))
                }
                if (register.twitter?.username?.isNotEmpty() == true) {
                    add(SocialNetworks(
                        id = register.twitter.id.toString(),
                        description = register.twitter.description,
                        username = register.twitter.username
                    ))
                }
                if (register.linkedin?.username?.isNotEmpty() == true) {
                    add(SocialNetworks(
                        id = register.linkedin.id.toString(),
                        description = register.linkedin.description,
                        username = register.linkedin.username
                    ))
                }
                if (register.pinterest?.username?.isNotEmpty() == true) {
                    add(SocialNetworks(
                        id = register.pinterest.id.toString(),
                        description = register.pinterest.description,
                        username = register.pinterest.username
                    ))
                }
                if (register.youtube?.username?.isNotEmpty() == true) {
                    add(SocialNetworks(
                        id = register.youtube.id.toString(),
                        description = register.youtube.description,
                        username = register.youtube.username
                    ))
                }
                if (register.other?.username?.isNotEmpty() == true) {
                    add(SocialNetworks(
                        id = register.other.id.toString(),
                        username = register.other.username,
                        description = register.other.description
                    ))
                }
            }
        )
    }
}

sealed class RegisterConfirmationState {
    object Init : RegisterConfirmationState()
    data class IsLoading(val isLoading: Boolean) : RegisterConfirmationState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : RegisterConfirmationState()
    data class BackHomeScreen(val direction: NavDirections) : RegisterConfirmationState()
    data class BackSocialMediaScreen(@IdRes val id: Int, val directions: NavDirections) : RegisterConfirmationState()
}
