package com.intelab.joblab.data.common.module

import com.intelab.joblab.data.apis.AuthApi
import com.intelab.joblab.data.apis.CandidateApi
import com.intelab.joblab.data.apis.CatalogsApi
import com.intelab.joblab.data.apis.NotificationsApi
import com.intelab.joblab.data.common.utils.ErrorGenerator
import com.intelab.joblab.data.database.dao.JobPostulationDao
import com.intelab.joblab.data.database.dao.JobReferenceDao
import com.intelab.joblab.data.database.dao.RegistrationDao
import com.intelab.joblab.data.database.repositories.DatabaseRepositoryImpl
import com.intelab.joblab.data.repositories.*
import com.intelab.joblab.domain.common.SharedPrefs
import com.intelab.joblab.domain.usecases.auth.IAuthRepository
import com.intelab.joblab.domain.usecases.candidate.ICandidateRepository
import com.intelab.joblab.domain.usecases.catalog.ICatalogRepository
import com.intelab.joblab.domain.usecases.database.IDatabaseRepository
import com.intelab.joblab.domain.usecases.notification.INotificationRepository
import com.intelab.joblab.domain.usecases.preferences.IPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Singleton
    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCatalogsApi(retrofit: Retrofit): CatalogsApi {
        return retrofit.create(CatalogsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCandidateApi(retrofit: Retrofit): CandidateApi {
        return retrofit.create(CandidateApi::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationApi(retrofit: Retrofit): NotificationsApi {
        return retrofit.create(NotificationsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        registrationApi: AuthApi,
        prefs: SharedPrefs,
        errorGenerator: ErrorGenerator
    ): IAuthRepository {
        return AuthRepositoryImpl(registrationApi, prefs, errorGenerator)
    }

    @Singleton
    @Provides
    fun provideCatalogRepository(
        catalogApi: CatalogsApi,
        errorGenerator: ErrorGenerator
    ): ICatalogRepository {
        return CatalogRepositoryImpl(catalogApi, errorGenerator)
    }

    @Singleton
    @Provides
    fun provideCandidateRepository(
        candidateApi: CandidateApi,
        prefs: SharedPrefs,
        errorGenerator: ErrorGenerator
    ): ICandidateRepository {
        return CandidateRepositoryImpl(candidateApi, prefs, errorGenerator)
    }

    @Singleton
    @Provides
    fun provideNotificationRepository(
        notificationsApi: NotificationsApi,
        errorGenerator: ErrorGenerator
    ): INotificationRepository {
        return NotificationsRepositoryImpl(notificationsApi, errorGenerator)
    }

    @Singleton
    @Provides
    fun provideDatabaseRepository(
        registrationDao: RegistrationDao,
        jobReferenceDao: JobReferenceDao,
        jobPostulationDao: JobPostulationDao,
        prefs: SharedPrefs
    ): IDatabaseRepository {
        return DatabaseRepositoryImpl(registrationDao, jobReferenceDao, jobPostulationDao, prefs)
    }

    @Singleton
    @Provides
    fun providePreferencesRepository(prefs: SharedPrefs): IPreferencesRepository {
        return PreferencesRepositoryImpl(prefs)
    }
}
