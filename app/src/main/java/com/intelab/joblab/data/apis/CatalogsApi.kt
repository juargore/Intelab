package com.intelab.joblab.data.apis

import com.intelab.joblab.domain.entities.AccutestResponse
import com.intelab.joblab.domain.entities.CatalogResponse
import com.intelab.joblab.domain.entities.PrivacyConsentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface CatalogsApi {

    @GET("catalogs/countries")
    suspend fun getCatalogsCountries(): Response<List<CatalogResponse>>

    @GET("catalogs/countries/{countryId}/states")
    suspend fun getCatalogsStates(@Path("countryId") countryId: Int): Response<List<CatalogResponse>>

    @GET("catalogs/jobs")
    suspend fun getCatalogsJobs(): Response<List<CatalogResponse>>

    @GET("catalogs/genders")
    suspend fun getCatalogsGenders(): Response<List<CatalogResponse>>

    @GET("catalogs/services")
    suspend fun getCatalogsServices(): Response<List<CatalogResponse>>

    @GET("catalogs/education-levels")
    suspend fun getCatalogsEducationLevels(): Response<List<CatalogResponse>>

    @GET("catalogs/education-statuses")
    suspend fun getCatalogsEducationStatus(): Response<List<CatalogResponse>>

    @GET("catalogs/housing-types")
    suspend fun getCatalogsHousingTypes(): Response<List<CatalogResponse>>

    @GET("catalogs/transportation-means")
    suspend fun getCatalogsTransportationMeans(): Response<List<CatalogResponse>>

    @GET("catalogs/social-networks")
    suspend fun getCatalogsSocialNetworks(): Response<List<CatalogResponse>>

    @GET("catalogs/marital-statuses")
    suspend fun getMaritalStatus(): Response<List<CatalogResponse>>

    @GET("catalogs/accutest")
    @Headers("Set-Token: true")
    suspend fun getAccutest(@Query("type") type: String): Response<AccutestResponse>

    @GET("catalogs/privacy")
    suspend fun getPrivacyNotice(): Response<PrivacyConsentResponse>

    @GET("catalogs/express-consent")
    suspend fun getConsentNotice(): Response<PrivacyConsentResponse>
}