package com.intelab.joblab.presentation.ui.home.accutest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemTestCardBinding
import com.intelab.joblab.presentation.ui.home.accutest.adapter.item.AccutestItem

class TestTrainingAdapter(var phrases: MutableList<AccutestItem>) :
    RecyclerView.Adapter<TestTrainingAdapter.TestViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<AccutestItem>() {
        override fun areItemsTheSame(oldItem: AccutestItem, newItem: AccutestItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AccutestItem, newItem: AccutestItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestTrainingAdapter.TestViewHolder {
        return TestViewHolder(DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_test_card, parent, false
        ))
    }

    private var onItemClickListener: ((AccutestItem) -> Unit)? = null

    override fun onBindViewHolder(holder: TestTrainingAdapter.TestViewHolder, position: Int) {
        val movieItem = differ.currentList[position]
        holder.bindView(movieItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @Suppress("unused")
    fun setOnItemClickListener(listener: (AccutestItem) -> Unit) {
        onItemClickListener = listener
    }

    fun updatePosition(fromPosition: Int, toPosition: Int) {
        val item = phrases.removeAt(fromPosition)
        phrases.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
        phrases = phrases.mapIndexed { i, v -> v.copy(position = i + 1) }.toMutableList()
    }

    inner class TestViewHolder(private val itemViewBinding: ItemTestCardBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {

        fun bindView(testItem: AccutestItem) {
            itemViewBinding.setVariable(BR.item, testItem)
            itemViewBinding.root.setOnClickListener {
                onItemClickListener?.let {
                    it(testItem)
                }
            }
        }
    }
}