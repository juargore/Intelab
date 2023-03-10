@file:Suppress("DeferredResultUnused", "MemberVisibilityCanBePrivate")

package com.intelab.joblab.presentation.ui.home.main.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.intelab.joblab.BuildConfig
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.domain.usecases.auth.AuthUseCase
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.domain.usecases.notification.NotificationUseCase
import com.intelab.joblab.domain.usecases.preferences.PreferencesUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.ui.helpers.getDeviceId
import com.intelab.joblab.presentation.ui.helpers.images.ImageGalleryOrCameraViewModel
import com.intelab.joblab.presentation.ui.helpers.toMultiPartFile
import com.intelab.joblab.presentation.ui.home.main.fragment.HomeFragmentDirections
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils.FileNames
import com.intelab.joblab.presentation.base.utils.USER_COMPLEMENTARY_REGISTER_STATE
import com.intelab.joblab.presentation.base.utils._delay50
import com.intelab.joblab.presentation.base.utils._firebase
import com.intelab.joblab.presentation.base.utils._foreign
import com.intelab.joblab.presentation.base.utils._joblabTopicName
import com.intelab.joblab.presentation.base.utils._national
import com.intelab.joblab.presentation.base.utils._percentageCompleted
import com.intelab.joblab.presentation.base.utils._zeroAsStr
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.huawei.hms.aaid.HmsInstanceId
import com.intelab.joblab.presentation.base.utils.HUAWEI

