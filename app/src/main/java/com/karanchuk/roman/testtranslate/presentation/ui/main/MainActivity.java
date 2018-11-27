package com.karanchuk.roman.testtranslate.presentation.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl;
import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.ui.stored.ClearStoredDialogFragment;
import com.karanchuk.roman.testtranslate.presentation.ui.settings.SettingsFragment;
import com.karanchuk.roman.testtranslate.presentation.ui.stored.StoredFragment;
import com.karanchuk.roman.testtranslate.presentation.ui.translator.TranslatorFragment;
import com.karanchuk.roman.testtranslate.utils.ContentManager;

import java.util.List;

import static com.karanchuk.roman.testtranslate.common.Constants.CUR_FRAGMENT_TAG;
import static com.karanchuk.roman.testtranslate.common.Constants.FAVORITES_TITLE;
import static com.karanchuk.roman.testtranslate.common.Constants.HISTORY_TITLE;
import static com.karanchuk.roman.testtranslate.common.Constants.SETTINGS_FRAGMENT;
import static com.karanchuk.roman.testtranslate.common.Constants.STORED_FRAGMENT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSLATOR_FRAGMENT;

public class MainActivity extends AppCompatActivity implements
        ClearStoredDialogFragment.ClearStoredDialogListener,
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver{

    private String mCurFragmentTag = "TRANSLATOR_FRAGMENT";
    private List<TranslatedItem> mHistoryTranslatedItems;
    private List<TranslatedItem> mFavoritesTranslatedItems;
    private TranslatorRepositoryImpl mRepository;
    private Handler mMainHandler;
    private ContentManager mContentManager;
    private Fragment mCurFragment;

    public void setCurFragmentTag(final String curFragmentTag){
        mCurFragmentTag = curFragmentTag;
    }


    private boolean clickOnItemNavigation(@NonNull final MenuItem item){
        switch (item.getItemId()) {
            case R.id.navigation_translate:
                if (!mCurFragmentTag.equals(TRANSLATOR_FRAGMENT)){
                    setCurFragmentTag(TRANSLATOR_FRAGMENT);
                    navigateToFragment(new TranslatorFragment(), TRANSLATOR_FRAGMENT);
                }
                return true;
            case R.id.navigation_favorites:
                if (!mCurFragmentTag.equals(STORED_FRAGMENT)){
                    setCurFragmentTag(STORED_FRAGMENT);
                    navigateToFragment(new StoredFragment(), STORED_FRAGMENT);
                }
                return true;
            case R.id.navigation_settings:
                if (!mCurFragmentTag.equals(SETTINGS_FRAGMENT)){
                    setCurFragmentTag(SETTINGS_FRAGMENT);
                    navigateToFragment(new SettingsFragment(), SETTINGS_FRAGMENT);
                }
                return true;
        }
        return false;
    }

    private void navigateToFragment(Fragment fragment, String fragmentTag){
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_activity_container, fragment, fragmentTag).
                commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainHandler = new Handler(getMainLooper());
        mContentManager = ContentManager.getInstance();

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this::clickOnItemNavigation);


        if (savedInstanceState != null) {
            mCurFragmentTag = savedInstanceState.getString(CUR_FRAGMENT_TAG);
            mCurFragment = getSupportFragmentManager().getFragment(savedInstanceState,mCurFragmentTag);
        } else {
            mCurFragment = new TranslatorFragment();
            mCurFragmentTag = TRANSLATOR_FRAGMENT;
        }
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_activity_container,
                        mCurFragment, mCurFragmentTag).
                commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        TranslatorRepository localDataSource = TranslatorLocalRepository.getInstance(this);
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);
        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
        mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRepository.removeHistoryContentObserver(this);
        mRepository.removeFavoritesContentObserver(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().
                putFragment(outState, mCurFragmentTag,
                        getSupportFragmentManager().findFragmentByTag(mCurFragmentTag));
        outState.putString(CUR_FRAGMENT_TAG, mCurFragmentTag);
    }


    @Override
    public void onDialogPositiveClick(ClearStoredDialogFragment dialog) {
        String curTitle = dialog.getArguments().getString("title");
        if (curTitle != null) {
            switch (curTitle) {
                case HISTORY_TITLE:
                    if (!mHistoryTranslatedItems.isEmpty()) {
                        mRepository.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
//                        mContentManager.notifyTranslatedItemChanged();
                    }
                    break;
                case FAVORITES_TITLE:
                    if (!mFavoritesTranslatedItems.isEmpty()) {
                        mRepository.updateIsFavoriteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY, false);
                        mRepository.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
//                        mContentManager.notifyTranslatedItemChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    public void onDialogNegativeClick(ClearStoredDialogFragment dialog) {
//        UIUtils.showToast(this,"cancel was clicked");
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> mHistoryTranslatedItems =
                mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY));
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(() -> mFavoritesTranslatedItems =
                mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES));
    }
}
