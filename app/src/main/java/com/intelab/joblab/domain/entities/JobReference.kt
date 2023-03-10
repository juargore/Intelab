package com.intelab.joblab.domain.entities

import com.intelab.joblab.data.database.entities.JobReferenceEntity
import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem

data class JobReference(
    val useremail: String? = null,
    val companyName: String? = null,
    val position: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val bossName: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val dbId: Int? = null,
    val current: Boolean? = null
) {
    fun toJobEntityDb(useremail: String) = JobReferenceEntity(
        useremail = useremail,
        companyName = companyName ?: "",
        position = position ?: "",
        startDate = startDate ?: "",
        endDate = endDate ?: "",
        bossName = bossName ?: "",
        contactEmail = contactEmail ?: "",
        contactPhone = contactPhone ?: "",
        id = dbId ?: 0,
        current = current ?: false
    )

    fun toPreviousJobItem() = PreviousJobItem(
        company = companyName ?: "",
        jobName = position ?: "",
        jobStart = startDate ?: "",
        jobEnd = endDate ?: "",
        id = dbId ?: 0,
        current = current ?: false,
        bossName = bossName ?: "",
        contactEmail = contactEmail,
        contactPhone = contactPhone
    )

    fun toCompaniesList() = companyName?.trim()
}
