package com.intelab.joblab.presentation.ui.home.accutest.viewmodels

import android.content.Context
import android.media.Image
import androidx.camera.core.ImageProxy
import androidx.databinding.Bindable
import androidx.navigation.NavDirections
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.AccutestItemResponse
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.ErrorResponse
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.helpers.convertBitmapToFile
import com.intelab.joblab.presentation.ui.helpers.flipBitmap
import com.intelab.joblab.presentation.ui.helpers.imageToBitmap
import com.intelab.joblab.presentation.ui.helpers.toMultiPartFile
import com.intelab.joblab.presentation.ui.home.accutest.adapter.item.AccutestItem
import com.intelab.joblab.presentation.ui.home.accutest.fragment.AccutestStepOneFragmentDirections
import com.intelab.joblab.presentation.base.utils.FileNames
import com.intelab.joblab.presentation.base.utils._accutestStepOne
import com.intelab.joblab.presentation.base.utils._imageUpper
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AccutestStepOneViewModel @Inject constructor(
    val catalogUseCase: CatalogUseCase,
    val preferencesUseCase: PreferencesUseCase,
    val candidateUseCase: CandidateUseCase,
    val authUseCase: AuthUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<AccutestStepOneState>(AccutestStepOneState.Init)
    var originalList = listOf<String>()
    var isLoadingFirstTest = true
    var answers = ""
    private var showText = false

    @get:Bindable
    var phrases by bindDelegate<List<AccutestItem>>(listOf())

    init {
        launch {
            catalogUseCase.getAccutest(_accutestStepOne)
                .onStart { state.value = AccutestStepOneState.IsLoading(true) }
                .collect { result ->
                    state.value = AccutestStepOneState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = AccutestStepOneState.ErrorAccutestStepOne(result.rawResponse)
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
                step = R.string.tv_title_test_part_one
            )
        }

    fun onChangeClicked() { state.value = AccutestStepOneState.GetAccutestPositions }

    fun onNextClicked() { state.value = AccutestStepOneState.TakeCandidatePhoto }

    fun onClickOnInstructions() {
        state.value = AccutestStepOneState.OpenVideoScreen(
            AccutestStepOneFragmentDirections.actionAccutestStepOneFragmentToVideoFragment(R.raw.joblab_accutest_parte_1)
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

    private suspend fun getCompressImage(context: Context, image: Image, rotation: Int): File {
        val asyncCompress = async(Dispatchers.Default) {
            val bitmap = TransformationUtils.rotateImage(imageToBitmap(image), rotation)
            val file = convertBitmapToFile(context, flipBitmap(bitmap))
            val compressPhoto = async {
                Compressor.compress(context, File(file.absolutePath)) { size(3_145_728) }
            }; compressPhoto.await()
        }
        return asyncCompress.await()
    }

    fun getAccutestCompressImage(context: Context, image: ImageProxy) {
        image.image?.let { im ->
            launch {
                val file = getCompressImage(context, im, image.imageInfo.rotationDegrees)
                image.close()
                sendAccutestPhoto(file)
            }
        }
    }

    private fun sendAccutestPhoto(file: File) {
        launch(Dispatchers.IO) {
            candidateUseCase.sendAccutestPhoto(toMultiPartFile(FileNames.ACCUTEST.value, file.readBytes()))
        }; goToAccutestStepTwo()
    }

    fun goToAccutestStepTwo() {
        state.value = AccutestStepOneState.OpenStepTwoScreen(
            AccutestStepOneFragmentDirections.actionAccutestStepOneFragmentToAccutestStepTwoFragment(answers)
        )
    }
}

sealed class AccutestStepOneState {
    object Init : AccutestStepOneState()
    data class IsLoading(val isLoading: Boolean) : AccutestStepOneState()
    data class ErrorStates(val rawResponse: ErrorResponse) : AccutestStepOneState()
    data class ErrorAccutestStepOne(val rawResponse: ErrorGenericResponse) : AccutestStepOneState()
    object TakeCandidatePhoto : AccutestStepOneState()
    data class OpenStepTwoScreen(val directions: NavDirections) : AccutestStepOneState()
    object GetAccutestPositions : AccutestStepOneState()
    data class OpenVideoScreen(val directions: NavDirections) : AccutestStepOneState()
}
