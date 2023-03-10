package com.intelab.joblab.presentation.ui.init.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemSelectedJobBinding
import com.intelab.joblab.domain.entities.JobUI

class SelectedJobAdapter(val jobItems: List<JobUI>, val deleteAction: (Int) -> Unit) :
    RecyclerView.Adapter<SelectedJobAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSelectedJobBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_selected_job,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(jobItems[position])
    }

    override fun getItemCount() = jobItems.size

    inner class ViewHolder(val binding: ItemSelectedJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(jobItem: JobUI) {
            binding.setVariable(BR.item, jobItem)
            binding.setVariable(BR.deleteAction, deleteAction)
        }
    }
}