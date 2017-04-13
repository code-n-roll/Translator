package com.karanchuk.roman.testtranslate.favorites;

import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karanchuk.roman.testtranslate.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 8.4.17.
 */

public class FavoritesFragment extends Fragment implements
        ViewPager.OnPageChangeListener{
    private ViewPager mViewPager;
    private FavoritesPagerAdapter mFavoritesAdapter;
    private TabLayout mTabLayout;
    private List<Fragment> mFragments;
    private List<String> mTitles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        initViewPager(view);
        initTabLayout(view);
        initToolbar();

        return view;
    }

    private void initTabLayout(View view){
        mTabLayout = (TabLayout) view.findViewById(R.id.tablayout_favorites);
        mTabLayout.setupWithViewPager(mViewPager);
    }
    
    public void initToolbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
            actionBar.hide();
        }
    }

    private void initViewPager(View view){
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_favorites);

        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
        mFavoritesAdapter = new FavoritesPagerAdapter(getChildFragmentManager(),mFragments, mTitles);
        mFavoritesAdapter.addFragment(new ContentHistoryFragment(), "History");
        mFavoritesAdapter.addFragment(new ContentFavoritesFragment(), "Favorites");
        mViewPager.setAdapter(mFavoritesAdapter);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
