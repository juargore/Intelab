package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentPersonalInformationPartTwoBinding
import com.intelab.joblab.presentation.extensions.flow
import com.intelab.joblab.presentation.extensions.navigateSafe
import com.intelab.joblab.presentation.extensions.simpleDialog
import com.intelab.joblab.presentation.extensions.updateProgressDialog
import com.intelab.joblab.presentation.ui.helpers.images.ImageGalleryOrCameraFragment
import com.intelab.joblab.presentation.ui.home.register.viewmodels.PersonalInformationPartTwoViewModel
import com.intelab.joblab.presentation.ui.home.register.viewmodels.PersonalPtTwoState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.PersonalPtTwoState.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalInformationPartTwoFragment : Fragment(R.layout.fragment_personal_information_part_two) {

    private val viewModel: PersonalInformationPartTwoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentPersonalInformationPartTwoBinding.bind(view).also { it.viewModel = viewModel }
        setCircularViewProgress()
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
    }

    private fun handleStateChange(state: PersonalPtTwoState) {
        when (state) {
            is Init -> Unit
            is IsLoading -> updateProgressDialog(state.isLoading)
            is OpenDomicileScreen -> navigateSafe(state.direction)
            is BackHomeScreen -> findNavController().navigateUp()
            is ErrorStates -> simpleDialog(R.string.dialog_title_error, R.string.dialog_description_error_generic_message)
        }
    }

    private fun setCircularViewProgress() {
        (childFragmentManager.findFragmentById(R.id.header_register_photo)
                as ImageGalleryOrCameraFragment).setShowStroke(false)
    }
}
