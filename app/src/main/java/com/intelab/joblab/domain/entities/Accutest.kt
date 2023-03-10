package com.intelab.joblab.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * This is the service response of accutest result to show at home.
 */
data class AccutestResultResponse(
    val developed: ArrayList<AccutestResult> = arrayListOf(),
    val toBeDeveloped: ArrayList<AccutestResult> = arrayListOf(),
    val opportunityAreas: ArrayList<AccutestResult> = arrayListOf()
)

@Parcelize
data class AccutestResult(
    val item: String,
    val percentage: Int,
    val details: Details?
) : Parcelable

@Parcelize
data class Details(
    val description: String,
    val values: ArrayList<Values> = arrayListOf()
) : Parcelable

@Parcelize
data class Values(
    val text: String
) : Parcelable

data class AccutestTestResponse(val message: String)
