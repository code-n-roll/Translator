package com.karanchuk.roman.testtranslate.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.settings.SettingsFragment;
import com.karanchuk.roman.testtranslate.ui.stored.StoredFragment;
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment;
import com.karanchuk.roman.testtranslate.ui.view.ClearStoredDialogFragment;
import com.karanchuk.roman.testtranslate.utils.ContentManager;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ClearStoredDialogFragment.ClearStoredDialogListener,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver{

    private String mCurFragment = "TRANSLATOR_FRAGMENT";
    public static String TRANSLATOR_FRAGMENT = "TRANSLATOR_FRAGMENT",
                        STORED_FRAGMENT = "STORED_FRAGMENT",
                        SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT";
    private List<TranslatedItem> mHistoryTranslatedItems,
                                mFavoritesTranslatedItems;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private ContentManager mContentManager;

    public void setCurFragment(String curFragment){
        this.mCurFragment = curFragment;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    if (!mCurFragment.equals(TRANSLATOR_FRAGMENT)) {
                        setCurFragment(TRANSLATOR_FRAGMENT);
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.main_activity_container,
                                        new TranslatorFragment(), TRANSLATOR_FRAGMENT).
                                commit();
                    }
                    return true;
                case R.id.navigation_favorites:
                    if (!mCurFragment.equals(STORED_FRAGMENT)){
                        setCurFragment(STORED_FRAGMENT);
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.main_activity_container,
                                        new StoredFragment(), STORED_FRAGMENT).
                                commit();
                    }
                    return true;
                case R.id.navigation_settings:
                    if (!mCurFragment.equals(SETTINGS_FRAGMENT)) {
                        setCurFragment(SETTINGS_FRAGMENT);
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

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonUtils.getDictDefinitionFromJson(
                JsonUtils.getJsonObjectFromFile(
                        getAssets(),"translator_response.json"));

        mMainHandler = new Handler(getMainLooper());
        mContentManager = ContentManager.getInstance();

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_activity_container,
                        new TranslatorFragment(), TRANSLATOR_FRAGMENT).
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
    public void onDialogPositiveClick(ClearStoredDialogFragment dialog) {
        Toast.makeText(this, "yes was clicked", Toast.LENGTH_SHORT).show();
        String curTitle = dialog.getArguments().getString("title");
        if (curTitle != null) {
            switch (curTitle) {
                case " History":
                    if (!mHistoryTranslatedItems.isEmpty()) {
                        mRepository.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);

                    }
                    break;
                case " Favorites":
                    if (!mFavoritesTranslatedItems.isEmpty()) {
                        mRepository.updateIsFavoriteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY, false);
                        mRepository.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);

                    }
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    public void onDialogNegativeClick(ClearStoredDialogFragment dialog) {
        Toast.makeText(this,"cancel was clicked", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
            }
        });
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
            }
        });
    }
}
