package com.intelab.joblab.presentation.ui.helpers

import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.intelab.joblab.R
import com.intelab.joblab.presentation.ui.home.accutest.adapter.AccutestAdapter
import com.intelab.joblab.presentation.ui.home.accutest.adapter.TestTrainingAdapter

val simpleTestItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
    0
) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (viewHolder.itemViewType != target.itemViewType)
            return false
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        val adapter = recyclerView.adapter as AccutestAdapter
        if(adapter.fromPosition == 0)
            adapter.fromPosition = fromPosition
        adapter.toPosition = toPosition
        adapter.updatePosition(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder.adapterPosition == 0)
            return ItemTouchHelper.ACTION_STATE_IDLE

        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.65f
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
        val adapter = recyclerView.adapter as AccutestAdapter
        if (adapter.toPosition != adapter.fromPosition)
            adapter.currentMovements++
        for (i in 0 until (recyclerView.adapter?.itemCount ?: 0)) {
            val holder =
                recyclerView.findViewHolderForAdapterPosition(i) as? AccutestAdapter.TestViewHolder
            holder.apply {
                "$i".also { holder?.itemView?.findViewById<TextView>(R.id.tv_number)?.text = it }
            }
        }
        adapter.fromPosition = 0
    }
}

val testItemTouchHelper = ItemTouchHelper(simpleTestItemTouchCallback)

val simpleTrainingTestItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
    0
) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (viewHolder.itemViewType != target.itemViewType)
            return false
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        (recyclerView.adapter as TestTrainingAdapter).updatePosition(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.65f
        }
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
        for (i in 0 until (recyclerView.adapter?.itemCount ?: 0)) {
            val holder =
                recyclerView.findViewHolderForAdapterPosition(i) as? TestTrainingAdapter.TestViewHolder
            holder.apply {
                "${i + 1}".also {
                    holder?.itemView?.findViewById<TextView>(R.id.tv_number)?.text = it
                }
            }
        }
    }
}

val trainingTestItemTouchHelper = ItemTouchHelper(simpleTrainingTestItemTouchCallback)
