package com.intelab.joblab.presentation.ui.home.accutest.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ObservableViewModel() {

    val state = MutableStateFlow<VideoState>(VideoState.Init)

    @get:Bindable
    var videoId by bindDelegate(savedStateHandle["videoId"] ?: 0)

    fun onCloseClicked() {
        state.value = VideoState.BackPreviousScreen
    }
}

sealed class VideoState {
    object Init : VideoState()
    object BackPreviousScreen : VideoState()
}