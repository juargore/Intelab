package com.intelab.joblab.domain.usecases.candidate

import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.entities.requests.*
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase.FilesExpected
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface ICandidateRepository {

    suspend fun registerNewCandidateOnServer(candidateRequest: CandidateRequest): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun registerNewCandidateComplementaryOnServer(candidateComplementaryRequest: CandidateComplementaryRequest): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun sendPreferableJobs(preferableJobs: List<PreferableJobs>): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun deleteAllPreferableJobs(): Flow<BaseResult<Unit, ErrorGenericResponse>>

    suspend fun sendAccutestPhoto(file: MultipartBody.Part): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun sendAccutestAnswer(answers: String): Flow<BaseResult<AccutestTestResponse, ErrorGenericResponse>>

    suspend fun getHomeStatusResponse(): Flow<BaseResult<HomeStatusResponse, ErrorGenericResponse>>

    suspend fun getPreferableJobs(): Flow<BaseResult<List<JobPostulation>, ErrorGenericResponse>>

    suspend fun getAccutestResult(): Flow<BaseResult<AccutestResultResponse, ErrorGenericResponse>>

    suspend fun uploadFileToServer(file: MultipartBody.Part, name: FilesExpected): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun deleteFileFromServer(name: FilesExpected): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun updatePreferableJobs(preferableJobs: List<PreferableJobs>): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getCandidateSocialNetworks(): Flow<BaseResult<List<SocialNetworkUI>, ErrorGenericResponse>>

    suspend fun sendCandidateSocialNetworks(list: List<SocialNetworkUI>): Flow<BaseResult<GeneralResponseOneMessage, ErrorGenericResponse>>

    suspend fun deleteCandidateSocialNetworks(): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getCandidatePersonalInformation(): Flow<BaseResult<PersonalInformation, ErrorGenericResponse>>

    suspend fun sendPersonalInformationUpdate(personalInformationRequest: PersonalInformationRequest): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getEconomicInformation(): Flow<BaseResult<Financial, ErrorGenericResponse>>

    suspend fun sendEconomicInformationUpdate(financialRequest: Financial): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getAcademicInformation(): Flow<BaseResult<List<Educations>, ErrorGenericResponse>>

    suspend fun sendAcademicInformationUpdate(educations: Educations): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun deleteAcademicInformation(id: Int): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun addNewAcademicInformation(educations: Educations): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getJobReferencesInformation(): Flow<BaseResult<CandidateWorkExperience, ErrorGenericResponse>>

    suspend fun addNewJobReferenceInformation(jobReferences: CandidateWorkExperience): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun deleteJobReference(id: Int): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun deleteAllJobReferences(): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun updateSocialIdentificationCodeRequest(code: String): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getDomicileInformation(): Flow<BaseResult<DomicileInformation, ErrorGenericResponse>>

    suspend fun sendDomicileInformationUpdate(domicileRequest: DomicileInformation): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getUserFinancialInformation(): Flow<BaseResult<FinancialInformation, ErrorGenericResponse>>

    suspend fun updateUserFinancialInformation(requestFinancialInformation: FinancialInformation): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getLifeStyleInformation(): Flow<BaseResult<HousingInformation, ErrorGenericResponse>>

    suspend fun updateLifeStyleInformation(lifeStyleRequest: HousingInformation): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun getCounterOfUnseenNotifications(): Flow<BaseResult<Int, ErrorGenericResponse>>

    suspend fun getTotalNotifications(start: String, end: String): Flow<BaseResult<List<Notifications>, ErrorGenericResponse>>

    suspend fun setNotificationsAsRead(from: Int): Flow<BaseResult<SuccessGenericResponse, ErrorGenericResponse>>

    suspend fun sendUserPhoto(file: MultipartBody.Part): Flow<BaseResult<String, ErrorGenericResponse>>
}
