package com.intelab.joblab.presentation.ui.home.accutest.viewmodels

import android.content.Context
import android.media.Image
import androidx.camera.core.ImageProxy
import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDirections
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.AccutestItemResponse
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils.*
import com.intelab.joblab.presentation.ui.helpers.convertBitmapToFile
import com.intelab.joblab.presentation.ui.helpers.flipBitmap
import com.intelab.joblab.presentation.ui.helpers.imageToBitmap
import com.intelab.joblab.presentation.ui.helpers.toMultiPartFile
import com.intelab.joblab.presentation.ui.home.accutest.adapter.item.AccutestItem
import com.intelab.joblab.presentation.ui.home.accutest.fragment.AccutestStepTwoFragmentDirections
import com.intelab.joblab.presentation.base.utils.FileNames
import com.intelab.joblab.presentation.base.utils._accutestStepTwo
import com.intelab.joblab.presentation.base.utils._answers
import com.intelab.joblab.presentation.base.utils._imageUpper
import com.intelab.joblab.presentation.base.utils._maxFileSize
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AccutestStepTwoViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val savedStateHandle: SavedStateHandle,
    val candidateUseCase: CandidateUseCase,
    val authUseCase: AuthUseCase,
    val preferencesUseCase: PreferencesUseCase,
) : ObservableViewModel() {

    val state = MutableStateFlow<AccutestStepTwoState>(AccutestStepTwoState.Init)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var showText = false
    var answers = ""
    var originalList = listOf<String>()
    var firstPhoto = true
    var isLoadingSecondTest = true

    @get:Bindable
    var phrases by bindDelegate<List<AccutestItem>>(listOf())

    init {
        launch {
            catalogUseCase.getAccutest(_accutestStepTwo)
                .onStart { state.value = AccutestStepTwoState.IsLoading(true) }
                .collect { result ->
                    state.value = AccutestStepTwoState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = AccutestStepTwoState.ErrorAccutestStepTwo(result.rawResponse)
                        is BaseResult.Success -> {
                            showText = !result.data.defaultValue.equals(_imageUpper, ignoreCase = true)
                            phrases = getPhrases(result.data.items)
                            originalList = phrases.map { it.id }
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
                imagePath = it.imagePath,
                step = R.string.tv_title_test_part_two
            )
        }

    fun onChangeClicked() { state.value = AccutestStepTwoState.GetAccutestPositions }

    fun onNextClicked() { state.value = AccutestStepTwoState.TakeCandidatePhoto }

    fun onClickOnInstructions() {
        state.value = AccutestStepTwoState.OpenVideoScreen(
            AccutestStepTwoFragmentDirections.actionAccutestStepTwoFragmentToVideoFragment(R.raw.joblab_accutest_parte_2)
        )
    }

    fun sendAnswersToServer(context: Context, image: ImageProxy) {
        image.image?.let {
            launch {
                state.value = AccutestStepTwoState.IsLoading(true)
                coroutineScope.launch(Dispatchers.IO) { sendAccutestPhoto(context, image) }
                sendCandidateAnswers()
            }
        }
    }

    private suspend fun getCompressImage(context: Context, image: Image, rotation: Int): File {
        val asyncCompress = async(Dispatchers.Default) {
            val bitmap = TransformationUtils.rotateImage(imageToBitmap(image), rotation)
            val file = convertBitmapToFile(context, flipBitmap(bitmap))
            val compressPhoto = async {
                Compressor.compress(context, File(file.absolutePath)) { size(_maxFileSize) }
            }; compressPhoto.await()
        }
        return asyncCompress.await()
    }

    fun sendAccutestPhoto(context: Context, image: ImageProxy) {
        image.image?.let { im ->
            launch {
                val file = getCompressImage(context, im, image.imageInfo.rotationDegrees)
                image.close()
                coroutineScope.launch(Dispatchers.IO) {
                    candidateUseCase.sendAccutestPhoto(
                        toMultiPartFile(FileNames.ACCUTEST.value, file.readBytes())
                    )
                }
            }
        }
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

    fun sendCandidateAnswers() {
        launch {
            candidateUseCase.sendAccutestAnswer("${savedStateHandle.get(_answers) ?: ""}/$answers/")
                .collect { result ->
                    state.value = AccutestStepTwoState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> {
                            when (result.rawResponse.messageKey) {
                                ERROR_ACCUTEST_INELEGIBLE, ERROR_VALIDATING_ACCUTEST, ERROR_VALIDATING_ACCUTEST_REMAINING_ZERO -> state.value =
                                    AccutestStepTwoState.OpenDialogToRedirectHome(result.rawResponse)
                                ERROR_VALIDATING_ACCUTEST_REMAINING -> state.value =
                                    AccutestStepTwoState.OpenDialogToRedirectPartOne(result.rawResponse)
                                else -> state.value =
                                    AccutestStepTwoState.ErrorAccutestStepTwo(result.rawResponse)
                            }
                        }
                        is BaseResult.Success ->
                            state.value = AccutestStepTwoState.OpenAccutestSuccessDialog(result.data.message)
                    }
                }
        }
    }
}

sealed class AccutestStepTwoState {
    object Init : AccutestStepTwoState()
    data class IsLoading(val isLoading: Boolean) : AccutestStepTwoState()
    data class ErrorAccutestStepTwo(val rawResponse: ErrorGenericResponse) : AccutestStepTwoState()
    object TakeCandidatePhoto : AccutestStepTwoState()
    data class OpenHomeScreen(val directions: NavDirections) : AccutestStepTwoState()
    object GetAccutestPositions : AccutestStepTwoState()
    data class OpenVideoScreen(val directions: NavDirections) : AccutestStepTwoState()
    data class OpenDialogToRedirectPartOne(val rawResponse: ErrorGenericResponse) : AccutestStepTwoState()
    data class OpenDialogToRedirectHome(val rawResponse: ErrorGenericResponse) : AccutestStepTwoState()
    data class OpenAccutestSuccessDialog(val message: String) : AccutestStepTwoState()
}
