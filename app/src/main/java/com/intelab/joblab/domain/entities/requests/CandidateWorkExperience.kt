package com.intelab.joblab.domain.entities.requests

import java.io.Serializable

data class CandidateWorkExperience(
    val socialIdentificationCode: String? = "",
    val workExperiences: MutableList<WorkExperiences> = mutableListOf(),
) : Serializable
