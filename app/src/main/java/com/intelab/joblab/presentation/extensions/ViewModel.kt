package com.intelab.joblab.presentation.extensions

import android.content.Context
import androidx.lifecycle.ViewModel
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.SocialNetworkUI
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAcademicViewModel
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.ForgetPasswordViewModel
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.RecoverPasswordViewModel
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.VerificationCodeViewModel
import com.intelab.joblab.presentation.ui.init.login.viewmodels.LoginViewModel
import com.intelab.joblab.presentation.ui.init.register.viewmodels.*
import kotlinx.coroutines.delay
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAcademicState as ProfileAcademic
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.ForgetPasswordState as Forget
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.RecoverPasswordState as Recover
import com.intelab.joblab.presentation.ui.init.forget.viewmodels.VerificationCodeState as Verification
import com.intelab.joblab.presentation.ui.init.login.viewmodels.LoginState as Login
import com.intelab.joblab.presentation.ui.init.register.viewmodels.ActivateAccountState as Activate
import com.intelab.joblab.presentation.ui.init.register.viewmodels.CreateAccountState as Create
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationValidateState as Personal
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PostulationState as Postulation
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PrivacyAndConsentState as Privacy

suspend fun loadingWithDelay(vm: Any?, loading: Boolean, delay: Long = 100) {
    when (vm) {
        is LoginViewModel -> vm.state.value = Login.IsLoading(loading)
        is ForgetPasswordViewModel -> vm.state.value = Forget.IsLoading(loading)
        is RecoverPasswordViewModel -> vm.state.value = Recover.IsLoading(loading)
        is VerificationCodeViewModel -> vm.state.value = Verification.IsLoading(loading)
        is ActivateAccountViewModel -> vm.state.value = Activate.IsLoading(loading)
        is PrivacyAndConsentViewModel -> vm.state.value = Privacy.IsLoading(loading)
        is CreateAccountViewModel -> vm.state.value = Create.IsLoading(loading)
        is PersonalInformationValidateViewModel -> vm.state.value = Personal.IsLoading(loading)
        is PostulationViewModel -> vm.state.value = Postulation.IsLoading(loading)

        is ProfileAcademicViewModel -> vm.state.value = ProfileAcademic.IsLoading(loading)
    }
    delay(delay)
}

@Suppress("unused")
fun ViewModel.getSocialNetwork(c: Context, social: SocialNetworkIds): SocialNetworkUI {
    return when (social) {
        SocialNetworkIds.FACEBOOK -> SocialNetworkUI(
            id = SocialNetworkIds.FACEBOOK.value,
            description = c.getString(R.string.tv_const_facebook),
            username = ""
        )
        SocialNetworkIds.YOUTUBE -> SocialNetworkUI(
            id = SocialNetworkIds.YOUTUBE.value,
            description = c.getString(R.string.tv_const_youtube),
            username = ""
        )
        SocialNetworkIds.TWITTER -> SocialNetworkUI(
            id = SocialNetworkIds.TWITTER.value,
            description = c.getString(R.string.tv_const_twitter),
            username = ""
        )
        SocialNetworkIds.LINKEDIN -> SocialNetworkUI(
            id = SocialNetworkIds.LINKEDIN.value,
            description = c.getString(R.string.tv_const_linkedin),
            username = ""
        )
        SocialNetworkIds.INSTAGRAM -> SocialNetworkUI(
            id = SocialNetworkIds.INSTAGRAM.value,
            description = c.getString(R.string.tv_const_instagram),
            username = ""
        )
        SocialNetworkIds.PINTEREST -> SocialNetworkUI(
            id = SocialNetworkIds.PINTEREST.value,
            description = c.getString(R.string.tv_const_pinterest),
            username = ""
        )
        SocialNetworkIds.OTHER -> SocialNetworkUI(
            id = SocialNetworkIds.OTHER.value,
            description = c.getString(R.string.tv_const_other),
            username = ""
        )
    }
}

enum class SocialNetworkIds(val value: Int) {
    FACEBOOK(1),
    YOUTUBE(3),
    TWITTER(4),
    LINKEDIN(5),
    INSTAGRAM(6),
    PINTEREST(8),
    OTHER(0)
}
