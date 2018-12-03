package com.karanchuk.roman.testtranslate.presentation.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl;
import com.karanchuk.roman.testtranslate.presentation.ui.settings.SettingsFragment;
import com.karanchuk.roman.testtranslate.presentation.ui.stored.ClearStoredDialogFragment;
import com.karanchuk.roman.testtranslate.presentation.ui.stored.StoredFragment;
import com.karanchuk.roman.testtranslate.presentation.ui.translator.TranslatorFragment;

import java.util.Arrays;
import java.util.List;

import static com.karanchuk.roman.testtranslate.common.Constants.FAVORITES_TITLE;
import static com.karanchuk.roman.testtranslate.common.Constants.HISTORY_TITLE;

public class MainActivity extends AppCompatActivity implements
        ClearStoredDialogFragment.ClearStoredDialogListener,
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver {

    private static final String MAIN_HANDLER_THREAD = "MAIN_HANDLER_THREAD";

    private List<TranslatedItem> mHistoryTranslatedItems;
    private List<TranslatedItem> mFavoritesTranslatedItems;
    private TranslatorRepositoryImpl mRepository;
    private Handler mMainHandler;
    private HandlerThread mMainHandlerThread;
    // private ContentManager mContentManager;

    private NoSwipePager mMainViewPager;
    private BottomBarAdapter mMainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mContentManager = ContentManager.getInstance();

        mMainViewPager = findViewById(R.id.pager);
        mMainViewPager.setSwipingEnabled(false);
        mMainPagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
        mMainPagerAdapter.addFragment(new TranslatorFragment());
        mMainPagerAdapter.addFragment(new StoredFragment());
        mMainPagerAdapter.addFragment(new SettingsFragment());
        mMainViewPager.setAdapter(mMainPagerAdapter);
        mMainViewPager.setOffscreenPageLimit(3);

        final AHBottomNavigation bottomNavigation = findViewById(R.id.navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("", R.drawable.translation_black_back_dark512);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("", R.drawable.bookmark_black_shape_light512);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("", R.drawable.gear_black_shape_light512);
        bottomNavigation.addItems(Arrays.asList(item1, item2, item3));
        bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            if (!wasSelected) {
                mMainViewPager.setCurrentItem(position);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        TranslatorRepository localDataSource = TranslatorLocalRepository.getInstance(this);
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);
        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);

        mMainHandlerThread = new HandlerThread(MAIN_HANDLER_THREAD);
        mMainHandlerThread.start();
        mMainHandler = new Handler(mMainHandlerThread.getLooper());

        mMainHandler.post(() -> {
            mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
            mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRepository.removeHistoryContentObserver(this);
        mRepository.removeFavoritesContentObserver(this);
        mMainHandlerThread.quit();
        mMainHandlerThread = null;
        mMainHandler = null;
    }

    @Override
    public void onDialogPositiveClick(ClearStoredDialogFragment dialog) {
        String curTitle = dialog.getArguments().getString("title");
        if (curTitle != null) {
            switch (curTitle) {
                case HISTORY_TITLE:
                    if (!mHistoryTranslatedItems.isEmpty()) {
                        mMainHandler.post(() ->
                            mRepository.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY)
                        );
//                        mContentManager.notifyTranslatedItemChanged();
                    }
                    break;
                case FAVORITES_TITLE:
                    if (!mFavoritesTranslatedItems.isEmpty()) {
                        mMainHandler.post(() -> {
                            mRepository.updateIsFavoriteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY, false);
                            mRepository.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
                        });
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
        mMainHandler.post(() -> {
            mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
        });
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
        });
    }
}
