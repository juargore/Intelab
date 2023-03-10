package com.intelab.joblab.presentation.ui.home.accutest.adapter.item

import androidx.annotation.StringRes

data class AccutestItem(
    val id: String,
    var position: Int,
    val showImage: Boolean,
    val text: String,
    val imagePath: String,
    @StringRes val step: Int? = null
)