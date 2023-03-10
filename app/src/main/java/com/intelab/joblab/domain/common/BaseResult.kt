package com.intelab.joblab.domain.common

import com.intelab.joblab.domain.entities.ErrorGenericResponse

/**
 * Class that serves as the base for all responses from repositories.
 * [Success] returns the request response as an object (usually as list or data class).
 * [Error] returns the request error if it exists (usually as [ErrorGenericResponse], data class, or empty list)
 * */
sealed class BaseResult <out T : Any, out U : Any> {
    data class Success <T: Any>(val data : T) : BaseResult<T, Nothing>()
    data class Error <U : Any>(val rawResponse: U) : BaseResult<Nothing, U>()
}
