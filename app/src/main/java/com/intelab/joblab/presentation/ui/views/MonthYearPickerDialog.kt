package com.intelab.joblab.presentation.ui.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.intelab.joblab.R
import com.intelab.joblab.databinding.DialogMonthYearEndPickerBinding
import com.intelab.joblab.databinding.DialogMonthYearEndPickerProfileBinding
import com.intelab.joblab.databinding.DialogMonthYearStartPickerBinding
import com.intelab.joblab.databinding.DialogMonthYearStartPickerProfileBinding
import com.intelab.joblab.presentation.base.utils._indexZero
import com.intelab.joblab.presentation.ui.home.profile.viewmodels.ProfileAddJobReferenceViewModel
import com.intelab.joblab.presentation.ui.home.register.viewmodels.PreviousJobInformationViewModel

class MonthYearPickerDialog(private var whichDate: Int) : DialogFragment() {
    private val viewModel: PreviousJobInformationViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var binding: DialogMonthYearStartPickerBinding
    private lateinit var sBinding: DialogMonthYearEndPickerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        when (whichDate) {
            0 -> {
                binding = DialogMonthYearStartPickerBinding.inflate(requireActivity().layoutInflater)
                binding.viewModel = viewModel
            }
            1 -> {
                sBinding = DialogMonthYearEndPickerBinding.inflate(requireActivity().layoutInflater)
                sBinding.viewModel = viewModel
            }
        }
        val mView = if (whichDate == _indexZero) binding.root else sBinding.root
        return getAlertDialogDate(requireContext(), mView, viewModel, whichDate)
    }
}

class MonthYearPickerDialogProfile(private var whichDate: Int) : DialogFragment() {
    private val viewModel: ProfileAddJobReferenceViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private lateinit var binding: DialogMonthYearStartPickerProfileBinding
    private lateinit var sBinding: DialogMonthYearEndPickerProfileBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        when (whichDate) {
            0 -> {
                binding = DialogMonthYearStartPickerProfileBinding.inflate(requireActivity().layoutInflater)
                binding.viewModel = viewModel
            }
            1 -> {
                sBinding = DialogMonthYearEndPickerProfileBinding.inflate(requireActivity().layoutInflater)
                sBinding.viewModel = viewModel
            }
        }

        val mView = if (whichDate == _indexZero) binding.root else sBinding.root
        return getAlertDialogDate(requireContext(), mView, viewModel, whichDate)
    }
}

fun getAlertDialogDate(c: Context, mView: View, vm: Any, whichDate: Int): AlertDialog {
    return AlertDialog.Builder(c)
        .setView(mView)
        .setTitle(c.getString(R.string.tv_select_month_and_year))
        .setNegativeButton(c.getString(R.string.tv_cancel)) { d, _ -> d?.cancel() }
        .setPositiveButton(c.getString(R.string.bn_text_accept)) { _, _ ->
            when (vm) {
                is PreviousJobInformationViewModel -> vm.onDatePickerOkClicked(whichDate)
                is ProfileAddJobReferenceViewModel -> vm.onDatePickerOkClicked(whichDate)
                else -> Unit
            }
        }.create()
}
