package com.gtastart.common.base

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gtastart.common.base.bean.FragmentItemBean

abstract class ViewPagerWithBottomNavActivity<VB: ViewBinding, VP: ViewPager, BNV: BottomNavigationView> : BaseActivity<VB>() {

    protected lateinit var adapter: BaseFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewPager()
        setupBottomNavigationView()
    }

    protected fun setupViewPager() {
        adapter = BaseFragmentPagerAdapter(supportFragmentManager)
        getFragmentList().forEach { fragmentItem ->
            adapter.addFragment(fragmentItem.fragment, fragmentItem.title)
        }
        getViewPager().offscreenPageLimit = getFragmentList().size
        getViewPager().adapter = adapter

        getViewPager().addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}
            override fun onPageSelected(position: Int) {
                getBottomNavigationView().menu.getItem(position).isChecked = true
            }
            override fun onPageScrollStateChanged(state: Int) {}

        })
    }

    protected fun setupBottomNavigationView() {
        getBottomNavigationView().setOnItemSelectedListener { item ->
            whenItemSelected(item)
        }
    }

    abstract fun whenItemSelected(item: MenuItem) : Boolean

    abstract fun getViewPager() : VP

    abstract fun getBottomNavigationView() : BNV

    abstract fun getFragmentList() : List<FragmentItemBean>

}