package com.intelab.joblab.presentation.ui.init.register.viewmodels

import android.text.Editable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorResponse
import com.intelab.joblab.domain.entities.JobUI
import com.intelab.joblab.domain.entities.ParentJobUI
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import com.intelab.joblab.presentation.extensions.withoutMarkAccent
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.ui.init.register.adapter.items.JobHeaderItem
import com.intelab.joblab.presentation.ui.init.register.adapter.items.JobItem
import com.intelab.joblab.presentation.ui.init.register.adapter.items.JobSearchItem
import com.intelab.joblab.presentation.ui.init.register.fragments.PostulationFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostulationViewModel @Inject constructor(
    private val catalogUseCase: CatalogUseCase,
    private val databaseUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<PostulationState>(PostulationState.Init)
    private var jobs = listOf<ParentJobUI>()
    private val numJobs = NumSelectedJobs(_indexZero)
    private val searchItemList = mutableListOf<JobSearchItem>()

    @get:Bindable
    var jobItems by bindDelegate(listOf<JobItem>())

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    @get:Bindable
    var selectedItems by bindDelegate(mutableListOf<JobUI>())

    @get:Bindable
    var searchText by bindDelegate("")

    init {
        loadJobsData()
        deletePreviousJobsPostulation()
    }

    private fun loadJobsData() {
        viewModelScope.launch {
            catalogUseCase.getJobs()
                .onStart { loadingWithDelay(this@PostulationViewModel, true) }
                .collect { result ->
                    loadingWithDelay(this@PostulationViewModel, false)
                    if (result is BaseResult.Success) {
                        jobs = result.data
                        jobItems = createViewData(result.data)
                    }
                }
        }
    }

    private fun deletePreviousJobsPostulation() {
        launch(Dispatchers.IO) { databaseUseCase.deleteAllJobPostulation() }
    }

    fun onNextClicked() {
        selectedItems.forEach {
            launch(Dispatchers.IO) {
                databaseUseCase.insertJobPostulation(it.toJobPostulation())
            }
        }
        state.value = PostulationState.OpenBureauScreen(
            PostulationFragmentDirections.actionPostulationFragmentToPersonalInformationValidateFragment()
        )
    }

    fun onClearTextClicked() { searchText = "" }

    val onDeleteSelectedJob: (Int) -> Unit = { id ->
        selectedItems.removeIf { it.id == id }
        notifyPropertyChanged(BR.selectedItems)
        val deletedJob = searchItemList.find { it.jobUI.id == id }
        if (deletedJob != null) {
            deletedJob.selected = false
            searchItemList.remove(deletedJob)
            numJobs.value--
        }
        nextBnEnabled = selectedItems.isNotEmpty()
    }

    private fun createViewData(jobsUI: List<ParentJobUI>): List<JobItem> {
        val viewData = mutableListOf<JobItem>()
        numJobs.value = selectedItems.size
        searchItemList.clear()
        jobsUI.forEach { job ->
            viewData.add(JobHeaderItem(job.header))
            job.jobList.forEach { jobUi ->
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

    private fun onJobItemClicked(job: JobSearchItem) {
        if (selectedItems.find { it.id == job.jobUI.id } == null) {
            selectedItems.add(job.jobUI)
            notifyPropertyChanged(BR.selectedItems)
            nextBnEnabled = selectedItems.isNotEmpty()
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
            nextBnEnabled = selectedItems.isNotEmpty()
        }
    }

    fun afterSearchTextChanged(editable: Editable) {
        val viewData = mutableListOf<JobItem>()
        if (editable.toString().isNotEmpty()) {
            numJobs.value = selectedItems.size
            searchItemList.clear()
            jobs.forEach { parentUi ->
                val pattern = editable.toString().lowercase().toRegex()
                val headerItem = JobHeaderItem(parentUi.header)
                viewData.add(headerItem)
                var hasElements = false
                parentUi.jobList.forEach { jobUi ->
                    if (pattern.containsMatchIn(jobUi.jobName.lowercase()) || pattern.containsMatchIn(
                            jobUi.jobName.withoutMarkAccent().lowercase()
                        )
                    ) {
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
}

data class NumSelectedJobs(var value: Int)

sealed class PostulationState {
    object Init : PostulationState()
    data class IsLoading(val isLoading: Boolean) : PostulationState()
    data class ErrorJobs(val rawResponse: ErrorResponse) : PostulationState()
    data class OpenBureauScreen(val direction: NavDirections) : PostulationState()
}
