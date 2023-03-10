package com.intelab.joblab.domain.entities

data class ErrorResponse(
    val error: String
)

data class ErrorGenericResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val messageKey: String,
    val message: String,
    val path: String? = null,
    val details: List<Detail>? = null
)

data class Detail(
    val key: String,
    val description: String
)