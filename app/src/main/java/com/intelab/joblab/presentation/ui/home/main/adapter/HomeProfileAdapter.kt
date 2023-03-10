package com.intelab.joblab.presentation.ui.home.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemHomeProfileBinding
import com.intelab.joblab.domain.entities.ItemHomeProfile
import com.intelab.joblab.domain.entities.LoadedStatus
import com.intelab.joblab.presentation.extensions.setCustomColorFilter

class HomeProfileAdapter : ListAdapter<ItemHomeProfile, HomeProfileAdapter.ProjectOverviewViewHolder>(OverviewDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectOverviewViewHolder =
        ProjectOverviewViewHolder(ItemHomeProfileBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))

    override fun onBindViewHolder(holder: ProjectOverviewViewHolder, position: Int) {
        getItem(position)?.also { item ->
            holder.run {
                val context = binding.layParent.context
                binding.txtDescription.text = item.description
                when (item.status) {
                    LoadedStatus.COMPLETED -> {
                        binding.imgBadge.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check, null))
                        binding.imgBadge.setCustomColorFilter(R.color.green_800)
                    }
                    LoadedStatus.PENDING -> {
                        binding.imgBadge.setImageDrawable(context.resources.getDrawable(R.drawable.ic_alert, null))
                        binding.imgBadge.setCustomColorFilter(R.color.orange_100)
                    }
                    LoadedStatus.TO_EXPIRE, LoadedStatus.EXPIRED -> {
                        binding.imgBadge.setImageDrawable(context.resources.getDrawable(R.drawable.ic_alert, null))
                        binding.imgBadge.setCustomColorFilter(R.color.red_600)
                    }
                }
            }
        }
    }

    class ProjectOverviewViewHolder(val binding: ItemHomeProfileBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val OverviewDiff = object : DiffUtil.ItemCallback<ItemHomeProfile>() {
            override fun areItemsTheSame(oldItem: ItemHomeProfile, newItem: ItemHomeProfile) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ItemHomeProfile, newItem: ItemHomeProfile) = oldItem == newItem
        }
    }
}
