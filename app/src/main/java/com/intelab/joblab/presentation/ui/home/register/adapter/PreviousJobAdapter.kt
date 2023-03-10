package com.intelab.joblab.presentation.ui.home.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem

class PreviousJobAdapter(
    val items: List<PreviousJobItem>, val deleteAction: (Int) -> Unit,
    val editAction: (Int) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_previous_job,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], deleteAction, editAction)
    }

    override fun getItemCount() = items.size
}

class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItem(item: PreviousJobItem, deleteAction: (Int) -> Unit, editAction: (Int) -> Unit) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.deleteAction, deleteAction)
        binding.setVariable(BR.editAction, editAction)
    }
}
