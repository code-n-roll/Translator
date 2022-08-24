package com.romankaranchuk.translator.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry
import com.romankaranchuk.translator.data.database.model.TranslatedItem
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver
import com.romankaranchuk.translator.ui.stored.ClearStoredDialogFragment
import com.romankaranchuk.translator.ui.stored.ClearStoredDialogFragment.ClearStoredDialogListener
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        private const val MAIN_HANDLER_THREAD = "MAIN_HANDLER_THREAD"
    }

    @Inject lateinit var fragmentInjectionFactory: FragmentFactory

    private var mHistoryTranslatedItems: List<TranslatedItem>? = null
    private var mFavoritesTranslatedItems: List<TranslatedItem>? = null
    private var mRepository: TranslatorRepositoryImpl? = null
    private var mMainHandler: Handler? = null
    private var mMainHandlerThread: HandlerThread? = null

    // private ContentManager mContentManager;

    private val clearStoredDialogListener = object : ClearStoredDialogListener {
        override fun onDialogPositiveClick(dialog: ClearStoredDialogFragment?) {
            val curTitle = dialog!!.arguments!!.getString("title")
            if (curTitle != null) {
                when (curTitle) {
                    com.romankaranchuk.translator.common.Constants.HISTORY_TITLE -> if (!mHistoryTranslatedItems!!.isEmpty()) {
                        mMainHandler!!.post { mRepository!!.deleteTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY) }
                        //                        mContentManager.notifyTranslatedItemChanged();
                    }
                    com.romankaranchuk.translator.common.Constants.FAVORITES_TITLE -> if (!mFavoritesTranslatedItems!!.isEmpty()) {
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
        inject()
        super.onCreate(savedInstanceState)

        // mContentManager = ContentManager.getInstance();

        setupNavigation()
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

    private fun setupNavigation() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_translator,
                R.id.navigation_history,
                R.id.navigation_settings
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnNavigationItemSelectedListener {
            if (it.itemId != navView.selectedItemId) {
                when(it.title) {
//                    getString(R.string.title_explore) -> {
//                    }
//                    getString(R.string.title_profile) -> {
//                    }
//                    getString(R.string.title_challenge) -> {
//                    }
                }
                NavigationUI.onNavDestinationSelected(it, navController)
            }
            true
        }
    }

    private fun inject() {
        AndroidInjection.inject(this)
        supportFragmentManager.fragmentFactory = fragmentInjectionFactory
    }
}