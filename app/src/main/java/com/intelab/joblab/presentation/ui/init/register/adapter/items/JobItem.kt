package com.intelab.joblab.presentation.ui.init.register.adapter.items

import androidx.annotation.LayoutRes

interface JobItem {
    @get:LayoutRes
    val layoutId: Int
    val viewType: Int
        get() = 0
}