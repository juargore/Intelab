package com.intelab.joblab.presentation.ui.home.register.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.*

class CustomSpinnerAdapter(
    context: Context,
    private val list: List<Any>
) : ArrayAdapter<Any>(context, 0, list) {

    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(p: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: layoutInflater.inflate(R.layout.item_spinner, parent, false)
        getItem(p)?.let { setItem(view, it) }
        return view
    }

    override fun getDropDownView(p: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: layoutInflater.inflate(R.layout.item_spinner_dropdown, parent, false)
        getItem(p)?.let { setItem(view, it) }
        return view
    }

    override fun getCount(): Int = list.size

    private fun setItem(view: View, mObject: Any) {
        val text = when (mObject) {
            is StateUI -> mObject.stateName
            is SpinnerItemUI -> mObject.text
            is EducationLvlUI -> mObject.educationLvlName
            is EducationStatusUI -> mObject.educationStatusName
            is MaritalUI -> mObject.maritalName
            else -> mObject as String
        }
        view.findViewById<TextView>(R.id.tv_name).text = text
    }
}