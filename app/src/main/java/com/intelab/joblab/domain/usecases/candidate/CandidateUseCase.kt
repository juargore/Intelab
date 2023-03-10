package com.intelab.joblab.domain.usecases.candidate

import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.entities.requests.*
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase.FilesExpected.*
import com.intelab.joblab.presentation.base.utils._document
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class CandidateUseCase @Inject constructor(
    private val candidateRepository: ICandidateRepository
) {

    private var homeStatusResponse: HomeStatusResponse? = null

    suspend fun registerNewCandidateOnServer(candidateRequest: CandidateRequest) =
        candidateRepository.registerNewCandidateOnServer(candidateRequest = candidateRequest)

    suspend fun registerNewCandidateComplementaryOnServer(candidateComplementaryRequest: CandidateComplementaryRequest) =
        candidateRepository.registerNewCandidateComplementaryOnServer(candidateComplementaryRequest)

    suspend fun sendPreferableJobs(jobs: List<PreferableJobs>) =
        candidateRepository.sendPreferableJobs(jobs)

    suspend fun deletePreferableJobs() =
        candidateRepository.deleteAllPreferableJobs()

    suspend fun sendAccutestPhoto(file: MultipartBody.Part) =
        candidateRepository.sendAccutestPhoto(file)

    suspend fun sendAccutestAnswer(answer: String) = candidateRepository.sendAccutestAnswer(answer)

    suspend fun getProfileInformation() = candidateRepository.getHomeStatusResponse()

    suspend fun getPercentageCompletedAtHome() = flow {
        when (val result = candidateRepository.getHomeStatusResponse().firstOrNull()!!) {
            is BaseResult.Success -> {
                homeStatusResponse = result.data
                emit(BaseResult.Success(result.data.profileStatus?.percentageCompleted ?: 0))
            }
            is BaseResult.Error -> emit(BaseResult.Error(result.rawResponse))
        }
    }

    private fun getStatus(value: String): LoadedStatus {
        return when (value) {
            LoadedStatus.COMPLETED.name -> LoadedStatus.COMPLETED
            LoadedStatus.PENDING.name -> LoadedStatus.PENDING
            LoadedStatus.TO_EXPIRE.name -> LoadedStatus.TO_EXPIRE
            else -> LoadedStatus.EXPIRED
        }
    }

    suspend fun getPreferableJobs() = candidateRepository.getPreferableJobs()

    suspend fun getAccutestResult() = candidateRepository.getAccutestResult()

    suspend fun deleteFileFromServer(name: FilesExpected) = candidateRepository.deleteFileFromServer(name)

    suspend fun uploadFileToServer(file: MultipartBody.Part, name: FilesExpected) =
        candidateRepository.uploadFileToServer(file, name)

    enum class FilesExpected {
        ACADEMIC_DEGREE,
        AVATAR,
        ID_OFICIAL,
        PROOF_OF_RESIDENCE,
        RESUME
    }

    suspend fun updatePreferableJobs(preferableJobs: List<PreferableJobs>) =
        candidateRepository.updatePreferableJobs(preferableJobs)

    suspend fun getCandidateSocialNetworks() = candidateRepository.getCandidateSocialNetworks()

    suspend fun sendCandidateSocialNetworks(list: List<SocialNetworkUI>) =
        candidateRepository.sendCandidateSocialNetworks(list)

    suspend fun deleteCandidateSocialNetworks() =
        candidateRepository.deleteCandidateSocialNetworks()

    suspend fun getCandidatePersonalInformation() =
        candidateRepository.getCandidatePersonalInformation()

    suspend fun sendPersonalInformationUpdate(personalInformationRequest: PersonalInformationRequest) =
        candidateRepository.sendPersonalInformationUpdate(personalInformationRequest)

    suspend fun getEconomicInformation() = candidateRepository.getEconomicInformation()

    suspend fun sendEconomicInformationUpdate(financialRequest: Financial) =
        candidateRepository.sendEconomicInformationUpdate(financialRequest)

    suspend fun getAcademicInformation() = candidateRepository.getAcademicInformation()

    suspend fun deleteAcademicInformation(id: Int) =
        candidateRepository.deleteAcademicInformation(id)

    suspend fun sendAcademicInformationUpdate(educations: Educations) =
        candidateRepository.sendAcademicInformationUpdate(educations)

    suspend fun addNewAcademicInformation(educations: Educations) =
        candidateRepository.addNewAcademicInformation(educations)

    suspend fun getJobReferencesInformation() = candidateRepository.getJobReferencesInformation()

    suspend fun addNewJobReferenceInformation(jobReference: CandidateWorkExperience) =
        candidateRepository.addNewJobReferenceInformation(jobReference)

    suspend fun deleteJobReference(id: Int) = candidateRepository.deleteJobReference(id)

    suspend fun deleteAllJobReferences() = candidateRepository.deleteAllJobReferences()

    suspend fun updateSocialIdentificationCodeRequest(code: String) =
        candidateRepository.updateSocialIdentificationCodeRequest(code)

    suspend fun getDomicileInformation() = candidateRepository.getDomicileInformation()

    suspend fun sendDomicileInformationUpdate(domicile: DomicileInformation) =
        candidateRepository.sendDomicileInformationUpdate(domicile)

    suspend fun getUserFinancialInformation() = candidateRepository.getUserFinancialInformation()

    suspend fun updateUserFinancialInformation(requestFinancialInformation: FinancialInformation) =
        candidateRepository.updateUserFinancialInformation(requestFinancialInformation)

    suspend fun getLifeStyleInformation() = candidateRepository.getLifeStyleInformation()

    suspend fun updateLifeStyleInformation(lifeStyleRequest: HousingInformation) =
        candidateRepository.updateLifeStyleInformation(lifeStyleRequest)

    suspend fun getCounterOfUnseenNotifications() =
        candidateRepository.getCounterOfUnseenNotifications()

    suspend fun getTotalNotifications(start: String, end: String) =
        candidateRepository.getTotalNotifications(start, end)

    suspend fun setNotificationsAsRead(from: Int) = candidateRepository.setNotificationsAsRead(from)

    suspend fun sendUserPhoto(file: MultipartBody.Part) = candidateRepository.sendUserPhoto(file)

    suspend fun getItemsDocumentsHome() = flow {
        val mList = mutableListOf<ItemHomeDocument>()
        homeStatusResponse?.profileStatus?.items?.forEachIndexed { i, status ->
            if (status.group == _document) {
                mList.add(
                    ItemHomeDocument(
                        id = i,
                        status = getStatus(status.status),
                        description = status.description,
                        type = when (status.type) {
                            ID_OFICIAL.name -> ID_OFICIAL
                            ACADEMIC_DEGREE.name -> ACADEMIC_DEGREE
                            PROOF_OF_RESIDENCE.name -> PROOF_OF_RESIDENCE
                            RESUME.name -> RESUME
                            else -> RESUME
                        },
                        icon = when (status.type) {
                            ID_OFICIAL.name -> R.mipmap.img_official_id_gray
                            ACADEMIC_DEGREE.name -> R.mipmap.img_academic_title_gray
                            PROOF_OF_RESIDENCE.name -> R.mipmap.img_domicilie_gray
                            else -> R.mipmap.img_cv_gray
                        }
                    )
                )
            }
        }
        emit(BaseResult.Success(mList))
    }

    suspend fun getItemsProfileHome() = flow {
        val mList = mutableListOf<ItemHomeProfile>()
        val result : BaseResult<HomeStatusResponse, ErrorGenericResponse>? =
            candidateRepository.getHomeStatusResponse().firstOrNull()
        if (result != null && result is BaseResult.Success) {
            result.data.profileStatus?.items?.forEachIndexed { i, status ->
                mList.add(
                    ItemHomeProfile(
                        id = i,
                        type = status.type,
                        status = getStatus(status.status),
                        description = status.description
                    )
                )
            }
            emit(BaseResult.Success(mList))
        } else if (result is BaseResult.Error) {
            emit(BaseResult.Error(result.rawResponse))
        }
    }
}
