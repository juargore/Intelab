package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import android.text.Editable
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.JobUI
import com.intelab.joblab.domain.entities.ParentJobUI
import com.intelab.joblab.domain.entities.requests.PreferableJobs
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.withoutMarkAccent
import com.intelab.joblab.presentation.ui.home.profile.fragment.ProfileJobsFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._profileJobsNo
import com.intelab.joblab.presentation.ui.init.register.adapter.items.JobHeaderItem
import com.intelab.joblab.presentation.ui.init.register.adapter.items.JobItem
import com.intelab.joblab.presentation.ui.init.register.adapter.items.JobSearchItem
import com.intelab.joblab.presentation.ui.init.register.viewmodels.NumSelectedJobs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileJobsViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileJobsState>(ProfileJobsState.Init)
    private val searchItemList = mutableListOf<JobSearchItem>()
    private val numJobs = NumSelectedJobs(_indexZero)
    private var jobs = listOf<ParentJobUI>()
    var counterScreen = _profileJobsNo

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var jobItems by bindDelegate(listOf<JobItem>())

    @get:Bindable
    var selectedItems by bindDelegate(mutableListOf<JobUI>())

    @get:Bindable
    var searchText by bindDelegate("")

    init {
        getProfileInfoFromServer()
    }

    private fun getProfileInfoFromServer() {
        launch {
            state.value = ProfileJobsState.IsLoading(true)
            val personal = async {
                candidateUseCase.getProfileInformation().collect { result ->
                    when (result) {
                        is BaseResult.Success -> userFullName = getFullName(result.data)
                        is BaseResult.Error -> state.value = ProfileJobsState.ErrorPreferableJobs(result.rawResponse)
                    }
                }
            }

            val selected = async {
                candidateUseCase.getPreferableJobs().collect { result ->
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileJobsState.ErrorPreferableJobs(result.rawResponse)
                        is BaseResult.Success -> {
                            result.data.forEach { job ->
                                selectedItems.add(
                                    JobUI(job.id, job.description, true)
                                )
                            }
                            numJobs.value = selectedItems.size
                            notifyPropertyChanged(BR.selectedItems)
                        }
                    }
                }
            }

            selected.await()
            val jobs = async { loadJobsData() }
            personal.await()
            jobs.await()
            state.value = ProfileJobsState.IsLoading(false)
        }
    }

    private suspend fun loadJobsData() {
        catalogUseCase.getJobs().collect { result ->
            if (result is BaseResult.Success) {
                jobs = result.data
                jobItems = createViewData(jobs)
            }
        }
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
            job.selected = true
            searchItemList.add(job)
            numJobs.value++
        } else {
            if (selectedItems.size == _indexOne) {
                showKeepOneApplicationMessage()
                return
            }
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

    private fun showKeepOneApplicationMessage() {
        state.value = ProfileJobsState.OpenDialog(
            R.string.dialog_title_profile_postulation,
            R.string.dialog_profile_postulation_keep_one_postulation
        )
    }

    private fun sendJobPreferencesUpdate(onSuccess: () -> Unit) {
        launch {
            candidateUseCase.updatePreferableJobs(selectedItems.map { PreferableJobs(id = it.id.toString()) })
                .onStart { state.value = ProfileJobsState.IsLoading(true) }
                .collect { result ->
                    state.value = ProfileJobsState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileJobsState.ErrorPreferableJobs(result.rawResponse)
                        is BaseResult.Success -> onSuccess()
                    }
                }
        }
    }

    fun onSaveAndExitClicked() {
        sendJobPreferencesUpdate { state.value = ProfileJobsState.BackHomeScreen }
    }

    fun onNextClicked() {
        sendJobPreferencesUpdate {
            val directions = ProfileJobsFragmentDirections.actionProfileJobsToProfilePersonal()
            state.value = ProfileJobsState.OpenPersonalProfileScreen(directions)
        }
    }

    val onDeleteSelectedJob = fun(id: Int) {
        if (selectedItems.size == _indexOne) {
            showKeepOneApplicationMessage()
            return
        }
        selectedItems.removeIf { it.id == id }
        notifyPropertyChanged(BR.selectedItems)
        val deletedJob = searchItemList.find { it.jobUI.id == id }
        if (deletedJob != null) {
            numJobs.value--
            deletedJob.selected = false
            searchItemList.remove(deletedJob)
        }
    }

    fun onClearTextClicked() { searchText = "" }
}

sealed class ProfileJobsState {
    object Init : ProfileJobsState()
    object BackHomeScreen : ProfileJobsState()
    data class IsLoading(val isLoading: Boolean) : ProfileJobsState()
    data class OpenDialog(val title: Int, val description: Int) : ProfileJobsState()
    data class OpenPersonalProfileScreen(val direction: NavDirections) : ProfileJobsState()
    data class ErrorPreferableJobs(val rawResponse: ErrorGenericResponse) : ProfileJobsState()
}
