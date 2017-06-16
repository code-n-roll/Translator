package com.karanchuk.roman.testtranslate.presentation.view.fragment;

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
import android.widget.ImageButton;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.StoredPagerAdapter;
import com.karanchuk.roman.testtranslate.presentation.view.dialog_fragment.ClearStoredDialogFragment;
import com.karanchuk.roman.testtranslate.utils.ContentManager;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

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
    private static final String CLEAR_HISTORY_DIALOG = "CLEAR_HISTORY_DIALOG";
    private static final String CLEAR_HISTORY_FAVORITES = "CLEAR_HISTORY_FAVORITES";
    private static final int HISTORY_FRAGMENT = 0;
    private static final int FAVORITES_FRAGMENT = 1;

    private StoredPagerAdapter mFavoritesAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageButton mClearStored;
    private View mView, mMainActivityContainer;
    private BottomNavigationView mNavigation;

    private List<Fragment> mFragments;
    private List<TranslatedItem> mFavoritesItems;
    private List<TranslatedItem> mHistoryItems;
    private List<String> mTitles;

    private ClearStoredDialogFragment mClearHistoryDialog;
    private TranslatorRepository mRepository;
    private ContentManager mContentManager;
    private Handler mMainHandler;
    private int curPosition = 0;
    private int mBottomPadding;
    private Bundle mBundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stored, container, false);


        mMainHandler = new Handler(Looper.getMainLooper());

        mMainActivityContainer = getActivity().findViewById(R.id.main_activity_container);
        mNavigation = getActivity().findViewById(R.id.navigation);


        mContentManager = ContentManager.getInstance();
        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);
        mFavoritesItems = mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES);
        mHistoryItems = mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);


        mClearHistoryDialog = new ClearStoredDialogFragment();
        mBundle = new Bundle();



        UIUtils.changeSoftInputModeWithOrientation(getActivity());

        findViewsOnFragment();

        if (mHistoryItems.isEmpty()){
            mClearStored.setVisibility(View.INVISIBLE);
        } else {
            mClearStored.setVisibility(View.VISIBLE);
        }
        mClearStored.setOnClickListener(bundle -> clickOnClearStored(mBundle));
        mTabLayout.setupWithViewPager(mViewPager);

        initViewPager();
        handleKeyboardVisibility();
        initActionBar();


        return mView;
    }

    private void findViewsOnFragment(){
        mTabLayout = mView.findViewById(R.id.tablayout_favorites);
        mViewPager = mView.findViewById(R.id.viewpager_stored);
        mClearStored = mView.findViewById(R.id.imagebutton_clear_stored);
    }

    private void clickOnClearStored(final Bundle bundle){
        switch(mViewPager.getCurrentItem()) {
            case HISTORY_FRAGMENT:
//                bundle.putString("title"," History");
//                mClearHistoryDialog.setArguments(bundle);
//                mClearHistoryDialog.show(getFragmentManager(), CLEAR_HISTORY_DIALOG);
                break;
            case FAVORITES_FRAGMENT:
//                bundle.putString("title"," Favorites");
//                mClearHistoryDialog.setArguments(bundle);
//                mClearHistoryDialog.show(getFragmentManager(), CLEAR_HISTORY_FAVORITES);
                break;
            default:
                break;
        }
        UIUtils.showToast(getContext(), getResources().getString(R.string.next_release_message));
    }

    @Override
    public void onStop() {
        super.onStop();
        mRepository.removeFavoritesContentObserver(this);
        mRepository.removeHistoryContentObserver(this);
    }


    private void initActionBar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
            actionBar.hide();
        }
    }

    private void initViewPager(){
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
        mMainHandler.post(() -> {
            mFavoritesItems.clear();
            mFavoritesItems.addAll(mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES));
        });
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mHistoryItems.clear();
            mHistoryItems.addAll(mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
        });
    }

    private void handleKeyboardVisibility(){
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                isOpen -> {
                    // some code depending on keyboard visiblity status
                    if (isOpen && isAdded()){
                            mBottomPadding =  UIUtils.hideBottomNavViewGetBottomPadding(getActivity(),mMainActivityContainer,mNavigation);
                    } else if (!isOpen && isAdded()){
                            UIUtils.showBottomNavViewSetBottomPadding(getActivity(),mMainActivityContainer,mNavigation,mBottomPadding);
                    }
                });
    }
}
