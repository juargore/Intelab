package com.intelab.joblab.domain.entities

import android.net.Uri
import com.intelab.joblab.data.database.entities.RegisterEntity
import com.intelab.joblab.presentation.ui.home.register.viewmodels.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CreateAccountViewModel
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationViewModel

data class ComplementaryRegister(

    /** @see CreateAccountViewModel */
    val email: String? = null,

    /** @see PersonalInformationViewModel */
    val photoUrl: String? = null,
    val photoUri: Uri? = null,
    val firstName: String? = null,
    val otherNames: String? = null,
    val fatherLastName: String? = null,
    val motherLastName: String? = null,
    val phone: String? = null,
    val curp: String? = null,
    val nationality: String? = null,
    val birthCountryId: String? = null,

    /** @see PersonalInformationPartTwoViewModel */
    val birthYear: SpinnerItemUI? = null,
    val birthMonth: SpinnerItemUI? = null,
    val birthDay: SpinnerItemUI? = null,
    val birthState: StateUI? = null,
    val gender: GenderUI? = null, // 1:Woman | 2:Man | 0:Unselected
    val children: SpinnerItemUI? = null,
    val marital: MaritalUI? = null,

    /** @see DomicileViewModel */
    val postalCode: String? = null,
    val suburb: String? = null,
    val municipality: String? = null,
    val state: StateUI? = null,
    val street: String? = null,
    val extNumber: String? = null,
    val intNumber: String? = null,

    /** @see CreditBureauViewModel */
    val selectedAutomotiveCredit: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val selectedMortgageCredit: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val selectedCreditCard: Int? = null, // 1:Yes | 2:No | 0:Unselected

    /** @see LifeStyleViewModel */
    val houseType: HousingTypeUI? = null, // 1:Rented | 2:Own | 3:Family House | 0:Unselected
    val totalFamilyMembers: String? = null,
    val hasPets: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val totalCars: String? = null,

    /** @see LifeStylePartTwoViewModel */
    val hasWater: ServiceUI? = null, // 0:No or Unselected | 1:Yes
    val hasElectricity: ServiceUI? = null, // 0:No or Unselected | 1:Yes
    val hasPhone: ServiceUI? = null, // 0:No or Unselected | 1:Yes
    val hasTv: ServiceUI? = null, // 0:No or Unselected | 1:Yes
    val hasGas: ServiceUI? = null, // 0:No or Unselected | 1:Yes
    val hasInternet: ServiceUI? = null, // 0:No or Unselected | 1:Yes
    val transportType: TransportationMeanUI? = null, // 1:Foot | 2:Bicycle | 3:Car | 4:Public | 0:Unselected

    /** @see EconomicViewModel */
    val dependents: String? = null,
    val creditCards: String? = null,
    val hasLoan: Int? = null, // 1:Yes | 2:No | 0:Unselected

    /** @see AcademicViewModel */
    val educationLevel: EducationLvlUI? = null,
    val educationStatus: EducationStatusUI? = null,
    val institution: String? = null,
    val profession: String? = null,
    val professionCode: String? = null,

    /** @see JobReferencesViewModel */
    val socialSecurityNumber: String? = null,
    val workExperience: Int? = null, // 1:Yes | 2:No | 0:Unselected
    val currentlyWorking: Int? = null, // 1:Yes | 2:No | 0:Unselected

    /** @see SocialMediaViewModel */
    val hasSocialMedia: Boolean? = null, // 0:No or Unselected | 1:Yes
    val facebook: SocialNetworkUI? = null,
    val instagram: SocialNetworkUI? = null,
    val twitter: SocialNetworkUI? = null,
    val linkedin: SocialNetworkUI? = null,
    val pinterest: SocialNetworkUI? = null,
    val youtube: SocialNetworkUI? = null,
    val other: SocialNetworkUI? = null,

    val screen: Int? = null,
    val screenName: String? = null,
    val step: Int? = null
) {
    fun toRegisterEntityDb(useremail: String) =
        RegisterEntity(
            id = useremail.trim(),
            email = useremail.trim(),
            photoUrl = photoUrl,
            photoUri = photoUri?.toString(),
            firstName = firstName?.trim(),
            otherNames = otherNames?.trim(),
            fatherLastName = fatherLastName?.trim(),
            motherLastName = motherLastName?.trim(),
            phone = phone?.trim(),
            curp = curp?.trim(),
            nationality = nationality?.trim(),
            birthCountryId = birthCountryId?.trim(),

            birthYear = birthYear?.text?.trim(),
            birthMonth = birthMonth?.text?.trim(),
            birthDay = birthDay?.text?.trim(),
            birthStateId = birthState?.id,
            birthStateName = birthState?.stateName?.trim(),
            genderId = gender?.id,
            genderName = gender?.genderName,
            children = children?.text?.trim(),
            maritalId = marital?.id,
            maritalName = marital?.maritalName,

            postalCode = postalCode?.trim(),
            suburb = suburb?.trim(),
            municipality = municipality?.trim(),
            stateId = state?.id,
            stateName = state?.stateName?.trim(),
            street = street?.trim(),
            extNumber = extNumber?.trim(),
            intNumber = intNumber?.trim(),

            automotiveCredit = selectedAutomotiveCredit,
            mortgageCredit = selectedMortgageCredit,
            creditCard = selectedCreditCard,

            housingTypeId = houseType?.id,
            housingTypeName = houseType?.housingTypeName,
            totalFamilyMembers = totalFamilyMembers,
            hasPets = hasPets,
            totalCars = totalCars,

            waterId = hasWater?.id,
            waterName = hasWater?.serviceName,
            electricityId = hasElectricity?.id,
            electricityName = hasElectricity?.serviceName,
            phoneId = hasPhone?.id,
            phoneName = hasPhone?.serviceName,
            tvId = hasTv?.id,
            tvName = hasTv?.serviceName,
            gasId = hasGas?.id,
            gasName = hasGas?.serviceName,
            internetId = hasInternet?.id,
            internetName = hasInternet?.serviceName,
            transportId = transportType?.id,
            transportName = transportType?.transportationMeanName,

            dependents = dependents?.trim(),
            creditCards = creditCards?.trim(),
            hasLoan = hasLoan,

            educationLevelId = educationLevel?.id,
            educationLevelName = educationLevel?.educationLvlName?.trim(),
            educationStatusId = educationStatus?.id,
            educationStatusName = educationStatus?.educationStatusName?.trim(),
            institution = institution?.trim(),
            profession = profession?.trim(),
            professionCode = professionCode?.trim(),

            socialSecurityNumber = socialSecurityNumber?.trim(),
            workExperience = workExperience,
            currentlyWorking = currentlyWorking,

            hasSocialMedia = hasSocialMedia,
            facebookId = facebook?.id,
            facebookName = facebook?.description,
            facebookLink = facebook?.username,
            instagramId = instagram?.id,
            instagramName = instagram?.description,
            instagramLink = instagram?.username,
            twitterId = twitter?.id,
            twitterName = twitter?.description,
            twitterLink = twitter?.username,
            linkedinId = linkedin?.id,
            linkedinName = linkedin?.description,
            linkedinLink = linkedin?.username,
            pinterestId = pinterest?.id,
            pinterestName = pinterest?.description,
            pinterestLink = pinterest?.username,
            youtubeId = youtube?.id,
            youtubeName = youtube?.description,
            youtubeLink = youtube?.username,
            otherLink = other?.username,

            screen = screen,
            screenName = screenName,
            step = step
        )
}
