package com.karanchuk.roman.testtranslate.presentation.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.view.ClearStoredDialogFragment;
import com.karanchuk.roman.testtranslate.presentation.view.fragment.SettingsFragment;
import com.karanchuk.roman.testtranslate.presentation.view.fragment.StoredFragment;
import com.karanchuk.roman.testtranslate.presentation.view.fragment.TranslatorFragment;
import com.karanchuk.roman.testtranslate.utils.ContentManager;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ClearStoredDialogFragment.ClearStoredDialogListener,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver{

    public static String TRANSLATOR_FRAGMENT = "TRANSLATOR_FRAGMENT";
    public static String STORED_FRAGMENT = "STORED_FRAGMENT";
    public static String SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT";
    public static String CUR_FRAGMENT_TAG = "CUR_FRAGMENT_TAG";
    private String mCurFragmentTag = "TRANSLATOR_FRAGMENT";
    private List<TranslatedItem> mHistoryTranslatedItems;
    private List<TranslatedItem> mFavoritesTranslatedItems;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private ContentManager mContentManager;
    private Fragment mCurFragment;

    public void setCurFragmentTag(final String curFragmentTag){
        this.mCurFragmentTag = curFragmentTag;
    }


    private boolean clickOnItemNavigation(@NonNull final MenuItem item){
        switch (item.getItemId()) {
            case R.id.navigation_translate:
                if (!mCurFragmentTag.equals(TRANSLATOR_FRAGMENT)) {
                    setCurFragmentTag(TRANSLATOR_FRAGMENT);
                    getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.main_activity_container,
                                    new TranslatorFragment(), TRANSLATOR_FRAGMENT).
                            commit();
                }
                return true;
            case R.id.navigation_favorites:
                if (!mCurFragmentTag.equals(STORED_FRAGMENT)){
                    setCurFragmentTag(STORED_FRAGMENT);
                    getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.main_activity_container,
                                    new StoredFragment(), STORED_FRAGMENT).
                            commit();
                }
                return true;
            case R.id.navigation_settings:
                if (!mCurFragmentTag.equals(SETTINGS_FRAGMENT)) {
                    setCurFragmentTag(SETTINGS_FRAGMENT);
                    getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.main_activity_container,
                                    new SettingsFragment(), SETTINGS_FRAGMENT).
                            commit();
                }
                return true;
        }
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonUtils.getDictDefinitionFromJson(JsonUtils.getJsonObjectFromFile(
                getAssets(),
                "translator_response.json"));

        mMainHandler = new Handler(getMainLooper());
        mContentManager = ContentManager.getInstance();

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener((item)->clickOnItemNavigation(item));


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
        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(this);
        mRepository = TranslatorRepository.getInstance(localDataSource);
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
        getSupportFragmentManager().putFragment(
                outState,
                mCurFragmentTag,
                getSupportFragmentManager().findFragmentByTag(mCurFragmentTag));
        outState.putString(CUR_FRAGMENT_TAG, mCurFragmentTag);
    }


    @Override
    public void onDialogPositiveClick(ClearStoredDialogFragment dialog) {
//        UIUtils.showToast(this, "yes was clicked");
        String curTitle = dialog.getArguments().getString("title");
        if (curTitle != null) {
            switch (curTitle) {
                case " History":
                    if (!mHistoryTranslatedItems.isEmpty()) {
                        mRepository.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
//                        mContentManager.notifyTranslatedItemChanged();
                    }
                    break;
                case " Favorites":
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
