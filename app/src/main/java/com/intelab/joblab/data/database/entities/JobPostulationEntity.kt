package com.intelab.joblab.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.intelab.joblab.domain.entities.JobPostulation

@Entity(tableName = "JobPostulation")
class JobPostulationEntity(
    @PrimaryKey
    val id: Int,
    val useremail: String,
    val description: String
) {
    fun toJobPostulation() = JobPostulation(
        id = id,
        description = description
    )
}