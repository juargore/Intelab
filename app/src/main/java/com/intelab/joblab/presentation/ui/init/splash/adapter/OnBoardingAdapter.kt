package com.intelab.joblab.presentation.ui.init.splash.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.intelab.joblab.presentation.ui.init.splash.fragment.OnBoardingFirstScreenFragment
import com.intelab.joblab.presentation.ui.init.splash.fragment.OnBoardingFourthScreenFragment
import com.intelab.joblab.presentation.ui.init.splash.fragment.OnBoardingSecondScreenFragment
import com.intelab.joblab.presentation.ui.init.splash.fragment.OnBoardingThirdScreenFragment

class OnBoardingAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    val fragments = listOf(
        OnBoardingFirstScreenFragment(),
        OnBoardingSecondScreenFragment(),
        OnBoardingThirdScreenFragment(),
        OnBoardingFourthScreenFragment()
    )

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}