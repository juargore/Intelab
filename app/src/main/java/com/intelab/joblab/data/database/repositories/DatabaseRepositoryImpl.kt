package com.intelab.joblab.data.database.repositories

import com.intelab.joblab.data.database.dao.JobPostulationDao
import com.intelab.joblab.data.database.dao.JobReferenceDao
import com.intelab.joblab.data.database.dao.RegistrationDao
import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.JobPostulation
import com.intelab.joblab.domain.entities.JobReference
import com.intelab.joblab.domain.usecases.database.IDatabaseRepository
import com.intelab.joblab.presentation.ui.helpers.images.ImageGalleryOrCameraViewModel
import com.intelab.joblab.presentation.ui.home.register.viewmodels.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val registrationDao: RegistrationDao,
    private val jobReferenceDao: JobReferenceDao,
    private val jobPostulationDao: JobPostulationDao,
    private val prefs: SharedPrefs
) : IDatabaseRepository {

    override suspend fun insertOrUpdateRegistrationData(
        data: ComplementaryRegister,
        className: String?
    ) {
        val userEmail = prefs.getUserEmail()
        val result = registrationDao.getComplementaryRegister(userEmail)

        if (result == null) {
            // new register
            registrationDao.insert(data.toRegisterEntityDb(userEmail))
        } else {
            // update register
            val db = data.toRegisterEntityDb(userEmail)
            when (className) {
                PersonalInformationViewModel::class.simpleName -> {
                    registrationDao.updatePersonalInformation(
                        useremail = userEmail,
                        photo = db.photoUrl ?: "",
                        photoUri = db.photoUri ?: "",
                        firstName = db.firstName ?: "",
                        otherNames = db.otherNames ?: "",
                        fatherLastName = db.fatherLastName ?: "",
                        motherLastName = db.motherLastName ?: "",
                        phone = db.phone ?: "",
                        curp = db.curp ?: "",
                        nationality = db.nationality ?: "",
                        birthCountryId = db.birthCountryId ?: ""
                    )
                }
                PersonalInformationPartTwoViewModel::class.simpleName ->
                    registrationDao.updatePersonalInformationPartTwo(
                        useremail = userEmail,
                        birthYear = db.birthYear ?: "",
                        birthMonth = db.birthMonth ?: "",
                        birthDay = db.birthDay ?: "",
                        birthStateId = db.birthStateId ?: 0,
                        birthStateName = db.birthStateName ?: "",
                        genderId = db.genderId ?: 0,
                        genderName = db.genderName ?: "",
                        children = db.children ?: "0",
                        maritalId = db.maritalId ?: 0,
                        maritalName = db.maritalName ?: "",
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                DomicileViewModel::class.simpleName ->
                    registrationDao.updateDomicile(
                        useremail = userEmail,
                        postalCode = db.postalCode ?: "",
                        suburb = db.suburb ?: "",
                        municipality = db.municipality ?: "",
                        stateId = db.stateId ?: 0,
                        stateName = db.stateName ?: "",
                        street = db.street ?: "",
                        extNumber = db.extNumber ?: "",
                        intNumber = db.intNumber ?: "",
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                CreditBureauViewModel::class.simpleName ->
                    registrationDao.updateCreditBureau(
                        useremail = userEmail,
                        automotiveCredit = db.automotiveCredit ?: 0,
                        mortgageCredit = db.mortgageCredit ?: 0,
                        creditCard = db.creditCard ?: 0,
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                CreditBureauValidateState::class.simpleName ->
                    registrationDao.updateCreditBureauValidate(
                        useremail = userEmail,
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                LifeStyleViewModel::class.simpleName ->
                    registrationDao.updateLifeStyle(
                        useremail = userEmail,
                        housingTypeId = db.housingTypeId ?: 0,
                        housingTypeName = db.housingTypeName ?: "",
                        totalFamilyMembers = db.totalFamilyMembers ?: "",
                        hasPets = db.hasPets ?: 0,
                        totalCars = db.totalCars ?: "",
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                LifeStylePartTwoViewModel::class.simpleName ->
                    registrationDao.updateLifeStylePartTwo(
                        useremail = userEmail,
                        waterId = db.waterId ?: 0,
                        waterName = db.waterName ?: "",
                        electricityId = db.electricityId ?: 0,
                        electricityName = db.electricityName ?: "",
                        phoneId = db.phoneId ?: 0,
                        phoneName = db.phoneName ?: "",
                        tvId = db.tvId ?: 0,
                        tvName = db.tvName ?: "",
                        gasId = db.gasId ?: 0,
                        gasName = db.gasName ?: "",
                        internetId = db.internetId ?: 0,
                        internetName = db.internetName ?: "",
                        transportId = db.transportId ?: 0,
                        transportName = db.transportName ?: "",
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                EconomicViewModel::class.simpleName ->
                    registrationDao.updateEconomics(
                        useremail = userEmail,
                        dependents = db.dependents ?: "",
                        creditCards = db.creditCards ?: "",
                        hasLoan = db.hasLoan ?: 0,
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                AcademicViewModel::class.simpleName ->
                    registrationDao.updateAcademic(
                        useremail = userEmail,
                        educationLevelId = db.educationLevelId ?: 0,
                        educationLevelName = db.educationLevelName ?: "",
                        educationStatusId = db.educationStatusId ?: 0,
                        educationStatusName = db.educationStatusName ?: "",
                        institution = db.institution ?: "",
                        profession = db.profession ?: "",
                        professionCode = db.professionCode ?: "",
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                JobReferencesViewModel::class.simpleName ->
                    registrationDao.updateJobReferences(
                        useremail = userEmail,
                        socialSecurityNumber = db.socialSecurityNumber ?: "",
                        workExperience = db.workExperience ?: 0,
                        currentlyWorking = db.currentlyWorking ?: 0,
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                SocialMediaViewModel::class.simpleName ->
                    registrationDao.updateSocialMedia(
                        useremail = userEmail,
                        hasSocialMedia = db.hasSocialMedia ?: false,
                        facebookId = db.facebookId ?: 0,
                        facebookLink = db.facebookLink ?: "",
                        facebookName = db.facebookName ?: "",
                        instagramId = db.instagramId ?: 0,
                        instagramLink = db.instagramLink ?: "",
                        instagramName = db.instagramName ?: "",
                        twitterId = db.twitterId ?: 0,
                        twitterLink = db.twitterLink ?: "",
                        twitterName = db.twitterName ?: "",
                        linkedinId = db.linkedinId ?: 0,
                        linkedinLink = db.linkedinLink ?: "",
                        linkedinName = db.linkedinName ?: "",
                        pinterestId = db.pinterestId ?: 0,
                        pinterestLink = db.pinterestLink ?: "",
                        pinterestName = db.pinterestName ?: "",
                        youtubeId = db.youtubeId ?: 0,
                        youtubeLink = db.youtubeLink ?: "",
                        youtubeName = db.youtubeName ?: "",
                        otherLink = db.otherLink ?: "",
                        screen = db.screen ?: 0,
                        screenName = db.screenName ?: "",
                        step = db.step ?: 0
                    )
                ImageGalleryOrCameraViewModel::class.simpleName ->
                    registrationDao.updatePhoto(useremail = userEmail, photo = db.photoUrl ?: "")
                else -> Unit
            }
        }
    }

    override suspend fun getRegistrationData(): Flow<ComplementaryRegister> {
        return flow {
            val useremail = prefs.getUserEmail()
            val result = registrationDao.getComplementaryRegister(useremail)
            if (result == null) {
                emit(ComplementaryRegister())
            } else {
                emit(result.toComplementaryRegister())
            }
        }
    }

    override suspend fun deleteCurrentRegister() {
        val useremail = prefs.getUserEmail()
        val result = registrationDao.getComplementaryRegister(useremail)
        if (result != null) {
            registrationDao.delete(useremail)
        }
    }

    override suspend fun insertOrUpdateJobReference(data: JobReference) {
        val useremail = prefs.getUserEmail()
        jobReferenceDao.insertJobReference(data.toJobEntityDb(useremail))
    }

    override suspend fun getJobReferences(): Flow<List<JobReference>> {
        return flow {
            val useremail = prefs.getUserEmail()
            val result = jobReferenceDao.getJobReferences(useremail)
            if (result == null) {
                emit(listOf())
            } else {
                val jobReferences = result.map { it.map { entity -> entity.toJobReference() }}
                emitAll(jobReferences)
            }
        }
    }

    override suspend fun deleteJobReference(id: Int) {
        jobReferenceDao.deleteJobReference(id)
    }

    override suspend fun getJobReference(id: Int): JobReference? {
        return jobReferenceDao.getJobReferenceById(id)?.toJobReference()
    }

    override suspend fun insertJobPostulation(data: JobPostulation) {
        val useremail = prefs.getUserEmail()
        jobPostulationDao.insetJobPostulation(data.toJobPostulationEntity(useremail))
    }

    override suspend fun getAllJobPostulations(): Flow<List<JobPostulation>> {
        return flow {
            val result = jobPostulationDao.getAllSelectedJobPostulations()
            if (result == null) {
                emit(listOf())
            } else {
                val postulations = result.map { it.toJobPostulation() }
                emit(postulations)
            }
        }
    }

    override suspend fun deleteAllJobPostulation() {
        jobPostulationDao.deleteAllPostulation()
    }

    override suspend fun deleteAllJobReferences() {
        jobReferenceDao.deleteAllJobReferences()
    }
}
