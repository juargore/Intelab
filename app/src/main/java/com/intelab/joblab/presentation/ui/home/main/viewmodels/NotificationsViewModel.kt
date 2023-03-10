package com.intelab.joblab.presentation.ui.home.main.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.Notifications
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils._fourteenAsStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val candidateUseCase: CandidateUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<NotificationsState>(NotificationsState.Init)

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var notificationList by bindDelegate<List<Notifications>>(listOf())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                userFullName = getFullName(cr)
            }
        }; getNotificationsFromServer()
    }

    private fun getNotificationsFromServer() {
        viewModelScope.launch {
            candidateUseCase.getTotalNotifications(start = "", end = _fourteenAsStr)
                .onStart { state.value = NotificationsState.IsLoading(true) }
                .onCompletion { state.value = NotificationsState.IsLoading(false) }
                .collect { result ->
                    state.value = NotificationsState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = NotificationsState.OnError(result.rawResponse)
                        is BaseResult.Success -> {
                            val listOfIntegers = mutableListOf<Int>()
                            notificationList = result.data
                            notificationList.forEach { n ->
                                n.notifications.forEach {
                                    listOfIntegers.add(it.id)
                                }
                            }
                            setNotificationsAsRead(listOfIntegers)
                        }
                    }
                }
        }
    }

    fun onBackClicked() { state.value = NotificationsState.BackScreen }

    private fun setNotificationsAsRead(list: List<Int>) {
        if (list.isNotEmpty()) {
            viewModelScope.launch {
                candidateUseCase.setNotificationsAsRead(list.max()).collect { result ->
                    when (result) {
                        is BaseResult.Success -> Unit
                        is BaseResult.Error -> state.value = NotificationsState.OnError(result.rawResponse)
                    }
                }
            }
        }
    }
}

sealed class NotificationsState {
    object Init : NotificationsState()
    object BackScreen : NotificationsState()
    data class IsLoading(val isLoading: Boolean) : NotificationsState()
    data class OnError(val rawResponse: ErrorGenericResponse) : NotificationsState()
}
