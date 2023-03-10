package com.intelab.joblab.presentation.ui.home.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemHomeDocumentBinding
import com.intelab.joblab.domain.entities.ItemHomeDocument
import com.intelab.joblab.domain.entities.LoadedStatus
import com.intelab.joblab.domain.usecases.candidate.CandidateUseCase
import com.intelab.joblab.presentation.extensions.setCustomColorFilter

class HomeDocumentAdapter(
    private val itemMenuSelector: ItemMenuSelector
) : ListAdapter<ItemHomeDocument, HomeDocumentAdapter.ProjectOverviewViewHolder>(OverviewDiff) {

    interface ItemMenuSelector {
        fun select(itemMenu: ItemHomeDocument)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectOverviewViewHolder =
        ProjectOverviewViewHolder(
            ItemHomeDocumentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ProjectOverviewViewHolder, position: Int) {
        getItem(position)?.also { item ->
            holder.run {
                binding.txtDescription.text = item.description
                when (item.status) {
                    LoadedStatus.COMPLETED -> {
                        binding.imgBadge.setImageResource(R.drawable.ic_check)
                        binding.imgBadge.setCustomColorFilter(R.color.green_800)
                        binding.background.setImageResource(
                            when (item.type) {
                                CandidateUseCase.FilesExpected.ACADEMIC_DEGREE -> R.mipmap.img_academic_title_green
                                CandidateUseCase.FilesExpected.ID_OFICIAL -> R.mipmap.img_official_id_green
                                CandidateUseCase.FilesExpected.PROOF_OF_RESIDENCE -> R.mipmap.img_domicilie_green
                                else -> R.mipmap.img_cv_green
                            }
                        )
                    }
                    LoadedStatus.PENDING -> {
                        binding.imgBadge.setImageResource(R.drawable.ic_alert)
                        binding.imgBadge.setCustomColorFilter(R.color.orange_100)
                        setGrayIcons(binding.background, item.type)
                    }
                    LoadedStatus.TO_EXPIRE, LoadedStatus.EXPIRED -> {
                        binding.imgBadge.setImageResource(R.drawable.ic_alert)
                        binding.imgBadge.setCustomColorFilter(R.color.red_600)
                        setGrayIcons(binding.background, item.type)
                    }
                }
                binding.layParent.setOnClickListener {
                    itemMenuSelector.select(item)
                }
            }
        }
    }

    private fun setGrayIcons(background: ImageView, type: CandidateUseCase.FilesExpected) {
        background.setImageResource(
            when (type) {
                CandidateUseCase.FilesExpected.ACADEMIC_DEGREE -> R.mipmap.img_academic_title_gray
                CandidateUseCase.FilesExpected.ID_OFICIAL -> R.mipmap.img_official_id_gray
                CandidateUseCase.FilesExpected.PROOF_OF_RESIDENCE -> R.mipmap.img_domicilie_gray
                else -> R.mipmap.img_cv_gray
            }
        )
    }

    class ProjectOverviewViewHolder(val binding: ItemHomeDocumentBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val OverviewDiff = object : DiffUtil.ItemCallback<ItemHomeDocument>() {
            override fun areItemsTheSame(oldItem: ItemHomeDocument, newItem: ItemHomeDocument) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ItemHomeDocument, newItem: ItemHomeDocument) = oldItem == newItem
        }
    }
}
