package com.karanchuk.roman.testtranslate.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.appcompat.app.AppCompatActivity
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.karanchuk.roman.testtranslate.R
import com.karanchuk.roman.testtranslate.common.Constants
import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract.TranslatedItemEntry
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepository
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver
import com.karanchuk.roman.testtranslate.ui.settings.SettingsFragment
import com.karanchuk.roman.testtranslate.ui.stored.ClearStoredDialogFragment
import com.karanchuk.roman.testtranslate.ui.stored.ClearStoredDialogFragment.ClearStoredDialogListener
import com.karanchuk.roman.testtranslate.ui.stored.StoredFragment
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment
import java.util.Arrays

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        private const val MAIN_HANDLER_THREAD = "MAIN_HANDLER_THREAD"
    }

    private var mHistoryTranslatedItems: List<TranslatedItem>? = null
    private var mFavoritesTranslatedItems: List<TranslatedItem>? = null
    private var mRepository: TranslatorRepositoryImpl? = null
    private var mMainHandler: Handler? = null
    private var mMainHandlerThread: HandlerThread? = null

    // private ContentManager mContentManager;
    private var mMainViewPager: NoSwipePager? = null
    private var mMainPagerAdapter: BottomBarAdapter? = null

    private val clearStoredDialogListener = object : ClearStoredDialogListener {
        override fun onDialogPositiveClick(dialog: ClearStoredDialogFragment?) {
            val curTitle = dialog!!.arguments!!.getString("title")
            if (curTitle != null) {
                when (curTitle) {
                    Constants.HISTORY_TITLE -> if (!mHistoryTranslatedItems!!.isEmpty()) {
                        mMainHandler!!.post { mRepository!!.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY) }
                        //                        mContentManager.notifyTranslatedItemChanged();
                    }
                    Constants.FAVORITES_TITLE -> if (!mFavoritesTranslatedItems!!.isEmpty()) {
                        mMainHandler!!.post {
                            mRepository!!.updateIsFavoriteTranslatedItems(
                                TranslatedItemEntry.TABLE_NAME_HISTORY,
                                false
                            )
                            mRepository!!.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES)
                        }
                        //                        mContentManager.notifyTranslatedItemChanged();
                    }
                    else -> {
                    }
                }
            }
        }

        override fun onDialogNegativeClick(dialog: ClearStoredDialogFragment?) {
//        UIUtils.showToast(this,"cancel was clicked");
        }
    }

    private val historyTranslatedItemsRepositoryObserver = HistoryTranslatedItemsRepositoryObserver {
        mMainHandler!!.post {
            mHistoryTranslatedItems =
                mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY)
        }
    }

    private val favoritesTranslatedItemsRepositoryObserver = FavoritesTranslatedItemsRepositoryObserver {
        mMainHandler!!.post {
            mFavoritesTranslatedItems =
                mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // mContentManager = ContentManager.getInstance();
        mMainViewPager = findViewById(R.id.pager)
        mMainViewPager!!.setSwipingEnabled(false)
        mMainPagerAdapter = BottomBarAdapter(supportFragmentManager)
        mMainPagerAdapter!!.addFragment(TranslatorFragment())
        mMainPagerAdapter!!.addFragment(StoredFragment())
        mMainPagerAdapter!!.addFragment(SettingsFragment())
        mMainViewPager!!.setAdapter(mMainPagerAdapter)
        mMainViewPager!!.setOffscreenPageLimit(3)
        val bottomNavigation = findViewById<AHBottomNavigation>(R.id.navigation)
        val item1 = AHBottomNavigationItem("", R.drawable.translation_black_back_dark512)
        val item2 = AHBottomNavigationItem("", R.drawable.bookmark_black_shape_light512)
        val item3 = AHBottomNavigationItem("", R.drawable.gear_black_shape_light512)
        bottomNavigation.addItems(Arrays.asList(item1, item2, item3))
        bottomNavigation.setOnTabSelectedListener { position: Int, wasSelected: Boolean ->
            if (!wasSelected) {
                mMainViewPager!!.setCurrentItem(position)
                return@setOnTabSelectedListener true
            }
            false
        }
    }

    override fun onStart() {
        super.onStart()

        val localDataSource: TranslatorRepository = TranslatorLocalRepository.getInstance(this)
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource)
        mRepository!!.addHistoryContentObserver(historyTranslatedItemsRepositoryObserver)
        mRepository!!.addFavoritesContentObserver(favoritesTranslatedItemsRepositoryObserver)
        mMainHandlerThread = HandlerThread(MAIN_HANDLER_THREAD)
        mMainHandlerThread!!.start()
        mMainHandler = Handler(mMainHandlerThread!!.looper)
        mMainHandler!!.post {
            mHistoryTranslatedItems =
                mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY)
            mFavoritesTranslatedItems =
                mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES)
        }
    }

    override fun onStop() {
        super.onStop()

        mRepository!!.removeHistoryContentObserver(historyTranslatedItemsRepositoryObserver)
        mRepository!!.removeFavoritesContentObserver(favoritesTranslatedItemsRepositoryObserver)
        mMainHandlerThread!!.quit()
        mMainHandlerThread = null
        mMainHandler = null
    }

    private fun setupPager() {

    }

    private fun setupListeners() {

    }
}