package com.intelab.joblab.presentation.ui.home.register.adapter.items

import java.io.Serializable

class PreviousJobItem(
    val company: String,
    val jobName: String,
    val jobStart: String,
    val jobEnd: String,
    val id: Int,
    val current: Boolean = false,
    val bossName: String = "",
    val contactEmail: String? = null,
    val contactPhone: String? = null,
) : Serializable
