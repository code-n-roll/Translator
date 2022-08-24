package com.romankaranchuk.translator.ui.stored.history

import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.common.Constants.UNIQUE_HISTORY_FRAGMENT_ID
import com.romankaranchuk.translator.data.database.model.TranslatedItem
import com.romankaranchuk.translator.ui.stored.StoredFragment
import com.romankaranchuk.translator.ui.stored.StoredRecyclerAdapter

class HistoryFragment : Fragment(), HistoryContract.HistoryView, SearchView.OnQueryTextListener {

    private var mEmptyView: View? = null
    private var mContentView: View? = null
    private var mEmptySearchView: View? = null
    private var mTextViewEmptyContent: TextView? = null
    private var mTextViewEmptySearch: TextView? = null
    private var mImageViewEmptyContent: ImageView? = null
    private var mImageViewEmptySearch: ImageView? = null
    @JvmField var mHistoryRecycler: RecyclerView? = null
    private var mSearchViewHistory: SearchView? = null
    private var mClearStored: ImageButton? = null

    private var mPresenter: HistoryContract.HistoryPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPresenter = (parentFragment as StoredFragment).mPresenter

        mPresenter?.attachView(this)
        findViewsOnFragment(view)
        initViewsStartStates()
        initSearchViewHistory()
        initHistoryRecycler()
        chooseCurView()
    }

    private fun initViewsStartStates() {
        val parentView = parentFragment?.view
        if (parentView != null) {
            mClearStored = parentView.findViewById(R.id.imagebutton_clear_stored)
        }
        mEmptySearchView!!.visibility = View.INVISIBLE
        mTextViewEmptyContent!!.setText(R.string.empty_history)
        mTextViewEmptySearch!!.setText(R.string.empty_search)
        mImageViewEmptyContent!!.setImageResource(R.drawable.history_light512)
        mImageViewEmptySearch!!.setImageResource(R.drawable.history_light512)
    }

    private fun initSearchViewHistory() {
        mSearchViewHistory!!.setIconifiedByDefault(false)
        mSearchViewHistory!!.queryHint = "Search in History"
        mSearchViewHistory!!.setOnQueryTextListener(this)
        mSearchViewHistory!!.visibility = View.GONE
    }

    private fun initHistoryRecycler() {
//        AHBottomNavigation navigation = getActivity().findViewById(R.id.navigation);
//        View translatorNavView = navigation.findViewById(R.id.navigation_translate);
        mHistoryRecycler!!.layoutManager = LinearLayoutManager(context)
        val verticalItemDecoration: ItemDecoration =
            DividerItemDecoration(mHistoryRecycler!!.context, RecyclerView.VERTICAL)
        mHistoryRecycler!!.addItemDecoration(verticalItemDecoration)
        mHistoryRecycler!!.adapter =
            StoredRecyclerAdapter(
                mPresenter?.historyTranslatedItems,
                { item -> clickOnItemStoredRecycler(item /*,translatorNavView*/) },
                this::clickOnSetFavoriteItem,
                UNIQUE_HISTORY_FRAGMENT_ID
            )
        registerForContextMenu(mHistoryRecycler!!)
    }

    private fun clickOnItemStoredRecycler(item: TranslatedItem) {
        mPresenter?.clickOnItemStoredRecycler(item)
//        view.performClick()
    }

    private fun clickOnSetFavoriteItem(item: TranslatedItem) {
        mPresenter?.clickOnSetFavoriteItem(item)
        mHistoryRecycler!!.adapter!!.notifyItemChanged(
            mPresenter?.historyTranslatedItems!!.indexOf(
                item
            )
        )
    }

    private fun findViewsOnFragment(view: View) {
        mEmptyView = view.findViewById(R.id.include_content_history_empty_item_list)
        mContentView = view.findViewById(R.id.include_content_history_full_item_list)
        mEmptySearchView = view.findViewById(R.id.include_content_history_empty_search)
        mTextViewEmptyContent = view.findViewById(R.id.textview_empty_item_list)
        mImageViewEmptyContent = view.findViewById(R.id.imageview_empty_item_list)
        mTextViewEmptySearch = view.findViewById(R.id.textview_empty_search)
        mImageViewEmptySearch = view.findViewById(R.id.imageview_empty_search)
        mHistoryRecycler = view.findViewById(R.id.history_items_list)
        mSearchViewHistory = view.findViewById(R.id.search_view_history)
    }

    fun chooseCurView() {
        if (!mPresenter?.historyTranslatedItems!!.isEmpty()) {
            mEmptyView!!.visibility = View.GONE
            mContentView!!.visibility = View.VISIBLE
        } else {
            mEmptyView!!.visibility = View.VISIBLE
            mContentView!!.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterForContextMenu(mHistoryRecycler!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter?.detachView()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val newList = mPresenter?.getSearchedText(newText) ?: emptyList()
        val adapter = mHistoryRecycler!!.adapter as StoredRecyclerAdapter?
        adapter!!.setFilter(newList)
        handleShowingSearchView(newList)
        return true
    }

    override fun handleShowingSearchView(list: List<TranslatedItem>) {
        if (list.isEmpty()) {
            mEmptySearchView!!.visibility = View.VISIBLE
            mHistoryRecycler!!.visibility = View.INVISIBLE
        } else {
            mEmptySearchView!!.visibility = View.INVISIBLE
            mHistoryRecycler!!.visibility = View.VISIBLE
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return if (item.groupId == UNIQUE_HISTORY_FRAGMENT_ID) {
            when (item.itemId) {
                R.id.menu_item_delete -> {
                    performContextItemDeletion()
                    chooseCurView()
                    handleShowClearStored()
                    true
                }
                else -> super.onContextItemSelected(item)
            }
        } else super.onContextItemSelected(
            item
        )
    }

    override fun handleShowClearStored() {
        if (!mPresenter?.historyTranslatedItems!!.isEmpty()) {
            mClearStored!!.visibility = View.VISIBLE
        } else {
            mClearStored!!.visibility = View.INVISIBLE
        }
    }

    private fun performContextItemDeletion() {
        val adapter = mHistoryRecycler!!.adapter as StoredRecyclerAdapter?
        val position = adapter!!.position
        mPresenter?.performContextItemDeletion(position)
        mHistoryRecycler!!.adapter!!.notifyItemRemoved(position)
    }
}