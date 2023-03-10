package com.intelab.joblab.data.repositories

import com.intelab.joblab.data.apis.CandidateApi
import com.intelab.joblab.data.common.utils.ErrorGenerator
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.entities.requests.*
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.candidate.ICandidateRepository
import com.intelab.joblab.presentation.ui.helpers.createPartFromString
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

class CandidateRepositoryImpl constructor(
    private val candidateApi: CandidateApi, 
    private val sharedPrefs: SharedPrefs,
    private val errorGenerator: ErrorGenerator
) : ICandidateRepository {

    override suspend fun registerNewCandidateOnServer(candidateRequest: CandidateRequest) = flow {
        val response = candidateApi.registerNewCandidateOnServer(candidateRequest)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun registerNewCandidateComplementaryOnServer(candidateComplementaryRequest: CandidateComplementaryRequest) = flow {
        val response = candidateApi.registerNewCandidateComplementaryOnServer(candidateComplementaryRequest)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendPreferableJobs(preferableJobs: List<PreferableJobs>) = flow {
        val response = candidateApi.sendPreferableJobs(PreferableJobsRequest(preferableJobs))
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun deleteAllPreferableJobs() = flow {
        val response = candidateApi.deletePreferableJobs()
        if (response.isSuccessful) {
            emit(BaseResult.Success(Unit))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendAccutestPhoto(file: MultipartBody.Part) = flow {
        val response = candidateApi.sendAccutestPhoto(file)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendAccutestAnswer(answers: String) = flow {
        val response = candidateApi.sendAccutestAnswer(AccutestAnswerRequest(answers))
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getHomeStatusResponse() = flow {
        val response = candidateApi.getHomeStatusResponseMock()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getPreferableJobs() = flow {
        val response = candidateApi.getJobPostulation()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!.preferableJobs))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getAccutestResult() = flow {
        val response = candidateApi.getAccutestResult()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun uploadFileToServer(
        file: MultipartBody.Part,
        name: CandidateUseCase.FilesExpected
    ) = flow {
        val response = candidateApi.sendFile(name.name, file)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun deleteFileFromServer(
        name: CandidateUseCase.FilesExpected
    ) = flow {
        val response = candidateApi.deleteFileFromServer(name.name)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun updatePreferableJobs(preferableJobs: List<PreferableJobs>) = flow {
        val response = candidateApi.updatePreferableJobs(PreferableJobsRequest(preferableJobs))
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getCandidateSocialNetworks() = flow {
        val response = candidateApi.getSocialNetworks()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!.socialNetworks))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendCandidateSocialNetworks(list: List<SocialNetworkUI>) = flow {
        val response =
            candidateApi.sendSocialNetworks(SocialNetworkRequest(socialNetworks = list))
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun deleteCandidateSocialNetworks() = flow {
        val response = candidateApi.deleteSocialNetworks()
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getCandidatePersonalInformation() = flow {
        val response = candidateApi.getCandidatePersonalInformation()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendPersonalInformationUpdate(personalInformationRequest: PersonalInformationRequest) = flow {
        val response = candidateApi.sendPersonalInformationUpdate(personalInformationRequest)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getEconomicInformation() = flow {
        val response = candidateApi.getFinancialInformation()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendEconomicInformationUpdate(financialRequest: Financial) = flow {
        val response = candidateApi.sendFinancialInformationUpdate(financialRequest)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getAcademicInformation() = flow {
        val response = candidateApi.getAcademicInformation()
        if (response.isSuccessful && response.body()?.educations?.isNotEmpty() == true) {
            emit(BaseResult.Success(response.body()!!.educations))
        } else {
            emit(BaseResult.Success(emptyList()))
        }
    }

    override suspend fun sendAcademicInformationUpdate(educations: Educations) = flow {
        val response = candidateApi.sendAcademicInformationUpdate(EducationsRequest(listOf(educations)))
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun deleteAcademicInformation(id: Int) = flow {
        val response = candidateApi.deleteAcademicInformation(id.toString())
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun addNewAcademicInformation(educations: Educations) = flow {
        val response = candidateApi.registerNewAcademicInformation(EducationsRequest(listOf(educations)))
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getJobReferencesInformation() = flow {
        val response = candidateApi.getJobReferencesInformation()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun addNewJobReferenceInformation(jobReferences: CandidateWorkExperience) = flow {
        val response = candidateApi.addNewJobReferenceInformation(jobReferences)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun deleteJobReference(id: Int) = flow {
        val response = candidateApi.deleteJobReferenceFromServer(id.toString())
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun deleteAllJobReferences() = flow {
        val response = candidateApi.deleteAllJobReferenceFromServer()
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun updateSocialIdentificationCodeRequest(code: String) = flow {
        val response = candidateApi.updateSocialIdentificationCode(SocialIdentificationCodeRequest(code))
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getDomicileInformation() = flow {
        val response = candidateApi.getDomicileInformation()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendDomicileInformationUpdate(domicileRequest: DomicileInformation) = flow {
        val response = candidateApi.sendDomicileInformationUpdate(domicileRequest)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getUserFinancialInformation() = flow {
        val response = candidateApi.getUserFinancialInformation()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun updateUserFinancialInformation(requestFinancialInformation: FinancialInformation) = flow {
        val response = candidateApi.updateUserFinancialInformation(requestFinancialInformation)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getLifeStyleInformation() = flow {
        val response = candidateApi.getLifeStyleInformation()
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()!!))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun updateLifeStyleInformation(lifeStyleRequest: HousingInformation) = flow {
        val response = candidateApi.updateLifeStyleInformation(lifeStyleRequest)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getCounterOfUnseenNotifications() = flow {
        val response = candidateApi.getUnseenNotifications()
        if (response.isSuccessful) {
            val res = response.body()!!.count
            emit(BaseResult.Success(res ?: 0))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun getTotalNotifications(start: String, end: String) = flow {
        val response = candidateApi.getFullNotifications(start = start, limit = end)
        if (response.isSuccessful) {
            val list = response.body()!!.results
            val listWithDuplicates = mutableListOf<Notifications>()
            list?.forEach { n -> n.period?.let { listWithDuplicates.add(Notifications(header = it)) } }
            val finalList = listWithDuplicates.toSet().toMutableList() // remove duplicates objects

            list?.forEach { notificationResponse ->
                finalList.forEach { notificationUI ->
                    if (notificationResponse.period == notificationUI.header) {
                        notificationUI.notifications.add(
                            Notification(
                                id = notificationResponse.id ?: 0,
                                mainText = "", // not used for now
                                complementaryText = notificationResponse.message,
                                isNew = notificationResponse.newMessage
                            )
                        )
                    }
                }
            }
            emit(BaseResult.Success(finalList))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun setNotificationsAsRead(from: Int) = flow {
        val response = candidateApi.setNotificationsAsRead(from)
        if (response.isSuccessful) {
            emit(BaseResult.Success(SuccessGenericResponse()))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }

    override suspend fun sendUserPhoto(file: MultipartBody.Part) = flow {
        val userEmail = sharedPrefs.getUserEmail()
        val response = candidateApi.sendUserPhoto(createPartFromString(userEmail), file)
        if (response.isSuccessful) {
            emit(BaseResult.Success(response.body()?.URL ?: ""))
        } else {
            errorGenerator.validateError(response)?.let { emit(it) }
        }
    }
}
