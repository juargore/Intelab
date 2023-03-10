package com.intelab.joblab.presentation.ui.home.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.AccutestResult

class AccutestResultAdapter(private val results: List<AccutestResult>) :
    RecyclerView.Adapter<AccutestResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_accutest_result,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(results[position])
    }

    override fun getItemCount(): Int = results.size

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(result: AccutestResult) {
            binding.setVariable(BR.item, result)
        }
    }
}