package com.intelab.joblab.data.database.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.intelab.joblab.domain.entities.*

@Entity(tableName = "Register")
class RegisterEntity(
    @PrimaryKey
    val id: String,
    val email: String? = null,
    val photoUrl: String? = null,
    val photoUri: String? = null,
    val firstName: String? = null,
    val otherNames: String? = null,
    val fatherLastName: String? = null,
    val motherLastName: String? = null,
    val phone: String? = null,
    val curp: String? = null,
    val nationality: String? = null,

    val birthYear: String? = null,
    val birthMonth: String? = null,
    val birthDay: String? = null,
    val birthStateId: Int? = null,
    val birthStateName: String? = null,
    val genderId: Int? = null,
    val genderName: String? = null,
    val children: String? = null,
    val birthCountryId: String? = null,
    val maritalId: Int? = null,
    val maritalName: String? = null,

    val postalCode: String? = null,
    val suburb: String? = null,
    val municipality: String? = null,
    val stateId: Int? = null,
    val stateName: String? = null,
    val street: String? = null,
    val extNumber: String? = null,
    val intNumber: String? = null,

    val automotiveCredit: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val mortgageCredit: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val creditCard: Int? = null, // 1:Yes | 2:No | 0:Unselected

    val housingTypeId: Int? = null, // 1:Rented | 2:Own | 3:Family House | 0:Unselected
    val housingTypeName: String? = null,
    val totalFamilyMembers: String? = null,
    val hasPets: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val totalCars: String? = null,

    val waterId: Int? = null, // 0:No or Unselected | 1:Yes
    val waterName: String? = null,
    val electricityId: Int? = null, // 0:No or Unselected | 1:Yes
    val electricityName: String? = null,
    val phoneId: Int? = null, // 0:No or Unselected | 1:Yes
    val phoneName: String? = null,
    val tvId: Int? = null, // 0:No or Unselected | 1:Yes
    val tvName: String? = null,
    val gasId: Int? = null, // 0:No or Unselected | 1:Yes
    val gasName: String? = null,
    val internetId: Int? = null, // 0:No or Unselected | 1:Yes
    val internetName: String? = null,
    val transportId: Int? = null, // 1:Foot | 2:Bicycle | 3:Car | 4:Public | 0:Unselected
    val transportName: String? = null,

    val dependents: String? = null,
    val creditCards: String? = null,
    val hasLoan: Int? = null, // 1:Yes | 2:No | 0:Unselected

    val educationLevelId: Int? = null,
    val educationLevelName: String? = null,
    val educationStatusId: Int? = null,
    val educationStatusName: String? = null,
    val institution: String? = null,
    val profession: String? = null,
    val professionCode: String? = null,

    val socialSecurityNumber: String? = null,
    val workExperience: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val currentlyWorking: Int? = null, // 1:Yes | 2:No | 0:Unselected

    val hasSocialMedia: Boolean? = null, // 0:No or Unselected | 1:Yes
    val facebookId: Int? = null,
    val facebookLink: String? = null,
    val facebookName: String? = null,
    val instagramId: Int? = null,
    val instagramLink: String? = null,
    val instagramName: String? = null,
    val twitterId: Int? = null,
    val twitterLink: String? = null,
    val twitterName: String? = null,
    val linkedinId: Int? = null,
    val linkedinLink: String? = null,
    val linkedinName: String? = null,
    val pinterestId: Int? = null,
    val pinterestLink: String? = null,
    val pinterestName: String? = null,
    val youtubeId: Int? = null,
    val youtubeLink: String? = null,
    val youtubeName: String? = null,
    val otherLink: String? = null,

    val screen: Int? = null,
    val screenName: String? = null,
    val step: Int? = null
) {
    fun toComplementaryRegister() = ComplementaryRegister(
        email = email,
        photoUrl = photoUrl,
        photoUri = if (!photoUri.isNullOrEmpty()) Uri.parse(photoUri) else null,
        firstName = firstName,
        otherNames = otherNames,
        fatherLastName = fatherLastName,
        motherLastName = motherLastName,
        phone = phone,
        curp = curp,
        nationality = nationality,
        birthCountryId = birthCountryId,

        birthYear = SpinnerItemUI(birthYear ?: ""),
        birthMonth = SpinnerItemUI(birthMonth ?: ""),
        birthDay = SpinnerItemUI(birthDay ?: ""),
        birthState = StateUI(birthStateId ?: 0, birthStateName ?: ""),
        gender = GenderUI(genderId ?: 0, genderName ?: ""),
        children = SpinnerItemUI(children ?: "0"),
        marital = MaritalUI(maritalId ?: 0, maritalName ?: ""),

        postalCode = postalCode,
        suburb = suburb,
        municipality = municipality,
        state = StateUI(stateId ?: 0, stateName ?: ""),
        street = street,
        extNumber = extNumber,
        intNumber = intNumber,

        selectedAutomotiveCredit = automotiveCredit,
        selectedMortgageCredit = mortgageCredit,
        selectedCreditCard = creditCard,

        houseType = HousingTypeUI(housingTypeId ?: 0, housingTypeName ?: ""),
        totalFamilyMembers = totalFamilyMembers,
        hasPets = hasPets,
        totalCars = totalCars,

        hasWater = ServiceUI(waterId ?: 0, waterName ?: ""),
        hasElectricity = ServiceUI(electricityId ?: 0, electricityName ?: ""),
        hasPhone = ServiceUI(phoneId ?: 0, phoneName ?: ""),
        hasTv = ServiceUI(tvId ?: 0, tvName ?: ""),
        hasGas = ServiceUI(gasId ?: 0, gasName ?: ""),
        hasInternet = ServiceUI(internetId ?: 0, internetName ?: ""),
        transportType = TransportationMeanUI(transportId ?: 0, transportName ?: ""),

        dependents = dependents,
        creditCards = creditCards,
        hasLoan = hasLoan,

        educationLevel = EducationLvlUI(educationLevelId ?: 0, educationLevelName ?: ""),
        educationStatus = EducationStatusUI(educationStatusId ?: 0, educationStatusName ?: ""),
        institution = institution,
        profession = profession,
        professionCode = professionCode,

        socialSecurityNumber = socialSecurityNumber,
        workExperience = workExperience,
        currentlyWorking = currentlyWorking,

        hasSocialMedia = hasSocialMedia,
        facebook = SocialNetworkUI(facebookId ?: 0, facebookName ?: "", facebookLink ?: ""),
        instagram = SocialNetworkUI(instagramId ?: 0, instagramName ?: "", instagramLink ?: ""),
        twitter = SocialNetworkUI(twitterId ?: 0, twitterName ?: "", twitterLink ?: ""),
        linkedin = SocialNetworkUI(linkedinId ?: 0, linkedinName ?: "", linkedinLink ?: ""),
        pinterest = SocialNetworkUI(pinterestId ?: 0, pinterestName ?: "", pinterestLink ?: ""),
        youtube = SocialNetworkUI(youtubeId ?: 0, youtubeName ?: "", youtubeLink ?: ""),
        other = SocialNetworkUI(0, "", otherLink ?: ""),

        screen = screen,
        screenName = screenName,
        step = step
    )
}