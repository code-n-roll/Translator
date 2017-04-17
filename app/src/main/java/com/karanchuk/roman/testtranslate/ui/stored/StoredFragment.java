package com.karanchuk.roman.testtranslate.ui.stored;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.ui.stored.favorites.FavoritesFragment;
import com.karanchuk.roman.testtranslate.ui.stored.history.HistoryFragment;
import com.karanchuk.roman.testtranslate.ui.view.ClearStoredDialogFragment;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import static com.karanchuk.roman.testtranslate.ui.main.MainActivity.STORED_FRAGMENT;

/**
 * Created by roman on 8.4.17.
 */

public class StoredFragment extends Fragment implements
        ViewPager.OnPageChangeListener,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver {
    private ViewPager mViewPager;
    private StoredPagerAdapter mFavoritesAdapter;
    private TabLayout mTabLayout;
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private ImageButton mClearHistory;
    private View mView;
    private TranslatorRepository mRepository;
    private ClearStoredDialogFragment mClearHistoryDialog;
    private static String CLEAR_HISTORY_DIALOG = "CLEAR_HISTORY_DIALOG",
                    CLEAR_HISTORY_FAVORITES = "CLEAR_HISTORY_FAVORITES";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stored, container, false);

//        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
//        mRepository = TranslatorRepository.getInstance(localDataSource);
//        mRepository.addHistoryContentObserver(this);

        mClearHistoryDialog = new ClearStoredDialogFragment();
        final Bundle bundle = new Bundle();


        mClearHistory = (ImageButton) mView.findViewById(R.id.imagebutton_clear_history);
        mClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(mViewPager.getCurrentItem()) {
                    case 0:
                        bundle.putString("title"," History");
                        mClearHistoryDialog.setArguments(bundle);
                        mClearHistoryDialog.show(getFragmentManager(), CLEAR_HISTORY_DIALOG);
                        break;
                    case 1:
                        bundle.putString("title"," Favorites");
                        mClearHistoryDialog.setArguments(bundle);
                        mClearHistoryDialog.show(getFragmentManager(), CLEAR_HISTORY_FAVORITES);
                        break;
                    default:
                        break;
                }
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
        Log.d("viewpager log",
                "onPageScrellod, position="+String.valueOf(position)+
                        ",positionOffset="+String.valueOf(positionOffset)+
                        ",positionOffsetPixels="+String.valueOf(positionOffsetPixels)
        );
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("viewpager log", "onPageSelected, position="+ String.valueOf(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d("viewpager log", "onPageScrollStateChanged, state="+String.valueOf(state));
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {

    }
}
