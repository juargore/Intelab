package com.intelab.joblab.domain.usecases.database

import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.JobPostulation
import com.intelab.joblab.domain.entities.JobReference
import kotlinx.coroutines.flow.Flow

interface IDatabaseRepository {

    suspend fun insertOrUpdateRegistrationData(data: ComplementaryRegister, className: String?)

    suspend fun getRegistrationData(): Flow<ComplementaryRegister>

    suspend fun deleteCurrentRegister()

    suspend fun insertOrUpdateJobReference(data: JobReference)

    suspend fun getJobReferences(): Flow<List<JobReference>>

    suspend fun deleteJobReference(id: Int)

    suspend fun getJobReference(id: Int): JobReference?

    suspend fun insertJobPostulation(data: JobPostulation)

    suspend fun getAllJobPostulations(): Flow<List<JobPostulation>>

    suspend fun deleteAllJobPostulation()

    suspend fun deleteAllJobReferences()
}