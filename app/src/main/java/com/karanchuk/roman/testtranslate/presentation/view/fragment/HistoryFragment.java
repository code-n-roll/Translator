package com.karanchuk.roman.testtranslate.presentation.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.presenter.HistoryPresenter;
import com.karanchuk.roman.testtranslate.presentation.presenter.impl.HistoryPresenterImpl;
import com.karanchuk.roman.testtranslate.presentation.view.HistoryView;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.StoredRecyclerAdapter;

import java.util.List;

import static com.karanchuk.roman.testtranslate.presentation.Constants.UNIQUE_HISTORY_FRAGMENT_ID;

/**
 * Created by roman on 9.4.17.
 */

public class HistoryFragment extends Fragment implements HistoryView,
        SearchView.OnQueryTextListener {
    private View mEmptyView;
    private View mContentView;
    private View mEmptySearchView;
    private TextView mTextViewEmptyContent;
    private TextView mTextViewEmptySearch;
    private ImageView mImageViewEmptyContent;
    private ImageView mImageViewEmptySearch;
    public RecyclerView mHistoryRecycler;
    private SearchView mSearchViewHistory;
    private ImageButton mClearStored;

    private HistoryPresenterImpl mPresenter;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setPresenter(new HistoryPresenterImpl(this, getContext()));
        mPresenter.subscribe();

        findViewsOnFragment(view);

        initViewsStartStates();
        initSearchViewHistory();
        initHistoryRecycler();

        chooseCurView();
    }

    private void initViewsStartStates(){
        View parentView = getParentFragment().getView();
        if (parentView != null) {
            mClearStored = parentView.findViewById(R.id.imagebutton_clear_stored);
        }
        mEmptySearchView.setVisibility(View.INVISIBLE);
        mTextViewEmptyContent.setText(R.string.empty_history);
        mTextViewEmptySearch.setText(R.string.empty_search);
        mImageViewEmptyContent.setImageResource(R.drawable.history_light512);
        mImageViewEmptySearch.setImageResource(R.drawable.history_light512);
    }

    private void initSearchViewHistory(){
        mSearchViewHistory.setIconifiedByDefault(false);
        mSearchViewHistory.setQueryHint("Search in History");
        mSearchViewHistory.setOnQueryTextListener(this);
        mSearchViewHistory.setVisibility(View.GONE);
    }

    private void initHistoryRecycler(){
        BottomNavigationView navigation = getActivity().findViewById(R.id.navigation);
        View translatorNavView = navigation.findViewById(R.id.navigation_translate);

        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryRecycler.addItemDecoration(new DividerItemDecoration(
                mHistoryRecycler.getContext(), RecyclerView.VERTICAL));
        mHistoryRecycler.setAdapter(new StoredRecyclerAdapter(
                mPresenter.getHistoryTranslatedItems(),
                (item)->clickOnItemStoredRecycler(item, translatorNavView),
                this::clickOnSetFavoriteItem,
                UNIQUE_HISTORY_FRAGMENT_ID));
        registerForContextMenu(mHistoryRecycler);
    }

    private void clickOnItemStoredRecycler(final TranslatedItem item, final View view){
        mPresenter.clickOnItemStoredRecycler(item);
        view.performClick();
    }

    private void clickOnSetFavoriteItem(final TranslatedItem item){
        mPresenter.clickOnSetFavoriteItem(item);
        mHistoryRecycler.getAdapter().
                notifyItemChanged(mPresenter.getHistoryTranslatedItems().indexOf(item));
    }

    private void findViewsOnFragment(View view){
        mEmptyView = view.findViewById(R.id.include_content_history_empty_item_list);
        mContentView = view.findViewById(R.id.include_content_history_full_item_list);
        mEmptySearchView = view.findViewById(R.id.include_content_history_empty_search);

        mTextViewEmptyContent = view.findViewById(R.id.textview_empty_item_list);
        mImageViewEmptyContent = view.findViewById(R.id.imageview_empty_item_list);

        mTextViewEmptySearch = view.findViewById(R.id.textview_empty_search);
        mImageViewEmptySearch = view.findViewById(R.id.imageview_empty_search);

        mHistoryRecycler = view.findViewById(R.id.history_items_list);
        mSearchViewHistory = view.findViewById(R.id.search_view_history);
    }

    public void chooseCurView(){
        if (!mPresenter.getHistoryTranslatedItems().isEmpty()){
            mEmptyView.setVisibility(View.GONE);
            mContentView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterForContextMenu(mHistoryRecycler);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }




    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<TranslatedItem> newList = mPresenter.getSearchedText(newText);

        StoredRecyclerAdapter adapter = (StoredRecyclerAdapter)mHistoryRecycler.getAdapter();
        adapter.setFilter(newList);
        handleShowingSearchView(newList);
        return true;
    }

    @Override
    public void handleShowingSearchView(final List<TranslatedItem> list){
        if (list.isEmpty()){
            mEmptySearchView.setVisibility(View.VISIBLE);
            mHistoryRecycler.setVisibility(View.INVISIBLE);
        } else {
            mEmptySearchView.setVisibility(View.INVISIBLE);
            mHistoryRecycler.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == UNIQUE_HISTORY_FRAGMENT_ID) {
            switch (item.getItemId()) {
                case R.id.menu_item_delete:
                    performContextItemDeletion();
                    chooseCurView();
                    handleShowClearStored();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void handleShowClearStored(){
        if (!mPresenter.getHistoryTranslatedItems().isEmpty()){
            mClearStored.setVisibility(View.VISIBLE);
        } else {
            mClearStored.setVisibility(View.INVISIBLE);
        }
    }

    private void performContextItemDeletion(){
        StoredRecyclerAdapter adapter = (StoredRecyclerAdapter) mHistoryRecycler.getAdapter();
        int position = adapter.getPosition();
        mPresenter.performContextItemDeletion(position);
        mHistoryRecycler.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void setPresenter(HistoryPresenter presenter) {
        mPresenter = (HistoryPresenterImpl) presenter;
    }
}


//        mSearchViewHistory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                try{
//                    if (hasFocus){
//                        mSearchViewHistory.setBackground(Drawable.createFromXml(
//                                getResources(),
//                                getResources().getLayout(R.layout.searchview_border_active)));
//                    } else {
//
//                    }
//                } catch (XmlPullParserException | IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        mSearchViewHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "clicked on searchview in history",Toast.LENGTH_SHORT).show();
//            }
//        });
//        mSearchViewHistory.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "clicked on searchview in history",Toast.LENGTH_SHORT).show();
//            }
//        });
