package com.intelab.joblab.presentation.ui.home.register.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentJobReferencesBinding
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.JobReferencesState
import com.intelab.joblab.presentation.ui.home.register.viewmodels.JobReferencesState.*
import com.intelab.joblab.presentation.ui.home.register.viewmodels.JobReferencesViewModel
import com.intelab.joblab.presentation.ui.views.TYPES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JobReferencesFragment : Fragment(R.layout.fragment_job_references) {

    private val viewModel: JobReferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentJobReferencesBinding.bind(view).also { it.viewModel = viewModel }
        setUpBackNavigation(
            R.id.academicFragment,
            JobReferencesFragmentDirections.actionJobReferencesFragmentToAcademicFragment()
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.value = Init
        viewModel.loadDataFromDb()
    }

    private fun handleStateChange(state: JobReferencesState) {
        when (state) {
            is Init -> Unit
            is OpenPreviousJobInformationScreen -> navigateSafe(state.direction)
            is OpenSocialMediaScreen -> navigateSafe(state.direction)
            is BackAcademicScreen -> navigatePreviousScreen(state.id, state.directions)
            is OpenDialog -> showJoblabDialog {
                setTypeDialog(TYPES.DOUBLE)
                title.text = requireContext().resources.getString(R.string.dialog_title_delete_job_reference)
                message.text = getString(state.stringId)
                acceptClickListener { viewModel.deleteJobReference(state.jobReferenceId) }
                cancelClickListener { }
                viewModel.state.value = Init
            }.show()
        }
    }
}
