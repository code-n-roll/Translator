package com.romankaranchuk.translator.ui.stored.favorites

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.common.Constants.*
import com.romankaranchuk.translator.utils.extensions.bind
import com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry
import com.romankaranchuk.translator.data.database.model.TranslatedItem
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository.*
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl
import com.romankaranchuk.translator.ui.stored.StoredRecyclerAdapter
import com.romankaranchuk.translator.utils.ContentManager
import java.util.*

/**
 * Created by roman on 9.4.17.
 */

class FavoritesFragment : Fragment(),
        SearchView.OnQueryTextListener,
        TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver,
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        ContentManager.TranslatedItemChanged {
    private val mFavoritesRecycler: RecyclerView by bind(R.id.favorites_items_list)
    private val mSearchViewFavorites: SearchView by bind(R.id.search_view_favorites)
    private val mButtonIsFavorite: ImageButton by bind(R.id.imagebutton_isfavorite_favorite_item)
    private val mEmptyView: View by bind(R.id.include_content_favorites_empty_item_list)
    private val mContentView: View by bind(R.id.include_content_favorites_full_item_list)
    private val mEmptySearchView: View by bind(R.id.include_content_favorites_empty_search)
    private val mTextViewEmptyFavorites: TextView by bind(R.id.textview_empty_item_list)
    private val mTextViewEmptySearch: TextView by bind(R.id.textview_empty_search)
    private val mImageViewEmptyFavorites: ImageView by bind(R.id.imageview_empty_item_list)
    private val mImageViewEmptySearch: ImageView by bind(R.id.imageview_empty_search)

    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mClearStored: ImageButton? = null
    private var mView: View? = null
    private var mRepository: TranslatorRepositoryImpl? = null
    private var mFavoritesTranslatedItems: MutableList<TranslatedItem>? = null
    private var mHistoryTranslatedItems: MutableList<TranslatedItem>? = null
    private var mCopyFavoritesTranslatedItems: MutableList<TranslatedItem>? = null
    private var mMainHandler: Handler? = null
    private var mContentManager: ContentManager? = null
    private var mSettings: SharedPreferences? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMainHandler = Handler(context!!.mainLooper)
        mSettings = activity!!.getSharedPreferences(PREFS_NAME, 0)

        mView = view
        val parentView = parentFragment!!.view
        if (parentView != null) {
            mClearStored = parentView.findViewById(R.id.imagebutton_clear_stored)
        }
        mContentManager = ContentManager.getInstance()

        val localDataSource = getInstance(context!!)
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource)
        mFavoritesTranslatedItems = mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES)
        mHistoryTranslatedItems = mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY)
        mCopyFavoritesTranslatedItems = ArrayList(mFavoritesTranslatedItems!!)

        mFavoritesTranslatedItems!!.reverse()
        mCopyFavoritesTranslatedItems!!.reverse()
        mHistoryTranslatedItems!!.reverse()

        mEmptySearchView.visibility = View.INVISIBLE

        mTextViewEmptyFavorites.setText(R.string.empty_favorites)
        mImageViewEmptyFavorites.setImageResource(R.drawable.bookmark_black_shape_light512)

        mTextViewEmptySearch.setText(R.string.empty_search)
        mImageViewEmptySearch.setImageResource(R.drawable.bookmark_black_shape_light512)

        mLayoutManager =
            LinearLayoutManager(mView!!.context)
        mFavoritesRecycler.layoutManager = mLayoutManager

        mSearchViewFavorites.setIconifiedByDefault(false)
        mSearchViewFavorites.queryHint = "Search in Favorites"
        mSearchViewFavorites.setOnQueryTextListener(this)
        mSearchViewFavorites.visibility = View.GONE

        val mDividerItemDecoration = DividerItemDecoration(mFavoritesRecycler.context, RecyclerView.VERTICAL)
        mFavoritesRecycler.addItemDecoration(mDividerItemDecoration)

//        val navigation = activity!!.findViewById<AHBottomNavigation>(R.id.navigation)
//        val translatorNavigationItem = navigation.findViewById<View>(R.id.navigation_translate)

