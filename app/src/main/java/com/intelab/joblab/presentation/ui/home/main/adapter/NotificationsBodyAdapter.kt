package com.intelab.joblab.presentation.ui.home.main.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemNotificationsBodyBinding
import com.intelab.joblab.domain.entities.Notification
import com.intelab.joblab.presentation.extensions.rotateDown
import com.intelab.joblab.presentation.extensions.rotateUp
import com.intelab.joblab.presentation.base.utils._indexTwo

class NotificationsBodyAdapter: ListAdapter<Notification, NotificationsBodyAdapter.ProjectOverviewViewHolder>(OverviewDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectOverviewViewHolder =
        ProjectOverviewViewHolder(ItemNotificationsBodyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))

    override fun onBindViewHolder(holder: ProjectOverviewViewHolder, position: Int) {
        getItem(position)?.also { item ->
            holder.run {
                with(binding.txtTop) {
                    text = Html.fromHtml(item.complementaryText, Html.FROM_HTML_MODE_COMPACT)
                    if (item.isNew == true) {
                        binding.layParent.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_100))
                    }
                    binding.layParent.setOnClickListener {
                        if (minLines == _indexTwo) {
                            minLines = Integer.MIN_VALUE
                            maxLines = Integer.MAX_VALUE
                            binding.imgArrow.rotateUp()
                        } else {
                            minLines = _indexTwo
                            maxLines = _indexTwo
                            binding.imgArrow.rotateDown()
                        }
                    }
                }
            }
        }
    }

    class ProjectOverviewViewHolder(val binding: ItemNotificationsBodyBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val OverviewDiff = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) = oldItem == newItem
        }
    }
}
