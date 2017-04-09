package com.karanchuk.roman.testtranslate.Favorites;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.TranslateFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 8.4.17.
 */

public class FavoritesFragment extends Fragment implements TabHost.OnTabChangeListener,
        ViewPager.OnPageChangeListener{
    private ViewPager mViewPager;
    private FavoritesPagerAdapter mFavoritesAdapter;
    private TabHost mTabHost;
    private List<Fragment> mFragments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        initViewPager(view);
        initTabHost(view);

        return view;
    }

    private void initViewPager(View view){
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_favorites);

        mFragments = new ArrayList<>();
        mFragments.add(new ContentFavoritesFragment());
        mFragments.add(new TranslateFragment());

        mFavoritesAdapter = new FavoritesPagerAdapter(getChildFragmentManager(),mFragments);
        mViewPager.setAdapter(mFavoritesAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initTabHost(View view){
        mTabHost = (TabHost) view.findViewById(R.id.tab_host_favorites);
        mTabHost.setup();

        mTabHost.addTab(mTabHost.newTabSpec("tab_favorites_tag").setIndicator("Favorites").setContent(new FakeContent(getContext())));
        mTabHost.addTab(mTabHost.newTabSpec("tab_history_tag").setIndicator("History").setContent(new FakeContent(getContext())));

        mTabHost.setOnTabChangedListener(this);
    }

    public class FakeContent implements TabHost.TabContentFactory{
        Context mContext;
        public FakeContent(Context context){
            mContext = context;
        }
        @Override
        public View createTabContent(String tag) {
            View fakeView = new View(mContext);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);
            return fakeView;
        }
    }


    @Override
    public void onTabChanged(String tabId) {
        int pos = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(pos);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mTabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
