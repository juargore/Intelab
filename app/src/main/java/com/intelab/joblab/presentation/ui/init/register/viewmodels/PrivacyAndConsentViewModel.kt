package com.intelab.joblab.presentation.ui.init.register.viewmodels

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.base.utils._type
import com.intelab.joblab.presentation.extensions.loadingWithDelay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyAndConsentViewModel @Inject constructor(
    catalogUseCase: CatalogUseCase,
    savedStateHandle: SavedStateHandle
) : ObservableViewModel() {

    val state = MutableStateFlow<PrivacyAndConsentState>(PrivacyAndConsentState.Init)

    @get:Bindable
    var htmlString by bindDelegate("")

    init {
        launch {
            val type = savedStateHandle[_type] ?: _indexZero
            if (type == _indexOne) catalogUseCase.getPrivacyNotice() else catalogUseCase.getConsentNotice()
                .onStart { loadingWithDelay(this@PrivacyAndConsentViewModel, true) }
                .collect { result ->
                    loadingWithDelay(this@PrivacyAndConsentViewModel, false)
                    if (result is BaseResult.Success) {
                        htmlString = result.data.html ?: ""
                    }
                }
        }
    }
}

sealed class PrivacyAndConsentState {
    object Init : PrivacyAndConsentState()
    data class IsLoading(val isLoading: Boolean) : PrivacyAndConsentState()
}
