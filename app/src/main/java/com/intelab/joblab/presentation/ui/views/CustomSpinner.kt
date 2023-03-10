package com.intelab.joblab.presentation.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.intelab.joblab.R
import com.intelab.joblab.databinding.CustomSpinnerBinding

class CustomSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val binding: CustomSpinnerBinding = DataBindingUtil.inflate(inflater, R.layout.custom_spinner, this, true)

    init {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomSpinner)
        try {
            val dropDownWidth =
                ta.getDimensionPixelSize(R.styleable.CustomSpinner_android_dropDownWidth, 0)
            if (dropDownWidth != 0) {
                binding.spinner.dropDownWidth = dropDownWidth
            }
        } finally {
            ta.recycle()
        }
    }
}