package com.intelab.joblab.domain.entities

import com.intelab.joblab.data.database.entities.JobPostulationEntity

data class JobPostulation(
    val id: Int,
    val description: String
) {
    fun toJobPostulationEntity(email: String) = JobPostulationEntity(
        id = id,
        useremail = email,
        description = description
    )
}

data class PreferableJobsResponse(
    val preferableJobs: List<JobPostulation>
)
