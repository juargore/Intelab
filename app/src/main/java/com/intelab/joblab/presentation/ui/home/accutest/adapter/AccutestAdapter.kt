package com.intelab.joblab.presentation.ui.home.accutest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.BR
import com.intelab.joblab.R
import com.intelab.joblab.databinding.ItemHeaderTestBinding
import com.intelab.joblab.databinding.ItemTestCardBinding
import com.intelab.joblab.presentation.ui.home.accutest.adapter.item.AccutestItem
import com.intelab.joblab.presentation.base.utils._indexOne
import com.intelab.joblab.presentation.base.utils._indexZero

class AccutestAdapter(
    var phrases: MutableList<AccutestItem>,
    val changeItems: () -> Unit,
    val onClickInstructions: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var currentMovements = 0
    var fromPosition: Int = 0
    var toPosition: Int = 0

    private val differCallBack = object : DiffUtil.ItemCallback<AccutestItem>() {
        override fun areItemsTheSame(oldItem: AccutestItem, newItem: AccutestItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AccutestItem, newItem: AccutestItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (getItemViewType(viewType) == _indexZero) {
            HeaderViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_header_test,
                parent,
                false
            ))
        } else {
            TestViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_test_card, parent, false
            ))
        }
    }

    private var onItemClickListener: ((AccutestItem) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == _indexOne) {
            val holdView = holder as TestViewHolder
            val movieItem = differ.currentList[position - 1]
            holdView.bindView(movieItem)
        } else {
            val holdView = holder as HeaderViewHolder
            if (differ.currentList.isNotEmpty()) {
                differ.currentList[0].step?.let {
                    holdView.bindView(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) _indexZero else _indexOne
    }

    @Suppress("unused")
    fun setOnItemClickListener(listener: (AccutestItem) -> Unit) {
        onItemClickListener = listener
    }

    fun updatePosition(fromPosition: Int, toPosition: Int) {
        val item = phrases.removeAt(fromPosition - 1)
        phrases.add(toPosition - 1, item)
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

    inner class HeaderViewHolder(val binding: ItemHeaderTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(stringId: Int) {
            binding.setVariable(BR.action, changeItems)
            binding.setVariable(BR.title, stringId)
            binding.setVariable(BR.seeInstruction, onClickInstructions)
        }
    }
}
