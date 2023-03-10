package com.intelab.joblab.domain.entities.requests

import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem
import java.io.Serializable

data class AccutestAnswerRequest(
    val sequence: String
)

data class CandidateRequest(
    val firstName: String,
    val middleName: String,
    val surnamePaternal: String,
    val surnameMaternal: String,
    val identificationCode: String?,
    val phoneNumber: String,
    val foreign: Boolean
)

data class CandidateComplementaryRequest(
    val personal: Personal? = Personal(),
    val address: Address? = Address(),
    val housing: Housing? = Housing(),
    val financial: Financial? = Financial(),
    val creditBureau: CreditBureau? = CreditBureau(),
    val educations: List<Educations> = arrayListOf(),
    val hasWorkExperience: Boolean? = null,
    val socialIdentificationCode: String? = null,
    val workExperiences: List<WorkExperiences> = arrayListOf(),
    val socialNetworks: List<SocialNetworks> = arrayListOf()
)

data class SocialIdentificationCodeRequest(
    val socialIdentificationCode: String
)

data class PreferableJobs(var id: String? = null)

data class Personal(
    val birthDate: String? = null,
    val birthCountryId: String? = null,
    val birthStateId: String? = null,
    val genderId: String? = null,
    val hasPet: Boolean? = null,
    val maritalStatusId: String? = null,
    val numberOfChildren: String? = null
)

data class Address(
    val street: String? = null,
    val extNumber: String? = null,
    val intNumber: String? = null,
    val town: String? = null,
    val county: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val reference: String? = null
)

data class Services(
    val id: String? = null,
    val description: String? = null
)

data class Housing(
    val housingTypeId: String? = null,
    val numberOfPersonsAtHome: String? = null,
    val numberOfDependents: String? = null,
    val services: List<Services> = arrayListOf(),
    val housingType: String? = null
)

data class Financial(
    val numberOfCreditCards: String? = null,
    val hasACreditOrLoanActive: Boolean? = null,
    val numberOfCarsAtHome: String? = null,
    val habitualTransportationMeanId: String? = null,
    val habitualTransportationMean: String? = null
)

data class CreditBureau(
    val hadAVehicleCreditInLast5Years: Boolean? = null,
    val hadAMortgageCreditInLast5Years: Boolean? = null,
    val hasACreditCard: Boolean? = null,
    val lastFourDigitsCreditCard: String? = null
)

data class Educations(
    val levelId: String? = null,
    val statusId: String? = null,
    val speciality: String? = null,
    val institutionName: String? = null,
    val professionalIdentificationCode: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val recordId: Int? = null,
    val level: String? = null,
    val status: String? = null
)

data class EducationsRequest(
    val educations: List<Educations>
)

data class Contact(
    var employerName: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null
) : Serializable

data class WorkExperiences(
    val companyName: String? = null,
    val position: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val contact: Contact? = Contact(),
    val current: Boolean? = null,
    val recordId: Int? = null
) : Serializable {
    fun toPreviousJobItem() = PreviousJobItem(
        company = companyName ?: "",
        jobName = position ?: "",
        jobStart = startDate?.substring(0, startDate.length - 3) ?: "",
        jobEnd = endDate?.substring(0, endDate.length - 3) ?: "",
        id = recordId ?: 0,
        current = current ?: false,
        bossName = contact?.employerName ?: "",
        contactEmail = contact?.email,
        contactPhone = contact?.phoneNumber
    )
    fun toOnlyCompanyName() = companyName?.trim()
}


data class SocialNetworks(
    val id: String? = null,
    val description: String? = null,
    val username: String? = null
)

data class DomicileInformation(
    val street: String? = null,
    val extNumber: String? = null,
    val intNumber: String? = null,
    val town: String? = null,
    val county: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val reference: String? = null
)

data class FinancialInformation(
    val hadAVehicleCreditInLast5Years: Boolean? = null,
    val hadAMortgageCreditInLast5Years: Boolean? = null,
    val hasACreditCard: Boolean? = null
)

data class HousingInformation(
    val housingTypeId: String? = null,
    val numberOfPersonsAtHome: String? = null,
    val numberOfDependents: String? = null,
    val services: List<Services> = arrayListOf(),
    val housingType: String? = null
)
