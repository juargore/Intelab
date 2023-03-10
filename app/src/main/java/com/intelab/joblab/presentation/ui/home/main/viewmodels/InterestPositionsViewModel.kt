@file:Suppress("UNUSED_PARAMETER")

package com.intelab.joblab.presentation.ui.home.main.viewmodels

import android.text.Editable
import android.view.View
import androidx.annotation.StringRes
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.viewModelScope
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.JobUI
import com.intelab.joblab.domain.entities.ParentJobUI
import com.intelab.joblab.domain.entities.requests.PreferableJobs
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexFour
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.extensions.withoutMarkAccent
import com.intelab.joblab.presentation.ui.init.register.adapter.items.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.NumSelectedJobs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterestPositionsViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val candidateUseCase: CandidateUseCase,
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<InterestState>(InterestState.Init)
    private val searchItemList = mutableListOf<JobSearchItem>()
    private val numJobs = NumSelectedJobs(_indexZero)
    private var jobs = listOf<ParentJobUI>()

    @get:Bindable
    var jobItems by bindDelegate<List<JobItem>>(listOf())

    @get:Bindable
    var selectedItems by bindDelegate(mutableListOf<JobUI>())

    @get:Bindable
    var searchText by bindDelegate("")

    init {
        getPositionsAndRefreshViews()
    }

    private fun getPositionsAndRefreshViews() {
        viewModelScope.launch {
            candidateUseCase.getPreferableJobs().collect { result ->
                when (result) {
                    is BaseResult.Success -> {
                        result.data.forEach {
                            selectedItems.add(JobUI(it.id, it.description, true))
                        }
                        numJobs.value = selectedItems.size
                        notifyPropertyChanged(BR.selectedItems)
                        loadJobsData()
                    }
                    is BaseResult.Error -> state.value = InterestState.ErrorPositions(result.rawResponse)
                }
            }
        }
    }

    private fun loadJobsData() {
        viewModelScope.launch {
            catalogUseCase.getJobs()
                .onStart { state.value = InterestState.IsLoading(true) }
                .collect { result ->
                    state.value = InterestState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> Unit
                        is BaseResult.Success -> {
                            jobs = result.data
                            jobItems = createViewData(jobs)
                        }
                    }
                }
        }
    }

    private fun createViewData(jobsUI: List<ParentJobUI>): List<JobItem> {
        val viewData = mutableListOf<JobItem>()
        numJobs.value = selectedItems.size
        searchItemList.clear()
        jobsUI.forEach { parent ->
            viewData.add(JobHeaderItem(parent.header))
            parent.jobList.forEach { jobUi ->
                val item = JobSearchItem(
                    jobUi,
                    ::onJobItemClicked,
                    numJobs,
                    marked = selectedItems.find { it.id == jobUi.id } != null,
                    ableToSelect = true
                )
                viewData.add(item)
                if (item.marked) { searchItemList.add(item) }
            }
        }
        return viewData
    }

    fun onClearTextClicked() { searchText = "" }

    fun onCancelClicked(v: View?) { state.value = InterestState.CloseScreen }

    fun onSaveClicked(v: View?) {
        if (selectedItems.size > _indexZero) {
            if (selectedItems.size < _indexFour) {
                // first, delete any preferable job on Server
                viewModelScope.launch(Dispatchers.IO) {
                    state.value = InterestState.IsLoading(true)
                    candidateUseCase.deletePreferableJobs().collect { result ->
                        when (result) {
                            is BaseResult.Error -> state.value = InterestState.ErrorPositions(result.rawResponse)
                            is BaseResult.Success -> sendNewPreferableJobs()
                        }
                    }
                }
            } else {
                state.value = InterestState.ErrorValidation(R.string.tv_job_validation_max)
            }
        } else {
            state.value = InterestState.ErrorValidation(R.string.tv_job_validation_min)
        }
    }

    private fun sendNewPreferableJobs() {
        // finally, send new list of preferable jobs to Server
        viewModelScope.launch(Dispatchers.IO) {
            val ids = selectedItems.map { it.id.toString() }
            candidateUseCase.sendPreferableJobs(ids.map { PreferableJobs(id = it) })
                .collect { result ->
                    when (result) {
                        is BaseResult.Error -> {
                            state.value = InterestState.IsLoading(false)
                            state.value = InterestState.ErrorPositions(result.rawResponse)
                        }
                        is BaseResult.Success -> {
                            viewModelScope.launch(Dispatchers.IO) {
                                dbUseCase.deleteAllJobPostulation()
                                delay(1000L)
                                state.value = InterestState.IsLoading(false)
                                state.value = InterestState.CloseScreen
                            }
                        }
                    }
                }
        }
    }

    fun afterSearchTextChanged(editable: Editable) {
        val viewData = mutableListOf<JobItem>()
        if (editable.toString().isNotEmpty()) {
            numJobs.value = selectedItems.size
            searchItemList.clear()
            jobs.forEach { parentUi ->
                var hasElements = false
                val pattern = editable.toString().lowercase().toRegex()
                val headerItem = JobHeaderItem(parentUi.header)
                viewData.add(headerItem)
                parentUi.jobList.forEach { jobUi ->
                    if (pattern.containsMatchIn(jobUi.jobName.lowercase()) || pattern.containsMatchIn(
                            jobUi.jobName.withoutMarkAccent().lowercase()))
                    {
                        val item = JobSearchItem(
                            jobUi,
                            ::onJobItemClicked,
                            numJobs,
                            marked = selectedItems.find { it.id == jobUi.id } != null,
                            ableToSelect = true
                        )
                        viewData.add(item)
                        if (item.marked) { searchItemList.add(item) }
                        hasElements = true
                    }
                }
                if (!hasElements) { viewData.remove(headerItem) }
            }
            jobItems = viewData
        } else {
            jobItems = createViewData(jobs)
        }
    }

    private fun onJobItemClicked(job: JobSearchItem) {
        if (selectedItems.find { it.id == job.jobUI.id } == null) {
            selectedItems.add(job.jobUI)
            notifyPropertyChanged(BR.selectedItems)
            job.selected = true
            searchItemList.add(job)
            numJobs.value++
        } else {
            selectedItems.removeIf { it.id == job.jobUI.id }
            notifyPropertyChanged(BR.selectedItems)
            val deletedJob = searchItemList.find { it.jobUI.id == job.jobUI.id }
            if (deletedJob != null) {
                deletedJob.selected = false
                searchItemList.remove(deletedJob)
                numJobs.value--
            }
        }
    }

    val onDeleteSelectedJob: (Int) -> Unit = { id ->
        selectedItems.removeIf { it.id == id }
        notifyPropertyChanged(BR.selectedItems)
        val deletedJob = searchItemList.find { it.jobUI.id == id }
        if (deletedJob != null) {
            deletedJob.selected = false
            searchItemList.remove(deletedJob)
            numJobs.value--
        }
    }
}

sealed class InterestState {
    object Init : InterestState()
    object CloseScreen : InterestState()
    data class IsLoading(val isLoading: Boolean) : InterestState()
    data class ErrorValidation(@StringRes val message: Int) : InterestState()
    data class ErrorPositions(val rawResponse: ErrorGenericResponse) : InterestState()
}
