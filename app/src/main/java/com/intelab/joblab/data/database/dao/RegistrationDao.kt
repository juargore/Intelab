package com.intelab.joblab.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intelab.joblab.data.database.entities.RegisterEntity

@Dao
interface RegistrationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: RegisterEntity)

    @Query("SELECT * FROM Register WHERE id = :useremail")
    fun getComplementaryRegister(useremail: String): RegisterEntity?

    @Query("DELETE FROM Register WHERE id = :useremail")
    fun delete(useremail: String)

    @Query(
        "UPDATE Register SET " +
                "photoUrl = :photo, " +
                "photoUri = :photoUri, " +
                "firstName = :firstName, " +
                "otherNames = :otherNames, " +
                "fatherLastName = :fatherLastName, " +
                "motherLastName = :motherLastName, " +
                "phone = :phone, " +
                "curp = :curp, " +
                "nationality = :nationality, " +
                "birthCountryId = :birthCountryId WHERE id = :useremail"
    )
    fun updatePersonalInformation(
        useremail: String,
        photo: String,
        photoUri: String,
        firstName: String,
        otherNames: String,
        fatherLastName: String,
        motherLastName: String,
        phone: String,
        curp: String,
        nationality: String,
        birthCountryId: String
    )

    @Query(
        "UPDATE Register SET " +
                "birthYear = :birthYear, " +
                "birthMonth = :birthMonth, " +
                "birthDay = :birthDay, " +
                "birthStateId = :birthStateId, " +
                "birthStateName = :birthStateName, " +
                "genderId = :genderId, " +
                "genderName = :genderName, " +
                "children = :children, " +
                "maritalId = :maritalId, " +
                "maritalName = :maritalName, " +
                "screen = :screen," +
                "screenName = :screenName," +
                "step = :step WHERE id = :useremail"
    )
    fun updatePersonalInformationPartTwo(
        useremail: String,
        birthYear: String,
        birthMonth: String,
        birthDay: String,
        birthStateId: Int,
        birthStateName: String,
        genderId: Int,
        genderName: String,
        children: String,
        maritalId: Int,
        maritalName: String,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "postalCode = :postalCode, " +
                "suburb = :suburb, " +
                "municipality = :municipality, " +
                "stateId = :stateId, " +
                "stateName = :stateName, " +
                "street = :street, " +
                "extNumber = :extNumber, " +
                "intNumber = :intNumber, " +
                "screen = :screen," +
                "screenName = :screenName," +
                "step = :step WHERE id = :useremail"
    )
    fun updateDomicile(
        useremail: String,
        postalCode: String,
        suburb: String,
        municipality: String,
        stateId: Int,
        stateName: String,
        street: String,
        extNumber: String,
        intNumber: String,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "automotiveCredit = :automotiveCredit, " +
                "mortgageCredit = :mortgageCredit, " +
                "creditCard = :creditCard, " +
                "screen = :screen," +
                "screenName = :screenName," +
                "step = :step WHERE id = :useremail"
    )
    fun updateCreditBureau(
        useremail: String,
        automotiveCredit: Int,
        mortgageCredit: Int,
        creditCard: Int,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "screen = :screen, " +
                "screenName = :screenName, " +
                "step = :step WHERE id = :useremail"
    )
    fun updateCreditBureauValidate(
        useremail: String,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "housingTypeId = :housingTypeId, " +
                "housingTypeName = :housingTypeName, " +
                "totalFamilyMembers = :totalFamilyMembers, " +
                "hasPets = :hasPets, " +
                "totalCars = :totalCars, " +
                "screen = :screen, " +
                "screenName = :screenName, " +
                "step = :step WHERE id = :useremail"
    )
    fun updateLifeStyle(
        useremail: String,
        housingTypeId: Int,
        housingTypeName: String,
        totalFamilyMembers: String,
        hasPets: Int,
        totalCars: String,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "waterId = :waterId, " +
                "waterName = :waterName, " +
                "electricityId = :electricityId, " +
                "electricityName = :electricityName, " +
                "phoneId = :phoneId, " +
                "phoneName = :phoneName, " +
                "tvId = :tvId, " +
                "tvName = :tvName, " +
                "gasId = :gasId, " +
                "gasName = :gasName, " +
                "internetId = :internetId, " +
                "internetName = :internetName, " +
                "transportId = :transportId, " +
                "transportName = :transportName, " +
                "screen = :screen, " +
                "screenName = :screenName, " +
                "step = :step WHERE id = :useremail"
    )
    fun updateLifeStylePartTwo(
        useremail: String,
        waterId: Int,
        waterName: String,
        electricityId: Int,
        electricityName: String,
        phoneId: Int,
        phoneName: String,
        tvId: Int,
        tvName: String,
        gasId: Int,
        gasName: String,
        internetId: Int,
        internetName: String,
        transportId: Int,
        transportName: String,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "dependents = :dependents, " +
                "creditCards = :creditCards, " +
                "hasLoan = :hasLoan, " +
                "screen = :screen, " +
                "screenName = :screenName, " +
                "step = :step WHERE id = :useremail"
    )
    fun updateEconomics(
        useremail: String,
        dependents: String,
        creditCards: String,
        hasLoan: Int,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "educationLevelId = :educationLevelId, " +
                "educationLevelName = :educationLevelName, " +
                "educationStatusId = :educationStatusId, " +
                "educationStatusName = :educationStatusName, " +
                "institution = :institution, " +
                "profession = :profession, " +
                "professionCode = :professionCode, " +
                "screen = :screen, " +
                "screenName = :screenName, " +
                "step = :step WHERE id = :useremail"
    )
    fun updateAcademic(
        useremail: String,
        educationLevelId: Int,
        educationLevelName: String,
        educationStatusId: Int,
        educationStatusName: String,
        institution: String,
        profession: String,
        professionCode: String,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "socialSecurityNumber = :socialSecurityNumber, " +
                "workExperience = :workExperience, " +
                "currentlyWorking = :currentlyWorking, " +
                "screen = :screen, " +
                "screenName = :screenName, " +
                "step = :step WHERE id = :useremail"
    )
    fun updateJobReferences(
        useremail: String,
        socialSecurityNumber: String,
        workExperience: Int,
        currentlyWorking: Int,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "hasSocialMedia = :hasSocialMedia, " +
                "facebookId = :facebookId, " +
                "facebookLink = :facebookLink, " +
                "facebookName = :facebookName, " +
                "instagramId = :instagramId, " +
                "instagramLink = :instagramLink, " +
                "instagramName = :instagramName, " +
                "twitterId = :twitterId, " +
                "twitterLink = :twitterLink, " +
                "twitterName = :twitterName, " +
                "linkedinId = :linkedinId, " +
                "linkedinLink = :linkedinLink, " +
                "linkedinName = :linkedinName, " +
                "pinterestId = :pinterestId, " +
                "pinterestLink = :pinterestLink, " +
                "pinterestName = :pinterestName, " +
                "youtubeId = :youtubeId, " +
                "youtubeLink = :youtubeLink, " +
                "youtubeName = :youtubeName, " +
                "otherLink = :otherLink, " +
                "screen = :screen, " +
                "screenName = :screenName, " +
                "step = :step WHERE id = :useremail"
    )
    fun updateSocialMedia(
        useremail: String,
        hasSocialMedia: Boolean,
        facebookId: Int,
        facebookLink: String,
        facebookName: String,
        instagramId: Int,
        instagramLink: String,
        instagramName: String,
        twitterId: Int,
        twitterLink: String,
        twitterName: String,
        linkedinId: Int,
        linkedinLink: String,
        linkedinName: String,
        pinterestId: Int,
        pinterestLink: String,
        pinterestName: String,
        youtubeId: Int,
        youtubeLink: String,
        youtubeName: String,
        otherLink: String,
        screen: Int,
        screenName: String,
        step: Int
    )

    @Query(
        "UPDATE Register SET " +
                "photoUrl = :photo " +
                "WHERE id = :useremail"
    )
    fun updatePhoto(
        useremail: String,
        photo: String
    )
}
