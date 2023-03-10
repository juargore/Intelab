package com.intelab.joblab.domain.entities

import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase

/**
 * Used to represent the UI on the Home section 'Sube tus documentos'
 * [icon] + badge + [description]
 */
data class ItemHomeDocument(
    val id: Int,
    val icon: Int?,
    val type: CandidateUseCase.FilesExpected,
    val status: LoadedStatus,
    val description: String
)

/**
 * Used to represent the UI on the Home section 'Completa tu perfil'
 * icon + [description]
 */
data class ItemHomeProfile(
    val id: Int,
    val type: String,
    val status: LoadedStatus,
    val description: String
)

/**
 * Represents the different status that an item can have
 */
enum class LoadedStatus {
    COMPLETED,
    PENDING,
    TO_EXPIRE,
    EXPIRED
}

data class HomeStatusResponse(
    val foreign: Boolean? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val surnamePaternal: String? = null,
    val surnameMaternal: String? = null,
    val phoneNumber: String? = null,
    val identificationCode: String? = null,
    val avatarURL: String? = null,
    val profileStatus: ProfileStatusResponse? = null
)

data class ProfileStatusResponse(
    val percentageCompleted: Int? = null,
    val items: List<ItemsStatusResponse>? = null
)

data class ItemsStatusResponse(
    val type: String,
    val description: String,
    val status: String,
    val group: String
)