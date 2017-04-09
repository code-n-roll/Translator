package com.karanchuk.roman.testtranslate.Favorites;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class FavoritesPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public FavoritesPagerAdapter(FragmentManager fm, List<Fragment> fragments){
        super(fm);
        mFragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
