package com.karanchuk.roman.testtranslate.ui.stored;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TablesPersistenceContract;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.stored.favorites.FavoritesFragment;
import com.karanchuk.roman.testtranslate.ui.stored.history.HistoryFragment;
import com.karanchuk.roman.testtranslate.ui.view.ClearStoredDialogFragment;
import com.karanchuk.roman.testtranslate.utils.ContentManager;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 8.4.17.
 */

public class StoredFragment extends Fragment implements
        ViewPager.OnPageChangeListener,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver
{
    private ViewPager mViewPager;
    private StoredPagerAdapter mFavoritesAdapter;
    private TabLayout mTabLayout;
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private ImageButton mClearStored;
    private View mView;
    private TranslatorRepository mRepository;
    private ClearStoredDialogFragment mClearHistoryDialog;
    private static String CLEAR_HISTORY_DIALOG = "CLEAR_HISTORY_DIALOG",
                    CLEAR_HISTORY_FAVORITES = "CLEAR_HISTORY_FAVORITES";
    private ContentManager mContentManager;
    private int curPosition = 0;
    private Handler mMainHandler;
    private List<TranslatedItem> mFavoritesItems, mHistoryItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stored, container, false);


        mMainHandler = new Handler(Looper.getMainLooper());

        mContentManager = ContentManager.getInstance();
        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);
        mFavoritesItems = mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES);
        mHistoryItems = mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);


        mClearHistoryDialog = new ClearStoredDialogFragment();
        final Bundle bundle = new Bundle();


        mClearStored = (ImageButton) mView.findViewById(R.id.imagebutton_clear_stored);
        if (mHistoryItems.isEmpty()){
            mClearStored.setVisibility(View.INVISIBLE);
        } else {
            mClearStored.setVisibility(View.VISIBLE);
        }
        mClearStored.setOnClickListener(new View.OnClickListener() {
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

        UIUtils.changeSoftInputModeWithOrientation(getActivity());
        initViewPager(mView);
        initTabLayout(mView);
        initToolbar();


        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRepository.removeFavoritesContentObserver(this);
        mRepository.removeHistoryContentObserver(this);
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
//        Log.d("viewpager log",
//                "onPageScrellod, position="+String.valueOf(position)+
//                        ",positionOffset="+String.valueOf(positionOffset)+
//                        ",positionOffsetPixels="+String.valueOf(positionOffsetPixels)
//        );
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0 && mHistoryItems.isEmpty() ||
                position == 1 && mFavoritesItems.isEmpty()){
            mClearStored.setVisibility(View.INVISIBLE);
        } else {
            mClearStored.setVisibility(View.VISIBLE);
        }

        if (curPosition != position){
            mContentManager.notifyTranslatedItemChanged();
        }
        curPosition = position;


//        Log.d("viewpager log", "onPageSelected, position="+ String.valueOf(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.d("viewpager log", "onPageScrollStateChanged, state="+String.valueOf(state));
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mFavoritesItems.clear();
                mFavoritesItems.addAll(mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES));
            }
        });
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mHistoryItems.clear();
                mHistoryItems.addAll(mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
            }
        });
    }
}
