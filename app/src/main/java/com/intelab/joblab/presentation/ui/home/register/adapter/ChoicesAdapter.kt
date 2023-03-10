package com.intelab.joblab.presentation.ui.home.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemChoiceBinding
import com.intelab.joblab.domain.entities.GenderUI
import com.intelab.joblab.domain.entities.HousingTypeUI
import com.intelab.joblab.domain.entities.MaritalUI
import com.intelab.joblab.domain.entities.TransportationMeanUI

class ChoicesAdapter(val items: List<Any>) : RecyclerView.Adapter<ChoicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemChoiceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_choice,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val binding: ItemChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(item: Any) {
            when (item) {
                is GenderUI -> binding.setVariable(BR.item, item.genderName)
                is MaritalUI -> binding.setVariable(BR.item, item.maritalName)
                is HousingTypeUI -> binding.setVariable(BR.item, item.housingTypeName)
                is TransportationMeanUI -> binding.setVariable(BR.item, item.transportationMeanName)
            }
            binding.executePendingBindings()
        }
    }
}