@HiltViewModel
class HomeViewModel @Inject constructor(
    val dbUseCase: DatabaseUseCase,
    val authUseCase: AuthUseCase,
    val candidateUseCase: CandidateUseCase,
    val preferencesUseCase: PreferencesUseCase,
    val notificationUseCase: NotificationUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<HomeState>(HomeState.Init)
    var complementaryRegisterFinished = true
    var newPhotoCapture = false

    @get:Bindable
    var totalNotifications by bindDelegate(_zeroAsStr)

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var url by bindDelegate("")

    @get:Bindable
    var preferableJobs by bindDelegate("")

    @get:Bindable
    var userPhoto by bindDelegate<DataArray?>(null)

    fun initialize(c: Context) {
        if (Build.BRAND.equals(HUAWEI, true)) {
            generateHuaweiTokens(c)
        } else {
            generateFirebaseTokens(c)
        }
    }

    init {
        getInitialInformation()
    }

    private fun getInitialInformation() {
        launch {
            val profile = async {
                candidateUseCase.getProfileInformation()
                    .collect { result ->
                        when (result) {
                            is BaseResult.Success -> {
                                val cr = result.data
                                url = cr.avatarURL ?: ""
                                userFullName = getFullName(result.data)
                                cr.profileStatus?.percentageCompleted?.let {
                                    if (it < _percentageCompleted) subscribeToTopic() else unsubscribeFromTopic()
                                }
                                insertRegistrationData(cr)
                            }
                            is BaseResult.Error -> state.value = HomeState.ErrorState(result.rawResponse)
                        }
                    }
            }

            val jobs = async {
                candidateUseCase.getPreferableJobs().collect { result ->
                    when (result) {
                        is BaseResult.Success -> preferableJobs = result.data.joinToString("/") { it.description }
                        is BaseResult.Error -> state.value = HomeState.ErrorState(result.rawResponse)
                    }
                }
            }

            jobs.await()
            profile.await()
            callUserStateService()
        }
    }

    private fun insertRegistrationData(cr: HomeStatusResponse) {
        val foreign = cr.foreign
        val photoUrl = cr.avatarURL
        val firstName = cr.firstName ?: ""
        val middleName = cr.middleName ?: ""
        val surnamePaternal = cr.surnamePaternal ?: ""
        val surnameMaternal = cr.surnameMaternal ?: ""

        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    photoUrl = photoUrl,
                    nationality = if (foreign == true) _foreign else _national,
                    firstName = firstName.trim(),
                    otherNames = middleName.trim(),
                    fatherLastName = surnamePaternal.trim(),
                    motherLastName = surnameMaternal.trim(),
                    phone = cr.phoneNumber?.trim(),
                    curp = cr.identificationCode?.trim(),
                    birthCountryId = _zeroAsStr
                ), PersonalInformationViewModel::class.simpleName
            )
        }
    }

    fun getTotalCounterNotifications() {
        launch {
            candidateUseCase.getCounterOfUnseenNotifications().collect { result ->
                when (result) {
                    is BaseResult.Success -> totalNotifications = result.data.toString()
                    is BaseResult.Error -> state.value = HomeState.ErrorState(result.rawResponse)
                }
            }
        }
    }

    private fun callUserStateService() {
        viewModelScope.launch(Dispatchers.IO) {
            authUseCase.checkForUserState().collect { result ->
                if (result is BaseResult.Success) {
                    if (result.data.state == USER_COMPLEMENTARY_REGISTER_STATE) {
                        // user has not finished the complementary registration yet
                        complementaryRegisterFinished = false
                    }
                }
            }
        }
    }

    fun updateUserName(name: String) { userFullName = name }

    fun onCloseMenuClicked() { state.value = HomeState.CloseSideMenu }

    fun onOpenMenuClicked() { state.value = HomeState.OpenSideMenu }

    private fun generateFirebaseTokens(context: Context) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(_firebase, "Fetching FCM token failed -> ${task.exception}")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.i(_firebase, "Generated Token: $token")

            preferencesUseCase.saveDeviceId(getDeviceId(context))
            val notificationRequest = NotificationRequest(
                model = Build.MODEL,
                os = context.getString(R.string.os_name),
                osVersion = Build.VERSION.RELEASE,
                appVersion = BuildConfig.VERSION_NAME,
                registrationToken = token,
                deviceId = getDeviceId(context),
                brand = Build.BRAND
            )
            if (preferencesUseCase.getFirebaseToken().isEmpty()) {
                preferencesUseCase.saveFirebaseToken(token)
                launch {
                    notificationUseCase.sendDeviceInfoForNotification(notificationRequest)
                }
            } else if (preferencesUseCase.getFirebaseToken() != token) {
                preferencesUseCase.saveFirebaseToken(token)
                launch {
                    notificationUseCase.updateDeviceInfoForNotification(notificationRequest)
                }
            }
        })
    }

    private fun generateHuaweiTokens(context: Context) {
        object : Thread() {
            override fun run() {
                try {
                    // Obtain the app ID from the agconnect-services.json file.
                    val appId = "107020959"

                    // Set tokenScope to HCM.
                    val tokenScope = "HCM"
                    val token = HmsInstanceId.getInstance(context).getToken(appId, tokenScope)
                    println("Huawei token:$token")
                } catch (e: ApiException) {
                    println("Huawei token failed: ${e.message}")
                }
            }
        }.start()
    }

    private fun subscribeToTopic() {
        Firebase.messaging.subscribeToTopic(_joblabTopicName)
            .addOnCompleteListener { task ->
                var msg = "Subscribed to $_joblabTopicName!"
                if (!task.isSuccessful) {
                    msg = "Subscribe to $_joblabTopicName failed!"
                }; Log.i(_firebase, msg)
            }
    }

    fun onNotificationsClicked(@Suppress("UNUSED_PARAMETER") v: View?) {
        val direction = HomeFragmentDirections.actionHomeFragmentToNotifications()
        state.value = HomeState.OpenNotificationsScreen(direction)
    }

    fun deleteAccount() {
        unsubscribeFromTopic()
        launch {
            authUseCase.deleteAccountFromServer()
                .onStart { state.value = HomeState.IsLoading(true) }
                .onCompletion { state.value = HomeState.IsLoading(false) }
                .collect { result ->
                    when (result) {
                        is BaseResult.Error -> {
                            state.value = HomeState.IsLoading(false)
                            state.value = HomeState.ErrorLogout(result.rawResponse)
                        }
                        is BaseResult.Success -> {
                            // after success deletion on Server -> delete internal db info
                            deleteInternalDbAccount()
                        }
                    }
                }
        }
    }

    private fun deleteInternalDbAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.deleteCurrentRegistrationData()
            dbUseCase.deleteAllJobReferences()
            state.value = HomeState.IsLoading(false)
            delay(_delay50)
            val directions = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            state.value = HomeState.OpenLoginScreen(directions)
            preferencesUseCase.clearSessionTokens()
        }
    }

    private fun unsubscribeFromTopic() {
        Firebase.messaging.unsubscribeFromTopic(_joblabTopicName)
            .addOnCompleteListener { task ->
                var msg = "Unsubscribed to $_joblabTopicName!"
                if (!task.isSuccessful) {
                    msg = "Unsubscribed to $_joblabTopicName failed"
                }; Log.i(_firebase, msg)
            }
    }

    fun logout() {
        unsubscribeFromTopic()
        launch {
            authUseCase.userLogout()
                .onStart { state.value = HomeState.IsLoading(true) }
                .collect { result ->
                    state.value = HomeState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = HomeState.ErrorLogout(result.rawResponse)
                        is BaseResult.Success -> {
                            val directions = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                            state.value = HomeState.OpenLoginScreen(directions)
                            preferencesUseCase.clearSessionTokens()
                        }
                    }
                }
        }
    }

    fun sendPhotoToCloud() {
        viewModelScope.launch {
            userPhoto?.value?.let {
                candidateUseCase.sendUserPhoto(toMultiPartFile(FileNames.CANDIDATE.value, it))
                    .onStart { state.value = HomeState.IsLoading(true) }
                    .collect { result ->
                        state.value = HomeState.IsLoading(false)
                        when (result) {
                            is BaseResult.Error -> state.value = HomeState.ErrorState(result.rawResponse)
                            is BaseResult.Success -> {
                                url = result.data
                                newPhotoCapture = true
                                insertOrUpdateRegistration(url)
                            }
                        }
                    }
            }
        }
    }

    private fun insertOrUpdateRegistration(url: String) {
        launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(photoUrl = url),
                ImageGalleryOrCameraViewModel::class.simpleName
            )
        }
    }

    fun deleteCloudPhoto() {
        launch {
            candidateUseCase.deleteFileFromServer(CandidateUseCase.FilesExpected.AVATAR)
                .onStart { state.value = HomeState.IsLoading(true) }
                .collect { result ->
                    state.value = HomeState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = HomeState.ErrorState(result.rawResponse)
                        is BaseResult.Success -> {
                            url = ""
                            newPhotoCapture = true
                            insertOrUpdateRegistration("")
                        }
                    }
                }
        }
    }

    fun updateSideMenu() {
        launch {
            async {
                candidateUseCase.getProfileInformation()
                    .collect { result ->
                        when (result) {
                            is BaseResult.Error -> state.value = HomeState.ErrorState(result.rawResponse)
                            is BaseResult.Success -> {
                                url = result.data.avatarURL ?: ""
                                userFullName = getFullName(result.data)
                            }
                        }
                    }
            }

            async {
                candidateUseCase.getPreferableJobs().collect { result ->
                    when (result) {
                        is BaseResult.Success -> preferableJobs = result.data.joinToString("/") { it.description }
                        is BaseResult.Error -> state.value = HomeState.ErrorState(result.rawResponse)
                    }
                }
            }
        }
    }
}

sealed class HomeState {
    object Init : HomeState()
    object CloseSideMenu : HomeState()
    object OpenSideMenu : HomeState()
    data class IsLoading(val isLoading: Boolean) : HomeState()
    data class OpenNotificationsScreen(val directions: NavDirections) : HomeState()
    data class OpenLoginScreen(val directions: NavDirections) : HomeState()
    data class ErrorLogout(val rawResponse: ErrorGenericResponse) : HomeState()
    data class ErrorState(val rawResponse: ErrorGenericResponse) : HomeState()
}
