package com.karanchuk.roman.testtranslate.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class BottomBarAdapter(fragmentManager: FragmentManager) : CachedFragmentStatePagerAdapter(fragmentManager) {

    private val fragments = ArrayList<Fragment>()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}