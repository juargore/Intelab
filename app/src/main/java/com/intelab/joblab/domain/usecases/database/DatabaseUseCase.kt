package com.intelab.joblab.domain.usecases.database

import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.JobPostulation
import com.intelab.joblab.domain.entities.JobReference
import javax.inject.Inject

class DatabaseUseCase @Inject constructor(
    private val databaseRepository: IDatabaseRepository
) {

    suspend fun insertOrUpdateRegistrationData(
        data: ComplementaryRegister,
        className: String?
    ) = databaseRepository.insertOrUpdateRegistrationData(data, className)

    suspend fun getRegistrationData() = databaseRepository.getRegistrationData()

    suspend fun deleteCurrentRegistrationData() = databaseRepository.deleteCurrentRegister()

    suspend fun insertOrUpdateJobReference(data: JobReference) = databaseRepository.insertOrUpdateJobReference(data)

    suspend fun getJobReferencesData() = databaseRepository.getJobReferences()

    suspend fun deleteJobReference(id: Int) { databaseRepository.deleteJobReference(id) }

    suspend fun getJobReference(id: Int): JobReference? = databaseRepository.getJobReference(id)

    suspend fun insertJobPostulation(data: JobPostulation) = databaseRepository.insertJobPostulation(data)

    suspend fun getAllJobPostulation() = databaseRepository.getAllJobPostulations()

    suspend fun deleteAllJobPostulation() = databaseRepository.deleteAllJobPostulation()

    suspend fun deleteAllJobReferences() = databaseRepository.deleteAllJobReferences()
}