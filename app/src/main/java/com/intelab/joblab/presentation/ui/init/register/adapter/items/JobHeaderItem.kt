package com.intelab.joblab.presentation.ui.init.register.adapter.items

import com.intelab.joblab.R
import com.intelab.joblab.presentation.base.utils._indexZero

class JobHeaderItem(val jobHeader: String) : JobItem {
    override val layoutId: Int = R.layout.item_header_postulation
    override val viewType: Int = _indexZero
}
