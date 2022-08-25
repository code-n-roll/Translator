package com.romankaranchuk.translator.ui.stored.favorites

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.common.Constants.*
import com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry
import com.romankaranchuk.translator.data.database.model.TranslatedItem
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository.getInstance
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl
import com.romankaranchuk.translator.databinding.FragmentFavoritesBinding
import com.romankaranchuk.translator.ui.stored.StoredRecyclerAdapter
import com.romankaranchuk.translator.utils.ContentManager


class FavoritesFragment : Fragment(),
        SearchView.OnQueryTextListener,
        TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver,
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        ContentManager.TranslatedItemChanged {

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

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMainHandler = Handler(Looper.getMainLooper())
        mSettings = requireActivity().getSharedPreferences(PREFS_NAME, 0)

        mView = view
        val parentView = requireParentFragment().view
        if (parentView != null) {
            mClearStored = parentView.findViewById(R.id.imagebutton_clear_stored)
        }
        mContentManager = ContentManager.getInstance()

        val localDataSource = getInstance(requireContext())
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource)
        mFavoritesTranslatedItems = mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES)
        mHistoryTranslatedItems = mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY)
        mCopyFavoritesTranslatedItems = ArrayList(mFavoritesTranslatedItems!!)

        mFavoritesTranslatedItems!!.reverse()
        mCopyFavoritesTranslatedItems!!.reverse()
        mHistoryTranslatedItems!!.reverse()

        binding.includeContentFavoritesFullItemList.includeContentFavoritesEmptySearch.root.visibility = View.INVISIBLE

        binding.includeContentFavoritesEmptyItemList.textviewEmptyItemList.setText(R.string.empty_favorites)
        binding.includeContentFavoritesEmptyItemList.imageviewEmptyItemList.setImageResource(R.drawable.bookmark_black_shape_light512)

        binding.includeContentFavoritesFullItemList.includeContentFavoritesEmptySearch.textviewEmptySearch.setText(R.string.empty_search)
        binding.includeContentFavoritesFullItemList.includeContentFavoritesEmptySearch.imageviewEmptySearch.setImageResource(R.drawable.bookmark_black_shape_light512)

        mLayoutManager = LinearLayoutManager(mView!!.context)
        binding.includeContentFavoritesFullItemList.favoritesItemsList.layoutManager = mLayoutManager

        binding.includeContentFavoritesFullItemList.searchViewFavorites.setIconifiedByDefault(false)
        binding.includeContentFavoritesFullItemList.searchViewFavorites.queryHint = "Search in Favorites"
        binding.includeContentFavoritesFullItemList.searchViewFavorites.setOnQueryTextListener(this)
        binding.includeContentFavoritesFullItemList.searchViewFavorites.visibility = View.GONE

        val mDividerItemDecoration = DividerItemDecoration(binding.includeContentFavoritesFullItemList.favoritesItemsList.context, RecyclerView.VERTICAL)
        binding.includeContentFavoritesFullItemList.favoritesItemsList.addItemDecoration(mDividerItemDecoration)

//        val navigation = activity!!.findViewById<AHBottomNavigation>(R.id.navigation)
//        val translatorNavigationItem = navigation.findViewById<View>(R.id.navigation_translate)

//        binding.includeContentFavoritesFullItemList.favoritesItemsList.adapter = StoredRecyclerAdapter(
//                mFavoritesTranslatedItems,
//                { item -> clickOnItemStoredRecycler(item, translatorNavigationItem) },
//                { item -> clickOnSetFavoriteItem(item) },
//                UNIQUE_FAVORITES_FRAGMENT_ID)
//
        registerForContextMenu(binding.includeContentFavoritesFullItemList.favoritesItemsList)

        chooseCurView()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun clickOnSetFavoriteItem(item: TranslatedItem) {
        if (item.isFavorite()) {
            item.isFavoriteUp(false)
            mRepository!!.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item)
            binding.includeContentFavoritesFullItemList.favoritesItemsList.adapter?.notifyItemChanged(mFavoritesTranslatedItems!!.indexOf(item))
        } else {
            item.isFavoriteUp(true)
            mRepository!!.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item)
            binding.includeContentFavoritesFullItemList.favoritesItemsList.adapter?.notifyItemChanged(mFavoritesTranslatedItems!!.indexOf(item))
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
            binding.includeContentFavoritesEmptyItemList.root.visibility = View.GONE
            binding.includeContentFavoritesFullItemList.root.visibility = View.VISIBLE
        } else {
            binding.includeContentFavoritesEmptyItemList.root.visibility = View.VISIBLE
            binding.includeContentFavoritesFullItemList.root.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterForContextMenu(binding.includeContentFavoritesFullItemList.favoritesItemsList)

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
        val adapter = binding.includeContentFavoritesFullItemList.favoritesItemsList.adapter as? StoredRecyclerAdapter
        adapter?.setFilter(newList)

        chooseCurSearchView(newList)
        return true
    }

    private fun chooseCurSearchView(list: List<TranslatedItem>) {
        if (list.isEmpty()) {
            binding.includeContentFavoritesFullItemList.includeContentFavoritesEmptySearch.root.visibility = View.VISIBLE
            binding.includeContentFavoritesFullItemList.favoritesItemsList.visibility = View.INVISIBLE
        } else {
            binding.includeContentFavoritesFullItemList.includeContentFavoritesEmptySearch.root.visibility = View.INVISIBLE
            binding.includeContentFavoritesFullItemList.favoritesItemsList.visibility = View.VISIBLE
        }
    }

    override fun onFavoritesTranslatedItemsChanged() {
        mMainHandler!!.post {
            mCopyFavoritesTranslatedItems!!.clear()
            mCopyFavoritesTranslatedItems!!.addAll(mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES))
            mCopyFavoritesTranslatedItems!!.reverse()

            //            binding.includeContentFavoritesFullItemList.favoritesItemsList.getAdapter().notifyDataSetChanged();
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
        binding.includeContentFavoritesFullItemList.favoritesItemsList.adapter?.notifyDataSetChanged()
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
        val adapter = binding.includeContentFavoritesFullItemList.favoritesItemsList.adapter as StoredRecyclerAdapter
        val position = adapter.position
        val item = mFavoritesTranslatedItems!![position]
        mRepository!!.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item)
        item.isFavoriteUp(false)
        mRepository!!.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item)
        mFavoritesTranslatedItems!!.removeAt(position)
        binding.includeContentFavoritesFullItemList.favoritesItemsList.adapter?.notifyItemRemoved(position)
    }
}
