package com.intelab.joblab.presentation.ui.home.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.databinding.ItemNotificationsHeaderBinding
import com.intelab.joblab.domain.entities.Notifications

class NotificationsHeaderAdapter: ListAdapter<Notifications, NotificationsHeaderAdapter.ProjectOverviewViewHolder>(OverviewDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectOverviewViewHolder =
        ProjectOverviewViewHolder(ItemNotificationsHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))

    override fun onBindViewHolder(holder: ProjectOverviewViewHolder, position: Int) {
        getItem(position)?.also { item ->
            holder.run {
                binding.txtHeader.text = item.header
                with(NotificationsBodyAdapter()) {
                    binding.rvNotificationBody.adapter = this
                    this.submitList(item.notifications)
                }
            }
        }
    }

    class ProjectOverviewViewHolder(val binding: ItemNotificationsHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val OverviewDiff = object : DiffUtil.ItemCallback<Notifications>() {
            override fun areItemsTheSame(oldItem: Notifications, newItem: Notifications) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Notifications, newItem: Notifications) = oldItem == newItem
        }
    }
}
