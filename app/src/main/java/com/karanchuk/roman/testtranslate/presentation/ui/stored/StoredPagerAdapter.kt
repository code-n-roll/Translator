package com.karanchuk.roman.testtranslate.presentation.ui.stored

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.karanchuk.roman.testtranslate.presentation.ui.main.CachedFragmentStatePagerAdapter
import java.util.*

/**
 * Created by roman on 9.4.17.
 */

class StoredPagerAdapter(fm: FragmentManager) : CachedFragmentStatePagerAdapter(fm) {
    private val mFragments = ArrayList<Fragment>()
    private val mFragmentTitles = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitles[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragments.add(fragment)
        mFragmentTitles.add(title)
    }
}
