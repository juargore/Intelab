package com.intelab.joblab.domain.entities

data class PersonalInformation(
    val birthDate: String?,
    val birthCountryId: String?,
    val birthStateId: String?,
    val genderId: String?,
    val hasPet: Boolean?,
    val maritalStatusId: String?,
    val numberOfChildren: String?,
    val birthCountry: String?,
    val birthState: String?,
    val gender: String?,
    val maritalStatus: String?
)

data class PersonalInformationRequest(
    val birthDate: String?,
    val birthCountryId: String?,
    val birthStateId: String?,
    val genderId: String?,
    val hasPet: Boolean?,
    val maritalStatusId: String?,
    val numberOfChildren: String?
)
