@file:Suppress("DEPRECATION")

package com.intelab.joblab.presentation.ui.home.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class HomeViewPagerAdapter(supportFragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(supportFragmentManager) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String? = null) {
        mFragmentList.add(fragment)
        title?.let { mFragmentTitleList.add(it) }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (mFragmentTitleList.isNotEmpty()) {
            mFragmentTitleList[position]
        } else ""
    }
}