package com.karanchuk.roman.testtranslate.stored;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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
import com.karanchuk.roman.testtranslate.common.database.TablePersistenceContract;
import com.karanchuk.roman.testtranslate.common.database.repository.TranslatorLocalRepository;
import com.karanchuk.roman.testtranslate.common.database.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.common.database.repository.TranslatorRepositoryImpl;
import com.karanchuk.roman.testtranslate.common.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.stored.favorites.FavoritesFragment;
import com.karanchuk.roman.testtranslate.stored.history.HistoryFragment;
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
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver {
    private static final int HISTORY_FRAGMENT = 0;
    private static final int FAVORITES_FRAGMENT = 1;

    private StoredPagerAdapter mFavoritesAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageButton mClearStored;
    private View mMainActivityContainer;
    private BottomNavigationView mNavigation;
    private ClearStoredDialogFragment mClearHistoryDialog;

    private List<TranslatedItem> mFavoritesItems;
    private List<TranslatedItem> mHistoryItems;

    private TranslatorRepositoryImpl mRepository;
    private ContentManager mContentManager;
    private Handler mMainHandler;
    private int mCurPosition = 0;
    private int mBottomPadding;
    private Bundle mBundle;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stored, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViewsOnFragment(view);
        findViewsOnActivity();

        mMainHandler = new Handler(Looper.getMainLooper());

        mContentManager = ContentManager.getInstance();
        TranslatorRepository localDataSource = TranslatorLocalRepository.getInstance(getContext());
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);
        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);
        mFavoritesItems = mRepository.getTranslatedItems(TablePersistenceContract.
                TranslatedItemEntry.TABLE_NAME_FAVORITES);
        mHistoryItems = mRepository.getTranslatedItems(TablePersistenceContract.
                TranslatedItemEntry.TABLE_NAME_HISTORY);


        mClearHistoryDialog = new ClearStoredDialogFragment();
        mBundle = new Bundle();
        mTabLayout.setupWithViewPager(mViewPager);



        UIUtils.changeSoftInputModeWithOrientation(getActivity());
        handleKeyboardVisibility();

        initClearStored();
        initViewPager();
        initActionBar();
    }

    private void initClearStored(){
        if (mHistoryItems.isEmpty()){
            mClearStored.setVisibility(View.INVISIBLE);
        } else {
            mClearStored.setVisibility(View.VISIBLE);
        }
        mClearStored.setOnClickListener(bundle -> clickOnClearStored(mBundle));
    }

    private void findViewsOnFragment(View view){
        mTabLayout = view.findViewById(R.id.tablayout_favorites);
        mViewPager = view.findViewById(R.id.viewpager_stored);
        mClearStored = view.findViewById(R.id.imagebutton_clear_stored);
    }

    private void findViewsOnActivity(){
        mMainActivityContainer = getActivity().findViewById(R.id.main_activity_container);
        mNavigation = getActivity().findViewById(R.id.navigation);
    }

    private void clickOnClearStored(Bundle bundle){
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
        mFavoritesAdapter = new StoredPagerAdapter(getChildFragmentManager(),
                new ArrayList<>(), new ArrayList<>());
        mFavoritesAdapter.addFragment(new HistoryFragment(),
                getResources().getString(R.string.title_history));
        mFavoritesAdapter.addFragment(new FavoritesFragment(),
                getResources().getString(R.string.title_favorites));
        mViewPager.setAdapter(mFavoritesAdapter);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position,
                               float positionOffset,
                               int positionOffsetPixels) {
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

        if (mCurPosition != position){
            mContentManager.notifyTranslatedItemChanged();
        }
        mCurPosition = position;
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
            mFavoritesItems.addAll(mRepository.getTranslatedItems(TablePersistenceContract.
                    TranslatedItemEntry.TABLE_NAME_FAVORITES));
        });
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mHistoryItems.clear();
            mHistoryItems.addAll(mRepository.getTranslatedItems(TablePersistenceContract.
                    TranslatedItemEntry.TABLE_NAME_HISTORY));
        });
    }

    private void handleKeyboardVisibility(){
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                isOpen -> {
                    if (isOpen && isAdded()){
                            mBottomPadding = UIUtils.hideBottomNavViewGetBottomPadding(
                                    getActivity(),
                                    mMainActivityContainer,
                                    mNavigation);
                    } else if (!isOpen && isAdded()){
                            UIUtils.showBottomNavViewSetBottomPadding(
                                    getActivity(),
                                    mMainActivityContainer,
                                    mNavigation,
                                    mBottomPadding);
                    }
                });
    }
}
