@file:SuppressLint("StaticFieldLeak")

package com.intelab.joblab.presentation.ui.home.register.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.IdRes
import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.BaseResult
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.usecases.catalog.CatalogUseCase
import com.intelab.joblab.domain.usecases.database.DatabaseUseCase
import com.intelab.joblab.presentation.base.ObservableViewModel
import com.intelab.joblab.presentation.base.bindDelegate
import com.intelab.joblab.presentation.base.utils._confirm
import com.intelab.joblab.presentation.base.utils._indexEight
import com.intelab.joblab.presentation.base.utils._indexEleven
import com.intelab.joblab.presentation.base.utils._indexNine
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.extensions.SocialNetworkIds
import com.intelab.joblab.presentation.extensions.getSocialNetwork
import com.intelab.joblab.presentation.ui.home.register.fragment.SocialMediaFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SocialMediaViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val dbUseCase: DatabaseUseCase,
    val catalogUseCase: CatalogUseCase
) : ObservableViewModel() {

    val state = MutableStateFlow<SocialMediaState>(SocialMediaState.Init)
    var screen = _indexEleven
    var screenName = _confirm
    val advance = _indexEight
    var step = _indexNine

    var facebook = getSocialNetwork(context, SocialNetworkIds.FACEBOOK)
    var instagram = getSocialNetwork(context, SocialNetworkIds.INSTAGRAM)
    var twitter = getSocialNetwork(context, SocialNetworkIds.TWITTER)
    var linkedin = getSocialNetwork(context, SocialNetworkIds.LINKEDIN)
    var pinterest = getSocialNetwork(context, SocialNetworkIds.PINTEREST)
    var youtube = getSocialNetwork(context, SocialNetworkIds.YOUTUBE)
    var other = getSocialNetwork(context, SocialNetworkIds.OTHER)

    @get:Bindable
    var selectedNoSocialNetwork by bindDelegate(false) { _, field ->
        if (field) clearFields()
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var facebookUsername by bindDelegate("") { _, field ->
        facebook.username = field
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var instagramUsername by bindDelegate("") { _, field ->
        instagram.username = field
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var twitterUsername by bindDelegate("") { _, field ->
        twitter.username = field
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var linkedinUsername by bindDelegate("") { _, field ->
        linkedin.username = field
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var pinterestUsername by bindDelegate("") { _, field ->
        pinterest.username = field
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var youtubeUsername by bindDelegate("") { _, field ->
        youtube.username = field
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var otherUsername by bindDelegate("") { _, field ->
        other.username = field
        nextBnEnabled = isNextBnEnabled()
    }

    @get:Bindable
    var nextBnEnabled by bindDelegate(false)

    fun getSocialNetworksFromServer() {
        launch {
            catalogUseCase.getSocialNetworks()
                .onStart { state.value = SocialMediaState.IsLoading(true) }
                .collect { result ->
                    state.value = SocialMediaState.IsLoading(false)
                    if (result is BaseResult.Success) {
                        facebook = result.data.first { it.id == SocialNetworkIds.FACEBOOK.value }
                        instagram = result.data.first { it.id == SocialNetworkIds.INSTAGRAM.value }
                        twitter = result.data.first { it.id == SocialNetworkIds.TWITTER.value }
                        linkedin = result.data.first { it.id == SocialNetworkIds.LINKEDIN.value }
                        pinterest = result.data.first { it.id == SocialNetworkIds.PINTEREST.value }
                        youtube = result.data.first { it.id == SocialNetworkIds.YOUTUBE.value }
                        loadDataFromDb()
                    }
            }
        }
    }

    fun loadDataFromDb() {
        launch(Dispatchers.IO) {
            dbUseCase.getRegistrationData().collect { cr ->
                cr.hasSocialMedia?.let { selectedNoSocialNetwork = it }
                cr.facebook?.let { if (it.id != _indexZero) facebook = it }
                cr.instagram?.let { if (it.id != _indexZero) instagram = it }
                cr.twitter?.let { if (it.id != _indexZero) twitter = it }
                cr.linkedin?.let { if (it.id != _indexZero) linkedin = it }
                cr.pinterest?.let { if (it.id != _indexZero) pinterest = it }
                cr.youtube?.let { if (it.id != _indexZero) youtube = it }
                cr.other?.let { other = it }
            }
        }
    }

    fun onNextClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUseCase.insertOrUpdateRegistrationData(
                ComplementaryRegister(
                    hasSocialMedia = selectedNoSocialNetwork,
                    facebook = facebook,
                    instagram = instagram,
                    twitter = twitter,
                    linkedin = linkedin,
                    pinterest = pinterest,
                    youtube = youtube,
                    other = other,
                    screen = screen,
                    screenName = screenName,
                    step = step
                ), SocialMediaViewModel::class.simpleName
            )
            withContext(Dispatchers.Main) {
                state.value = SocialMediaState.OpenConfirmationScreen(
                    SocialMediaFragmentDirections.actionSocialMediaFragmentToRegisterConfirmationFragment()
                )
            }
        }
    }

    fun onBackClicked() {
        state.value = SocialMediaState.BackJobReferencesScreen(R.id.jobReferencesFragment,
            SocialMediaFragmentDirections.actionSocialMediaFragmentToJobReferencesFragment()
        )
    }

    private fun clearFields() {
        facebookUsername = ""
        instagramUsername = ""
        twitterUsername = ""
        linkedinUsername = ""
        pinterestUsername = ""
        youtubeUsername = ""
        otherUsername = ""
    }

    private fun isNextBnEnabled(): Boolean {
        return selectedNoSocialNetwork || facebookUsername.isNotEmpty()
                || instagramUsername.isNotEmpty() || twitterUsername.isNotEmpty()
                || linkedinUsername.isNotEmpty() || pinterestUsername.isNotEmpty()
                || youtubeUsername.isNotEmpty() || otherUsername.isNotEmpty()
    }
}

sealed class SocialMediaState {
    object Init : SocialMediaState()
    data class IsLoading(val isLoading: Boolean) : SocialMediaState()
    data class OpenConfirmationScreen(val direction: NavDirections) : SocialMediaState()
    data class BackJobReferencesScreen(@IdRes val id: Int, val directions: NavDirections) : SocialMediaState()
}
