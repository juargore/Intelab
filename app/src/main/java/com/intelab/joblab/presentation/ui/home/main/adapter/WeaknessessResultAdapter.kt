package com.intelab.joblab.presentation.ui.home.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemWeaknessResultBinding
import com.intelab.joblab.domain.entities.AccutestResult

class WeaknessesResultAdapter(
    private val results: List<AccutestResult>,
    private val onClick: (View, View, View) -> Unit
) : RecyclerView.Adapter<WeaknessesResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemWeaknessResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_weakness_result,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(results[position])
    }

    override fun getItemCount() = results.size

    inner class ViewHolder(private val binding: ItemWeaknessResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(result: AccutestResult) {
            binding.setVariable(BR.item, result)
            binding.setVariable(BR.showArrow, result.details?.values?.isNotEmpty())
            binding.setVariable(BR.action, onClick)
        }
    }
}