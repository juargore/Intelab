package com.intelab.joblab.data.apis

import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.entities.requests.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CandidateApi {

    @POST("candidates")
    @Headers("Set-Token: true")
    suspend fun registerNewCandidateOnServer(
        @Body candidateRequest: CandidateRequest
    ): Response<Unit>

    @PATCH("candidates/me/complementary")
    @Headers("Set-Token: true")
    suspend fun registerNewCandidateComplementaryOnServer(
        @Body candidateComplementaryRequest: CandidateComplementaryRequest
    ): Response<Unit>

    @POST("candidates/me/preferable-jobs")
    @Headers("Set-Token: true")
    suspend fun sendPreferableJobs(
        @Body preferableJobsRequest: PreferableJobsRequest
    ): Response<GeneralResponseOneMessage>

    @DELETE("candidates/me/preferable-jobs")
    @Headers("Set-Token: true")
    suspend fun deletePreferableJobs(): Response<Unit>

    @PUT("candidates/me/preferable-jobs")
    @Headers("Set-Token: true")
    suspend fun updatePreferableJobs(
        @Body preferableJobsRequest: PreferableJobsRequest
    ): Response<GeneralResponseOneMessage>

    @Multipart
    @POST("candidates/me/picture/upload")
    @Headers("Set-Token: true")
    suspend fun sendAccutestPhoto(
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @POST("candidates/me/accutest")
    @Headers("Set-Token: true")
    suspend fun sendAccutestAnswer(
        @Body accutestAnswerRequest: AccutestAnswerRequest
    ): Response<AccutestTestResponse>

    @GET("candidates/me")
    @Headers("Set-Token: true")
    suspend fun getHomeStatusResponseMock(): Response<HomeStatusResponse>

    @GET("candidates/me/preferable-jobs")
    @Headers("Set-Token: true")
    suspend fun getJobPostulation(): Response<PreferableJobsResponse>

    @GET("candidates/me/accutest")
    @Headers("Set-Token: true")
    suspend fun getAccutestResult(): Response<AccutestResultResponse>

    @Multipart
    @POST("candidates/me/file/{type_name}/upload")
    @Headers("Set-Token: true")
    suspend fun sendFile(
        @Path("type_name") typeName: String,
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @DELETE("candidates/me/file/{type_name}")
    @Headers("Set-Token: true")
    suspend fun deleteFileFromServer(@Path("type_name") typeName: String): Response<Unit>

    /**
     * Social Networks Requests starts here
     */
    @GET("candidates/me/social-networks")
    @Headers("Set-Token: true")
    suspend fun getSocialNetworks(): Response<SocialNetworkRequest>

    @POST("candidates/me/social-networks")
    @Headers("Set-Token: true")
    suspend fun sendSocialNetworks(
        @Body socialNetworkRequest: SocialNetworkRequest
    ): Response<GeneralResponseOneMessage>

    @DELETE("candidates/me/social-networks")
    @Headers("Set-Token: true")
    suspend fun deleteSocialNetworks(): Response<Unit>

    /**
     * Personal Information Requests starts here
     */
    @GET("candidates/me/personal")
    @Headers("Set-Token: true")
    suspend fun getCandidatePersonalInformation(): Response<PersonalInformation>

    @PUT("candidates/me/personal")
    @Headers("Set-Token: true")
    suspend fun sendPersonalInformationUpdate(
        @Body personalInformationRequest: PersonalInformationRequest
    ): Response<Unit>

    /**
     * Financials (Economic) Requests starts here
     */
    @GET("candidates/me/financial")
    @Headers("Set-Token: true")
    suspend fun getFinancialInformation(): Response<Financial>

    @PUT("candidates/me/financial")
    @Headers("Set-Token: true")
    suspend fun sendFinancialInformationUpdate(
        @Body financialRequest: Financial
    ): Response<Unit>

    /**
     * Academic Requests starts here
     */
    @GET("candidates/me/educations")
    @Headers("Set-Token: true")
    suspend fun getAcademicInformation(): Response<EducationsRequest>

    @POST("candidates/me/educations")
    @Headers("Set-Token: true")
    suspend fun registerNewAcademicInformation(
        @Body educationsRequest: EducationsRequest
    ): Response<Unit>

    @PUT("candidates/me/educations")
    @Headers("Set-Token: true")
    suspend fun sendAcademicInformationUpdate(
        @Body educationsRequest: EducationsRequest
    ): Response<Unit>

    @DELETE("candidates/me/educations/{id}")
    @Headers("Set-Token: true")
    suspend fun deleteAcademicInformation(@Path("id") id: String): Response<Unit>

    /**
     * Job References Requests starts here
     */
    @GET("candidates/me/work-experiences")
    @Headers("Set-Token: true")
    suspend fun getJobReferencesInformation(): Response<CandidateWorkExperience>

    @POST("candidates/me/work-experiences")
    @Headers("Set-Token: true")
    suspend fun addNewJobReferenceInformation(
        @Body jobReferencesRequest: CandidateWorkExperience
    ): Response<Unit>

    @PUT("candidates/me/social-identification")
    @Headers("Set-Token: true")
    suspend fun updateSocialIdentificationCode(
        @Body socialIdentificationCodeRequest: SocialIdentificationCodeRequest
    ): Response<Unit>

    @DELETE("candidates/me/work-experiences/{id}")
    @Headers("Set-Token: true")
    suspend fun deleteJobReferenceFromServer(@Path("id") id: String): Response<Unit>

    @DELETE("candidates/me/work-experiences")
    @Headers("Set-Token: true")
    suspend fun deleteAllJobReferenceFromServer(): Response<Unit>

    /**
     * Domicile Requests
     */
    @GET("candidates/me/address")
    @Headers("Set-Token: true")
    suspend fun getDomicileInformation(): Response<DomicileInformation>

    @PUT("candidates/me/address")
    @Headers("Set-Token: true")
    suspend fun sendDomicileInformationUpdate(@Body domicileRequest: DomicileInformation): Response<Unit>

    /**
     * Financial Information Requests
     */
    @GET("candidates/me/credit-bureau")
    @Headers("Set-Token: true")
    suspend fun getUserFinancialInformation(): Response<FinancialInformation>

    @PUT("candidates/me/credit-bureau")
    @Headers("Set-Token: true")
    suspend fun updateUserFinancialInformation(@Body requestFinancial: FinancialInformation): Response<Unit>

    /**
     * Life Style Requests
     */
    @GET("candidates/me/housing")
    @Headers("Set-Token: true")
    suspend fun getLifeStyleInformation(): Response<HousingInformation>

    @PUT("candidates/me/housing")
    @Headers("Set-Token: true")
    suspend fun updateLifeStyleInformation(@Body lifeStyleRequest: HousingInformation): Response<Unit>

    /**
     * Notifications Requests
     */
    @GET("candidates/me/notifications")
    @Headers("Set-Token: true")
    suspend fun getFullNotifications(
        @Query("start") start: String,
        @Query("limit") limit: String
    ): Response<NotificationsResponse>

    @GET("candidates/me/notifications/count/unseen")
    @Headers("Set-Token: true")
    suspend fun getUnseenNotifications(): Response<TotalUnseenNotificationsRequest>

    @PUT("candidates/me/notifications")
    @Headers("Set-Token: true")
    suspend fun setNotificationsAsRead(@Query("start") start: Int): Response<Unit>

    @Multipart
    @POST("candidates/me/file/AVATAR/upload")
    @Headers("Set-Token: true")
    suspend fun sendUserPhoto(
        @Part("username") username: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<PhotoResponse>
}
