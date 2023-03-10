package com.intelab.joblab.presentation.ui.bindings

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.intelab.joblab.presentation.ui.home.register.adapter.CustomSpinnerAdapter
import com.intelab.joblab.presentation.ui.views.CustomSpinner

@BindingAdapter("entries", requireAll = true)
fun CustomSpinner.entries(values: List<Any>) {
    binding.spinner.adapter = CustomSpinnerAdapter(binding.spinner.context, values)
}

@BindingAdapter("selectedValue")
fun CustomSpinner.setSelectedValue(selectedValue: Any?) {
    if (binding.spinner.adapter != null) {
        val position = (binding.spinner.adapter as CustomSpinnerAdapter).getPosition(selectedValue)
        binding.spinner.setSelection(position, false)
        tag = position
    }
}

@BindingAdapter("selectedValueAttrChanged")
fun CustomSpinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
    if (inverseBindingListener == null) {
        binding.spinner.onItemSelectedListener = null
    } else {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (tag != position) {
                    inverseBindingListener.onChange()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun CustomSpinner.getSelectedValue(): Any? {
    return binding.spinner.selectedItem
}

