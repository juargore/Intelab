package com.intelab.joblab.presentation.ui.bindings

import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.*
import com.intelab.joblab.presentation.extensions.runWhenReady
import com.intelab.joblab.presentation.ui.helpers.testItemTouchHelper
import com.intelab.joblab.presentation.ui.helpers.trainingTestItemTouchHelper
import com.intelab.joblab.presentation.ui.home.accutest.adapter.AccutestAdapter
import com.intelab.joblab.presentation.ui.home.accutest.adapter.TestTrainingAdapter
import com.intelab.joblab.presentation.ui.home.accutest.adapter.item.AccutestItem
import com.intelab.joblab.presentation.ui.home.main.adapter.*
import com.intelab.joblab.presentation.ui.home.register.adapter.ChoicesAdapter
import com.intelab.joblab.presentation.ui.home.register.adapter.ConfirmationAdapter
import com.intelab.joblab.presentation.ui.home.register.adapter.PreviousJobAdapter
import com.intelab.joblab.presentation.ui.home.register.adapter.items.PreviousJobItem
import com.intelab.joblab.presentation.ui.init.register.adapter.SearchPostulationAdapter
import com.intelab.joblab.presentation.ui.init.register.adapter.SelectedJobAdapter
import com.intelab.joblab.presentation.ui.init.register.adapter.items.JobItem
import com.intelab.joblab.presentation.ui.views.recyclers.HomeDocumentsRecyclerView
import com.intelab.joblab.presentation.ui.views.recyclers.HomeProfileRecyclerView
import com.intelab.joblab.presentation.ui.views.recyclers.NotificationRecyclerView


@BindingAdapter("previousJobsItems", "deleteAction", "editAction")
fun RecyclerView.setPreviousJobsAdapter(
    previousJobItems: List<PreviousJobItem>,
    deleteAction: (Int) -> Unit, editAction: (Int) -> Unit
) {
    adapter = PreviousJobAdapter(previousJobItems, deleteAction, editAction)
}

@BindingAdapter("choices")
fun RecyclerView.setChoicesAdapter(choices: List<Any>) {
    val layoutManager = FlexboxLayoutManager(context)
    layoutManager.flexDirection = FlexDirection.ROW
    layoutManager.justifyContent = JustifyContent.FLEX_START

    val adapter = ChoicesAdapter(choices)
    setLayoutManager(layoutManager)
    setAdapter(adapter)
}

@InverseBindingAdapter(attribute = "currentPosition", event = "currentPositionAttributeChanged")
fun RecyclerView.getCurrentPosition(): Int {
    return tag.toString().toInt()
}

@BindingAdapter(value = ["currentPositionAttributeChanged"])
fun RecyclerView.setListener(l: InverseBindingListener) {
    addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val childView: View? = rv.findChildViewUnder(e.x, e.y)
            childView?.let {
                val position = rv.getChildAdapterPosition(childView)
                if (tag != position) {
                    tag = position
                    l.onChange()
                }
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    })
}

@BindingAdapter("currentPosition")
fun RecyclerView.setCurrentPosition(position: Int) {
    val view = layoutManager?.findViewByPosition(position)
    if (view == null) {
        runWhenReady {
            updateRecyclerView(this, position)
        }
    } else {
        updateRecyclerView(this, position)
    }
}

fun updateRecyclerView(rv: RecyclerView, position: Int) {
    val v = rv.layoutManager?.findViewByPosition(position)
    v?.findViewById<TextView>(R.id.tv_item)?.setParallelogramBackground(R.color.green_300)
    for (i in 0 until rv.childCount) {
        val holder: RecyclerView.ViewHolder = rv.getChildViewHolder(rv.getChildAt(i))
        if (i != position) {
            holder.itemView.findViewById<TextView>(R.id.tv_item)
                .setParallelogramBackground(R.color.gray_100)
        }
    }
}

@BindingAdapter("phrases", "changeItems", "clickInstruction", requireAll = true)
fun RecyclerView.setTestPhrases(phrases: List<AccutestItem>, changeItems: () -> Unit, onClickInstruction: () -> Unit) {
    testItemTouchHelper.attachToRecyclerView(this)
    val testAdapter = AccutestAdapter(phrases.toMutableList(), changeItems, onClickInstruction)
    val mLayoutManager = GridLayoutManager(context, 3)
    mLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (testAdapter.getItemViewType(position)) {
                0 -> 3
                1 -> 1
                else -> 1
            }
        }
    }
    layoutManager = mLayoutManager
    testAdapter.differ.submitList(phrases)
    adapter = testAdapter
}

@BindingAdapter("trainingPhrases")
fun RecyclerView.setTrainingPhrases(phrases: List<AccutestItem>) {
    trainingTestItemTouchHelper.attachToRecyclerView(this)
    val trainingTestAdapter = TestTrainingAdapter(phrases.toMutableList())
    trainingTestAdapter.differ.submitList(phrases)
    adapter = trainingTestAdapter
}

@BindingAdapter("itemList", "itemSelector")
fun HomeDocumentsRecyclerView.bindingList(
    items: List<ItemHomeDocument>,
    itemSelector: HomeDocumentAdapter.ItemMenuSelector
) {
    if (adapter == null) adapter = HomeDocumentAdapter(itemSelector)
    (adapter as HomeDocumentAdapter).submitList(items)
}

@BindingAdapter("results")
fun RecyclerView.setAccutestResultAdapter(results: List<AccutestResult>) {
    adapter = AccutestResultAdapter(results)
}

@BindingAdapter("weaknesses", "onClickArrow")
fun RecyclerView.setWeaknessesResult(
    results: List<AccutestResult>,
    onClickArrow: (View, View, View) -> Unit
) {
    adapter = WeaknessesResultAdapter(results, onClickArrow)
}

@BindingAdapter("details")
fun RecyclerView.setWeaknessDetailAdapter(details: List<Values>?) {
    details?.let {
        adapter = WeaknessDetailAdapter(details)
    }
}

@BindingAdapter("itemList")
fun HomeProfileRecyclerView.bindingList(
    items: List<ItemHomeProfile>
) {
    if (adapter == null) adapter = HomeProfileAdapter()
    (adapter as HomeProfileAdapter).submitList(items)
}

@BindingAdapter("itemList")
fun NotificationRecyclerView.bindingList(
    items: List<Notifications>
) {
    if (adapter == null) adapter = NotificationsHeaderAdapter()
    (adapter as NotificationsHeaderAdapter).submitList(items)
}

@Suppress("UNUSED_PARAMETER")
@BindingAdapter("confirmationItems")
fun RecyclerView.setConfirmationListAdapter(boolean: Boolean) {
    adapter = ConfirmationAdapter(
        listOf(
            "Datos personales",
            "Domicilio",
            "Información Financiera",
            "Estilo de Vida",
            "Económico",
            "Académico",
            "Referencias Laborales",
            "Redes Sociales"
        )
    )
}

@BindingAdapter("selectedJobs", "deleteAction")
fun RecyclerView.setSelectedJobsAdapter(jobItems: List<JobUI>, deleteAction: (Int) -> Unit){
    val selectedAdapter = SelectedJobAdapter(jobItems, deleteAction)
    adapter = selectedAdapter
}

@BindingAdapter("availableJobs")
fun RecyclerView.setAvailableJobsAdapter(jobItems: List<JobItem>){
    val jobsAdapter = SearchPostulationAdapter(jobItems)
    adapter = jobsAdapter
}