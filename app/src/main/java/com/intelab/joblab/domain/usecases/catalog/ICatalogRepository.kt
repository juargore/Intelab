package com.intelab.joblab.domain.usecases.catalog

import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import kotlinx.coroutines.flow.Flow

interface ICatalogRepository {

    suspend fun getCountries(): Flow<BaseResult<List<CountryUI>, ErrorResponse>>

    suspend fun getStatesByCountryId(countryId: Int): Flow<BaseResult<List<StateUI>, ErrorResponse>>

    suspend fun getJobs(): Flow<BaseResult<List<JobUI>, ErrorResponse>>

    suspend fun getGenders(): Flow<BaseResult<List<GenderUI>, ErrorResponse>>

    suspend fun getServices(): Flow<BaseResult<List<ServiceUI>, ErrorResponse>>

    suspend fun getEducationLevels(): Flow<BaseResult<List<EducationLvlUI>, ErrorResponse>>

    suspend fun getEducationStatus(): Flow<BaseResult<List<EducationStatusUI>, ErrorResponse>>

    suspend fun getHousingTypes(): Flow<BaseResult<List<HousingTypeUI>, ErrorResponse>>

    suspend fun getTransportationMeans(): Flow<BaseResult<List<TransportationMeanUI>, ErrorResponse>>

    suspend fun getSocialNetworks(): Flow<BaseResult<List<SocialNetworkUI>, ErrorResponse>>

    suspend fun getMaritalStatus(): Flow<BaseResult<List<MaritalUI>, ErrorResponse>>

    suspend fun getAccutest(type: String): Flow<BaseResult<AccutestResponse, ErrorGenericResponse>>

    suspend fun getPrivacyNotice(): Flow<BaseResult<PrivacyConsentResponse, ErrorResponse>>

    suspend fun getConsentNotice(): Flow<BaseResult<PrivacyConsentResponse, ErrorResponse>>
}
