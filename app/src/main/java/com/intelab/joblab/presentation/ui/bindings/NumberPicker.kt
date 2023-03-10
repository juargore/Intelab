package com.intelab.joblab.presentation.ui.bindings

import android.widget.NumberPicker
import androidx.databinding.BindingAdapter

@BindingAdapter("maxValue", "minValue", "android:value", requireAll = true)
fun NumberPicker.setMaxMinActualValue(max: Int, min: Int, actualValue: Int) {
    minValue = min
    maxValue = max
    value = actualValue
}

@BindingAdapter("displayedValues", "maxValue", "minValue", "android:value", requireAll = true)
fun NumberPicker.setName(array: Array<String>, max: Int, min: Int, actualValue: Int) {
    displayedValues = null
    minValue = min
    maxValue = max
    displayedValues = array
    value = actualValue
}
