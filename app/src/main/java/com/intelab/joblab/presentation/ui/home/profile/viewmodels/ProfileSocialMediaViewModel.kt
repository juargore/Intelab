@file:SuppressLint("StaticFieldLeak")

package com.intelab.joblab.presentation.ui.home.profile.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ErrorGenericResponse
import com.intelab.joblab.domain.entities.SocialNetworkUI
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.extensions.SocialNetworkIds.*
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFullName
import com.intelab.joblab.presentation.base.utils._profileSocialNo
import com.intelab.joblab.presentation.extensions.getSocialNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSocialMediaViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val catalogUseCase: CatalogUseCase,
    val candidateUseCase: CandidateUseCase,
    val dbUseCase: DatabaseUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<ProfileSocialMediaState>(ProfileSocialMediaState.Init)
    var counterScreen = _profileSocialNo

    @get:Bindable
    var facebook by bindDelegate(getSocialNetwork(context, FACEBOOK))

    @get:Bindable
    var instagram by bindDelegate(getSocialNetwork(context, INSTAGRAM))

    @get:Bindable
    var twitter by bindDelegate(getSocialNetwork(context, TWITTER))

    @get:Bindable
    var linkedin by bindDelegate(getSocialNetwork(context, LINKEDIN))

    @get:Bindable
    var pinterest by bindDelegate(getSocialNetwork(context, PINTEREST))

    @get:Bindable
    var youtube by bindDelegate(getSocialNetwork(context, YOUTUBE))

    @get:Bindable
    var other by bindDelegate(getSocialNetwork(context, OTHER))

    @get:Bindable
    var userFullName by bindDelegate("")

    @get:Bindable
    var selectedNoSocialNetwork by bindDelegate(false) { _ , field ->
        if (field) clearFields()
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                userFullName = getFullName(cr)
            }
        }
    }

    fun callInitialService() {
        launch {
            catalogUseCase.getSocialNetworks().onStart {
                state.value = ProfileSocialMediaState.IsLoading(true)
            }.collect { result ->
                state.value = ProfileSocialMediaState.IsLoading(false)
                if (result is BaseResult.Success) {
                    facebook = result.data.first { it.id == FACEBOOK.value }
                    instagram = result.data.first { it.id == INSTAGRAM.value }
                    twitter = result.data.first { it.id == TWITTER.value }
                    linkedin = result.data.first { it.id == LINKEDIN.value }
                    pinterest = result.data.first { it.id == PINTEREST.value }
                    youtube = result.data.first { it.id == YOUTUBE.value }
                    getCandidateSocialNetworks()
                }
            }
        }
    }

    private fun getCandidateSocialNetworks() {
        viewModelScope.launch(Dispatchers.IO) {
            candidateUseCase.getCandidateSocialNetworks()
                .onStart {
                    state.value = ProfileSocialMediaState.IsLoading(true)
                }
                .collect { result ->
                    state.value = ProfileSocialMediaState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileSocialMediaState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> {
                            if (result.data.isEmpty()) {
                                selectedNoSocialNetwork = true
                            } else {
                                result.data.forEach { socialNetwork ->
                                    when (socialNetwork.id) {
                                        OTHER.value -> other = socialNetwork
                                        FACEBOOK.value -> facebook = socialNetwork
                                        YOUTUBE.value -> youtube = socialNetwork
                                        TWITTER.value -> twitter = socialNetwork
                                        LINKEDIN.value -> linkedin = socialNetwork
                                        INSTAGRAM.value -> instagram = socialNetwork
                                        PINTEREST.value -> pinterest = socialNetwork
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun clearFields() {
        facebook = getSocialNetwork(context, FACEBOOK)
        instagram = getSocialNetwork(context, INSTAGRAM)
        twitter = getSocialNetwork(context, TWITTER)
        linkedin = getSocialNetwork(context, LINKEDIN)
        pinterest = getSocialNetwork(context, PINTEREST)
        youtube = getSocialNetwork(context, YOUTUBE)
        other = getSocialNetwork(context, OTHER)
    }

    fun onSaveAndExitClicked(@Suppress("UNUSED_PARAMETER") v: View?) {
        // first, delete current Social Network data on Server
        viewModelScope.launch(Dispatchers.IO) {
            candidateUseCase.deleteCandidateSocialNetworks()
                .onStart { state.value = ProfileSocialMediaState.IsLoading(true) }
                .collect { result ->
                    when (result) {
                        is BaseResult.Error -> {
                            state.value = ProfileSocialMediaState.IsLoading(false)
                            state.value = ProfileSocialMediaState.ErrorStates(result.rawResponse)
                        }
                        is BaseResult.Success -> {
                            val mList = mutableListOf<SocialNetworkUI>()
                            listOf(facebook, instagram, twitter, linkedin, pinterest, youtube, other)
                                .forEach {
                                    if (it.username.isNotBlank()) { mList.add(it) }
                                }

                            // if exists at least one social media to store, call the POST request,
                            // otherwise, here finish the process since have been already deleted the data
                            if (mList.isEmpty()) {
                                state.value = ProfileSocialMediaState.IsLoading(false)
                                state.value = ProfileSocialMediaState.ExitScreen
                            } else {
                                sendNewCandidateSocialNetworks(mList)
                            }
                        }
                    }
                }
        }
    }

    private fun sendNewCandidateSocialNetworks(mList: List<SocialNetworkUI>) {
        viewModelScope.launch(Dispatchers.IO) {
            candidateUseCase.sendCandidateSocialNetworks(mList)
                .collect { result ->
                    state.value = ProfileSocialMediaState.IsLoading(false)
                    when (result) {
                        is BaseResult.Error -> state.value = ProfileSocialMediaState.ErrorStates(result.rawResponse)
                        is BaseResult.Success -> state.value = ProfileSocialMediaState.ExitScreen
                    }
                }
        }
    }
}

sealed class ProfileSocialMediaState {
    object Init : ProfileSocialMediaState()
    object ExitScreen : ProfileSocialMediaState()
    data class IsLoading(val isLoading: Boolean) : ProfileSocialMediaState()
    data class ErrorStates(val rawResponse: ErrorGenericResponse) : ProfileSocialMediaState()
}
