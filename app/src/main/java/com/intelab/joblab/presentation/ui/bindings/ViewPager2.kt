package com.intelab.joblab.presentation.ui.bindings

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.intelab.joblab.presentation.ui.init.splash.adapter.OnBoardingAdapter

@BindingAdapter("adapter", "tabLayout")
fun ViewPager2.setUpViewPager2(onBoardingAdapter: OnBoardingAdapter, tabLayout: TabLayout) {
    adapter = onBoardingAdapter
    TabLayoutMediator(tabLayout, this) { _, _ ->

    }.attach()
}