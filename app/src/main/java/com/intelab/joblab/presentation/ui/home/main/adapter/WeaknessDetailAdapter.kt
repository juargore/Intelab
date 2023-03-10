package com.intelab.joblab.presentation.ui.home.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.Values

class WeaknessDetailAdapter(val details: List<Values>) : RecyclerView.Adapter<WeaknessDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_weakness_detail,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(details[position])
    }

    override fun getItemCount() = details.size

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(detail: Values) {
            binding.setVariable(BR.item, detail)
        }
    }
}
