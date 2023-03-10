package com.intelab.joblab.data.repositories

import com.intelab.joblab.data.apis.CatalogsApi
import com.intelab.joblab.data.common.utils.ErrorGenerator
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.usecases.catalog.ICatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CatalogRepositoryImpl constructor(
    private val catalogApi: CatalogsApi,
    private val errorGenerator: ErrorGenerator
) : ICatalogRepository {

    override suspend fun getCountries(): Flow<BaseResult<List<CountryUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsCountries()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toCountryUI() }))
            }
        }
    }

    override suspend fun getStatesByCountryId(countryId: Int): Flow<BaseResult<List<StateUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsStates(countryId)
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toStateUI() }))
            }
        }
    }

    override suspend fun getJobs(): Flow<BaseResult<List<JobUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsJobs()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toJobUI() }))
            }
        }
    }

    override suspend fun getGenders(): Flow<BaseResult<List<GenderUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsGenders()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toGenderUI() }))
            }
        }
    }

    override suspend fun getServices(): Flow<BaseResult<List<ServiceUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsServices()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toServicesUI() }))
            }
        }
    }

    override suspend fun getEducationLevels(): Flow<BaseResult<List<EducationLvlUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsEducationLevels()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toEducationLvlUI() }))
            }
        }
    }

    override suspend fun getEducationStatus(): Flow<BaseResult<List<EducationStatusUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsEducationStatus()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toEducationStatusUI() }))
            }
        }
    }

    override suspend fun getHousingTypes(): Flow<BaseResult<List<HousingTypeUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsHousingTypes()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toHousingTypeUI() }))
            }
        }
    }

    override suspend fun getTransportationMeans(): Flow<BaseResult<List<TransportationMeanUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsTransportationMeans()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toTransportationMeanUI() }))
            }
        }
    }

    override suspend fun getSocialNetworks(): Flow<BaseResult<List<SocialNetworkUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getCatalogsSocialNetworks()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toSocialNetworkUI() }))
            }
        }
    }

    override suspend fun getMaritalStatus(): Flow<BaseResult<List<MaritalUI>, ErrorResponse>> {
        return flow {
            val response = catalogApi.getMaritalStatus()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!.map { it.toMaritalUI() }))
            }
        }
    }

    override suspend fun getAccutest(type: String): Flow<BaseResult<AccutestResponse, ErrorGenericResponse>> =
        flow {
            val response = catalogApi.getAccutest(type)
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!))
            } else {
                emit(errorGenerator.returnErrorResponse(response))
            }
        }

    override suspend fun getPrivacyNotice(): Flow<BaseResult<PrivacyConsentResponse, ErrorResponse>> =
        flow {
            val response = catalogApi.getPrivacyNotice()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!))
            }
        }

    override suspend fun getConsentNotice(): Flow<BaseResult<PrivacyConsentResponse, ErrorResponse>> =
        flow {
            val response = catalogApi.getConsentNotice()
            if (response.isSuccessful) {
                emit(BaseResult.Success(response.body()!!))
            }
        }
}