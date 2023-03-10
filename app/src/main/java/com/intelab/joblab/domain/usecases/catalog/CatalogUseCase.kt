package com.intelab.joblab.domain.usecases.catalog

import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorResponse
import com.intelab.joblab.domain.entities.JobUI
import com.intelab.joblab.domain.entities.ParentJobUI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CatalogUseCase @Inject constructor(
    private val catalogRepository: ICatalogRepository
) {

    suspend fun getStatesByCountryId(countryId: Int) = catalogRepository.getStatesByCountryId(countryId)

    suspend fun getGenders() = catalogRepository.getGenders()

    suspend fun getServices() = catalogRepository.getServices()

    suspend fun getEducationLevels() = catalogRepository.getEducationLevels()

    suspend fun getEducationStatus() = catalogRepository.getEducationStatus()

    suspend fun getHousingTypes() = catalogRepository.getHousingTypes()

    suspend fun getTransportationMeans() = catalogRepository.getTransportationMeans()

    suspend fun getSocialNetworks() = catalogRepository.getSocialNetworks()

    suspend fun getMaritalStatus() = catalogRepository.getMaritalStatus()

    suspend fun getAccutest(type: String) = catalogRepository.getAccutest(type)

    suspend fun getPrivacyNotice() = catalogRepository.getPrivacyNotice()

    suspend fun getConsentNotice() = catalogRepository.getConsentNotice()

    suspend fun getJobs(): Flow<BaseResult<List<ParentJobUI>, ErrorResponse>> {
        return catalogRepository.getJobs().map { result ->
            when (result) {
                is BaseResult.Error -> return@map BaseResult.Error(result.rawResponse)
                is BaseResult.Success -> {
                    var parentList = mutableListOf<ParentJobUI>()

                    // add all first letters of each job to the list
                    result.data.forEach { job ->
                        val letter = job.jobName.substring(0, 1)
                        parentList.add(ParentJobUI(header = letter, jobList = mutableListOf()))
                    }

                    // remove first letter duplicates
                    parentList = parentList.toSet().toMutableList()

                    // every time a job starts with selected letter -> add to list
                    result.data.forEach { job ->
                        parentList.forEachIndexed { i, parent ->
                            if (job.jobName.startsWith(parent.header))
                                parentList[i].jobList.add(
                                    JobUI(
                                        id = job.id,
                                        jobName = job.jobName,
                                        false
                                    )
                                )
                        }
                    }

                    // return result as List of ParentJobUI instead JobUI
                    return@map BaseResult.Success(parentList)
                }
            }
        }
    }
}
