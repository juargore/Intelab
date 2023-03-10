package com.intelab.joblab.presentation.ui.home.accutest.viewmodels

import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.AccutestItemResponse
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.home.accutest.adapter.item.AccutestItem
import com.intelab.joblab.presentation.ui.home.accutest.fragment.AccutestTrainFragmentDirections
import com.intelab.joblab.presentation.base.utils._accutestTest
import com.intelab.joblab.presentation.base.utils._imageUpper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccutestTrainViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<TrainAccutestState>(TrainAccutestState.Init)
    private var showText = false

    @get:Bindable
    var phrases by bindDelegate<List<AccutestItem>>(listOf())

    init {
        launch {
            catalogUseCase.getAccutest(_accutestTest).onStart {
                state.value = TrainAccutestState.IsLoading(true)
            }.collect { result ->
                state.value = TrainAccutestState.IsLoading(false)
                when (result) {
                    is BaseResult.Error -> state.value = TrainAccutestState.ErrorStates(result.rawResponse)
                    is BaseResult.Success -> {
                        showText = !result.data.defaultValue.equals(_imageUpper, ignoreCase = true)
                        phrases = getPhrases(result.data.items)
                    }
                }
            }
        }
    }

    private fun getPhrases(items: List<AccutestItemResponse>) =
        items.map {
            AccutestItem(
                id = it.id,
                position = it.id.toInt(),
                showImage = !showText,
                text = it.phrase,
                imagePath = it.imagePath
            )
        }

    fun onChangeClicked() { state.value = TrainAccutestState.GetAccutestPositions }

    fun onInitTestClicked() {
        state.value = TrainAccutestState.OpenInstructionScreen(
            AccutestTrainFragmentDirections.actionAccutestTrainFragmentToAccutestInstructionFragment()
        )
    }

    fun onVideosClicked() {
        state.value = TrainAccutestState.OpenVideosScreen(
            AccutestTrainFragmentDirections.actionAccutestTrainFragmentToVideoFragment(R.raw.joblab_accutest_example_test)
        )
    }

    fun changeAccutestCard(items: List<AccutestItem>) {
        phrases = if (showText) {
            showText = false
            items.map { it.copy(showImage = true) }
        } else {
            showText = true
            items.map { it.copy(showImage = false) }
        }
    }
}

sealed class TrainAccutestState {
    object Init : TrainAccutestState()
    data class IsLoading(val isLoading: Boolean) : TrainAccutestState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : TrainAccutestState()
    data class OpenInstructionScreen(val direction: NavDirections) : TrainAccutestState()
    data class OpenVideosScreen(val direction: NavDirections) : TrainAccutestState()
    object GetAccutestPositions : TrainAccutestState()
}
