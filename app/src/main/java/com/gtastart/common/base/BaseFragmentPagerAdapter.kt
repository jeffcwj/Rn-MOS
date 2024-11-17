package com.gtastart.common.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gtastart.common.base.bean.FragmentItemBean

class BaseFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    protected val fragmentList = ArrayList<FragmentItemBean>()

    override fun getCount(): Int = fragmentList.size

    override fun getItem(position: Int): Fragment = fragmentList[position].fragment

    fun addFragment(fragment: Fragment, title: String) = fragmentList.add(FragmentItemBean(fragment, title))

    override fun getPageTitle(position: Int): CharSequence? = fragmentList[position].title
}