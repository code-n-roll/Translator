package com.karanchuk.roman.testtranslate.ui.stored;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.stored.favorites.FavoritesFragment;
import com.karanchuk.roman.testtranslate.ui.stored.history.HistoryFragment;
import com.karanchuk.roman.testtranslate.ui.view.ClearHistoryDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 8.4.17.
 */

public class StoredFragment extends Fragment implements
        ViewPager.OnPageChangeListener,
        TranslatorRepository.TranslatedItemsRepositoryObserver{
    private ViewPager mViewPager;
    private StoredPagerAdapter mFavoritesAdapter;
    private TabLayout mTabLayout;
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private ImageButton mClearHistory;
    private View mView;
    private TranslatorRepository mRepository;
    private ClearHistoryDialogFragment mClearHistoryDialog;
    private static String CLEAR_HISTORY_DIALOG = "CLEAR_HISTORY_DIALOG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stored, container, false);

//        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
//        mRepository = TranslatorRepository.getInstance(localDataSource);
//        mRepository.addContentObserver(this);

        mClearHistoryDialog = new ClearHistoryDialogFragment();
        mClearHistory = (ImageButton) mView.findViewById(R.id.imagebutton_clear_history);
        mClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClearHistoryDialog.show(getFragmentManager(),CLEAR_HISTORY_DIALOG);
            }
        });

        initViewPager(mView);
        initTabLayout(mView);
        initToolbar();

        return mView;
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
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_stored);

        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
        mFavoritesAdapter = new StoredPagerAdapter(getChildFragmentManager(),mFragments, mTitles);
        mFavoritesAdapter.addFragment(new HistoryFragment(), "History");
        mFavoritesAdapter.addFragment(new FavoritesFragment(), "Favorites");
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

    @Override
    public void onTranslatedItemsChanged() {

    }
}
