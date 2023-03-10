package com.intelab.joblab.presentation.ui.init.register

import app.cash.turbine.test
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CameraState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    private lateinit var cameraViewModel: CameraViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Default)
        cameraViewModel = CameraViewModel()
    }

    @Test
    fun `Function validates onCameraClicked Test`() = runTest {
        cameraViewModel.state.test {
            Assert.assertEquals(awaitItem(), CameraState.Init)
            cameraViewModel.onCameraClicked()
            Assert.assertEquals(awaitItem(), CameraState.OpenCamera)
        }
    }

    @Test
    fun `Function validates onCheckClicked Test`() = runTest {
        cameraViewModel.state.test {
            Assert.assertEquals(awaitItem(), CameraState.Init)
            cameraViewModel.onCheckClicked()
            Assert.assertEquals(awaitItem(), CameraState.BackPersonalInformationScreen)
        }
    }

    @Test
    fun `Function validates onCloseClicked Test`() = runTest {
        cameraViewModel.state.test {
            Assert.assertEquals(awaitItem(), CameraState.Init)
            cameraViewModel.onCloseClicked()
            Assert.assertEquals(awaitItem(), CameraState.TakeOtherPhoto)
        }
    }
}
