package com.intelab.joblab.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.intelab.joblab.domain.entities.JobReference

@Entity(tableName = "JobReference")
class JobReferenceEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val useremail: String,
    val companyName: String,
    val position: String,
    val startDate: String,
    val endDate: String,
    val bossName: String,
    val contactEmail: String,
    val contactPhone: String,
    val current: Boolean
) {
    fun toJobReference() = JobReference(
        useremail = useremail,
        companyName = companyName,
        position = position,
        startDate = startDate,
        endDate = endDate,
        bossName = bossName,
        contactEmail = contactEmail,
        contactPhone = contactPhone,
        current = current,
        dbId = id
    )
}