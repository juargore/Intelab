package com.intelab.joblab.presentation.ui.init.register.fragments

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentPersonalInformationValidateBinding
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationValidateState
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationValidateState.*
import com.intelab.joblab.presentation.ui.init.register.viewmodels.PersonalInformationValidateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalInformationValidateFragment : Fragment(R.layout.fragment_personal_information_validate) {

    private val viewModel: PersonalInformationValidateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentPersonalInformationValidateBinding.bind(view).also { it.viewModel = viewModel }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDbRegistrationData()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: PersonalInformationValidateState) {
        when (state) {
            is Init -> Unit
            is BackPersonalInformationScreen -> navigateSafe(state.directions)
            is ErrorPersonalInfoValidation -> errorValidation(state.rawResponse)
            is IsLoading -> updateProgressDialog(state.isLoading)
            is UriLoaded -> getArrayFromUri(state.uri)?.let {
                viewModel.userPhoto = DataArray(it)
            }
            is OpenHomeScreen -> navigateToDeepLink(
                getString(state.deepLink).toUri(),
                state.popUpTo,
                state.popUpToInclusive
            )
        }
    }
}
