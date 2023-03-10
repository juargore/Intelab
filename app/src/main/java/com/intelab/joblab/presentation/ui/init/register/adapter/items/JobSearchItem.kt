package com.intelab.joblab.presentation.ui.init.register.adapter.items

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.JobUI
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.ui.init.register.viewmodels.NumSelectedJobs

class JobSearchItem(
    val jobUI: JobUI,
    private val onItemClick: (JobSearchItem) -> Unit,
    private var num: NumSelectedJobs,
    val marked: Boolean,
    private val ableToSelect: Boolean
) : JobItem, BaseObservable() {

    @Bindable
    var selected = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

    init {
        if (marked) {
            selected = true
        }
    }

    override val layoutId = R.layout.item_search_job_postulation
    override val viewType = _indexOne

    fun onClick() {
        if (ableToSelect) {
            if (selected) {
                onItemClick(this)
            } else {
                if (num.value < 3) {
                    onItemClick(this)
                }
            }
        }
    }
}