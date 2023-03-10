package com.intelab.joblab.presentation.extensions

import android.content.res.ColorStateList
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.intelab.joblab.R
import com.intelab.joblab.presentation.base.utils._rotationDegrees

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.setBackgroundTint(color: Int) {
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this.context, color))
}

fun ImageView.setCustomColorFilter(color: Int) {
    setColorFilter(ContextCompat.getColor(this.context, color))
}

fun View.rotateUp() {
    val deg: Float = this.rotation + _rotationDegrees
    this.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
}

fun View.rotateDown() {
    val deg: Float = this.rotation - _rotationDegrees
    this.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
}

fun TabLayout.addCustomListener(viewPager: ViewPager) {
    addOnTabSelectedListener(object :
        TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
        override fun onTabSelected(tab: TabLayout.Tab) {
            super.onTabSelected(tab)
            tab.customView?.findViewById<ImageView>(R.id.nav_icon)
                ?.setCustomColorFilter(R.color.black_800)
            tab.customView?.findViewById<ConstraintLayout>(R.id.nav_background)
                ?.setBackgroundTint(R.color.green_300)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            super.onTabUnselected(tab)
            tab?.customView?.findViewById<ImageView>(R.id.nav_icon)
                ?.setCustomColorFilter(R.color.white)
            tab?.customView?.findViewById<ConstraintLayout>(R.id.nav_background)
                ?.setBackgroundTint(R.color.gray_300)
        }
    })
}