//        mFavoritesRecycler.adapter = StoredRecyclerAdapter(
//                mFavoritesTranslatedItems,
//                { item -> clickOnItemStoredRecycler(item, translatorNavigationItem) },
//                { item -> clickOnSetFavoriteItem(item) },
//                UNIQUE_FAVORITES_FRAGMENT_ID)
//
        registerForContextMenu(mFavoritesRecycler)

        chooseCurView()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    private fun clickOnSetFavoriteItem(item: TranslatedItem) {
        if (item.isFavorite()) {
            item.isFavoriteUp(false)
            mRepository!!.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item)
            mFavoritesRecycler.adapter?.notifyItemChanged(mFavoritesTranslatedItems!!.indexOf(item))
        } else {
            item.isFavoriteUp(true)
            mRepository!!.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item)
            mFavoritesRecycler.adapter?.notifyItemChanged(mFavoritesTranslatedItems!!.indexOf(item))
        }
        mRepository!!.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item)

        //        UIUtils.showToast(getContext(),"isFavorite was clicked in favorites");
    }

    private fun clickOnItemStoredRecycler(item: TranslatedItem, view: View) {
        mSettings!!.edit().apply {
            putString(EDITTEXT_DATA, item.srcMeaning)
            putString(SRC_LANG, item.srcLanguageForUser)
            putString(TRG_LANG, item.trgLanguageForUser)
            putString(TRANSL_RESULT, item.trgMeaning)
            putString(TRANSL_CONTENT, item.dictDefinitionJSON)
            putString(CUR_SELECTED_ITEM_SRC_LANG, item.srcLanguageForAPI)
            putString(CUR_SELECTED_ITEM_TRG_LANG, item.trgLanguageForAPI)
            apply()
        }

        view.performClick()
    }

    override fun onStart() {
        super.onStart()

        mContentManager!!.addContentObserver(this)
        mRepository!!.addFavoritesContentObserver(this)
        mRepository!!.addHistoryContentObserver(this)
    }

    private fun chooseCurView() {
        if (!mFavoritesTranslatedItems!!.isEmpty()) {
            mEmptyView.visibility = View.GONE
            mContentView.visibility = View.VISIBLE
        } else {
            mEmptyView.visibility = View.VISIBLE
            mContentView.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterForContextMenu(mFavoritesRecycler)

        mRepository!!.removeHistoryContentObserver(this)
        mRepository!!.removeFavoritesContentObserver(this)
        mContentManager!!.removeContentObserver(this)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        var newText = newText
        //        mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
        newText = newText.toLowerCase()
        val newList = ArrayList<TranslatedItem>()
        for (item in mFavoritesTranslatedItems!!) {
            val srcMeaning = item.srcMeaning
            val trgMeaning = item.trgMeaning
            if (srcMeaning.contains(newText) || trgMeaning.contains(newText)) {
                newList.add(item)
            }
        }
        val adapter = mFavoritesRecycler.adapter as StoredRecyclerAdapter
        adapter.setFilter(newList)

        chooseCurSearchView(newList)
        return true
    }

    private fun chooseCurSearchView(list: List<TranslatedItem>) {
        if (list.isEmpty()) {
            mEmptySearchView.visibility = View.VISIBLE
            mFavoritesRecycler.visibility = View.INVISIBLE
        } else {
            mEmptySearchView.visibility = View.INVISIBLE
            mFavoritesRecycler.visibility = View.VISIBLE
        }
    }

    override fun onFavoritesTranslatedItemsChanged() {
        mMainHandler!!.post {
            mCopyFavoritesTranslatedItems!!.clear()
            mCopyFavoritesTranslatedItems!!.addAll(mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES))
            mCopyFavoritesTranslatedItems!!.reverse()

            //            mFavoritesRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    override fun onHistoryTranslatedItemsChanged() {
        mMainHandler!!.post {
            mHistoryTranslatedItems!!.clear()
            mHistoryTranslatedItems!!.addAll(mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY))
            mHistoryTranslatedItems!!.reverse()
        }
    }

    override fun onTranslatedItemsChanged() {
        mFavoritesTranslatedItems!!.clear()
        mCopyFavoritesTranslatedItems?.let {
            mFavoritesTranslatedItems!!.addAll(it)
        }
        chooseCurView()
        mFavoritesRecycler.adapter?.notifyDataSetChanged()
    }

//    override fun onContextItemSelected(item: MenuItem?): Boolean {
//        if (item!!.groupId == UNIQUE_FAVORITES_FRAGMENT_ID) {
//            return when (item.itemId) {
//                R.id.menu_item_delete -> {
//                    performContextItemDeletion()
//                    chooseCurView()
//                    chooseClearStoredVisibility()
//                    UIUtils.showToast(context, "item was longclicked contextmenu in favorites")
//                    true
//                }
//                else -> super.onContextItemSelected(item)
//            }
//        }
//        return super.onContextItemSelected(item)
//    }

    private fun chooseClearStoredVisibility() {
        if (!mFavoritesTranslatedItems!!.isEmpty()) {
            mClearStored!!.visibility = View.VISIBLE
        } else {
            mClearStored!!.visibility = View.INVISIBLE
        }
    }

    private fun performContextItemDeletion() {
        val adapter = mFavoritesRecycler.adapter as StoredRecyclerAdapter
        val position = adapter.position
        val item = mFavoritesTranslatedItems!![position]
        mRepository!!.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item)
        item.isFavoriteUp(false)
        mRepository!!.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item)
        mFavoritesTranslatedItems!!.removeAt(position)
        mFavoritesRecycler.adapter?.notifyItemRemoved(position)
    }
}
