package com.intelab.joblab.domain.entities

data class CatalogResponse(
    var id: Int,
    var description: String
) {
    fun toCountryUI() = CountryUI(id, description)

    fun toStateUI() = StateUI(id, description)

    fun toJobUI() = JobUI(id, description, false)

    fun toGenderUI() = GenderUI(id, description)

    fun toServicesUI() = ServiceUI(id, description)

    fun toEducationLvlUI() = EducationLvlUI(id, description)

    fun toHousingTypeUI() = HousingTypeUI(id, description)

    fun toSocialNetworkUI() = SocialNetworkUI(id, description, "")

    fun toEducationStatusUI() = EducationStatusUI(id, description)

    fun toTransportationMeanUI() = TransportationMeanUI(id, description)

    fun toMaritalUI() = MaritalUI(id, description)
}

data class CountryUI(val id: Int, val countryName: String)

data class StateUI(val id: Int, val stateName: String)

data class ParentJobUI(val header: String, val jobList: MutableList<JobUI>)

data class JobUI(val id: Int, val jobName: String, var selected: Boolean) {
    fun toJobPostulation() = JobPostulation(id = id, description = jobName)
}

data class GenderUI(val id: Int, val genderName: String)

data class ServiceUI(val id: Int, val serviceName: String)

data class EducationLvlUI(val id: Int, var educationLvlName: String)

data class EducationStatusUI(val id: Int, val educationStatusName: String)

data class HousingTypeUI(val id: Int, val housingTypeName: String)

data class TransportationMeanUI(val id: Int, val transportationMeanName: String)

data class SocialNetworkUI(val id: Int, val description: String, var username: String)

data class MaritalUI(val id: Int, val maritalName: String)

data class AccutestResponse(
    val defaultValue: String? = null,
    val items: List<AccutestItemResponse> = arrayListOf()
)

data class AccutestItemResponse(val id: String, val phrase: String, val imagePath: String)

data class SocialNetworkRequest(val socialNetworks: List<SocialNetworkUI>)

data class PrivacyConsentResponse(val html: String? = null)
