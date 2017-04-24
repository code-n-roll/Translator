package com.karanchuk.roman.testtranslate.ui.stored.history;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.stored.StoredRecyclerAdapter;
import com.karanchuk.roman.testtranslate.utils.ContentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.karanchuk.roman.testtranslate.ui.source_lang.SourceLangActivity.CUR_SELECTED_ITEM_SRC_LANG;
import static com.karanchuk.roman.testtranslate.ui.target_lang.TargetLangActivity.CUR_SELECTED_ITEM_TRG_LANG;
import static com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment.SRC_LANG;
import static com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment.TRG_LANG;

/**
 * Created by roman on 9.4.17.
 */

public class HistoryFragment extends Fragment implements
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver,
        SearchView.OnQueryTextListener,
        ContentManager.TranslatedItemChanged
{
    private View mEmptyView;
    private View mContentView;
    private View mEmptySearchView;
    private View mView;
    private TextView mTextViewEmptyContent;
    private TextView mTextViewEmptySearch;
    private ImageView mImageViewEmptyContent;
    private ImageView mImageViewEmptySearch;
    private RecyclerView mHistoryRecycler;
    private SearchView mSearchViewHistory;
    private ImageButton mClearStored;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;

    private List<TranslatedItem> mHistoryTranslatedItems,
                                 mFavoritesTranslatedItems;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private ContentManager mContentManager;
    private SharedPreferences mSettings;

    public final static int UNIQUE_HISTORY_FRAGMENT_ID = 1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainHandler = new Handler(getContext().getMainLooper());

        mSettings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        View parentView = getParentFragment().getView();
        if (parentView != null)
            mClearStored = (ImageButton) parentView.findViewById(R.id.imagebutton_clear_stored);


        mContentManager = ContentManager.getInstance();

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
        mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
        Collections.reverse(mHistoryTranslatedItems);

        mView = inflater.inflate(R.layout.content_history, container, false);

        findViewsOnFragment();


        mEmptySearchView.setVisibility(View.INVISIBLE);
        mTextViewEmptyContent.setText(R.string.empty_history);
        mTextViewEmptySearch.setText(R.string.empty_search);
        mImageViewEmptyContent.setImageResource(R.drawable.history_light512);
        mImageViewEmptySearch.setImageResource(R.drawable.history_light512);

        mLayoutManager = new LinearLayoutManager(mView.getContext());
        mHistoryRecycler.setLayoutManager(mLayoutManager);
        mSearchViewHistory.setIconifiedByDefault(false);
        mSearchViewHistory.setQueryHint("Search in History");
        mSearchViewHistory.setOnQueryTextListener(this);
        mSearchViewHistory.setVisibility(View.GONE);

        final BottomNavigationView navigation = (BottomNavigationView)
                getActivity().findViewById(R.id.navigation);
        final View translatorNavView = navigation.findViewById(R.id.navigation_translate);

        mDividerItemDecoration =
                new DividerItemDecoration(mHistoryRecycler.getContext(), RecyclerView.VERTICAL);
        mHistoryRecycler.addItemDecoration(mDividerItemDecoration);

//        mHistoryTranslatedItems = new ArrayList<>();
//        for (int i = 0; i < 100; i++)
//            mHistoryTranslatedItems.add(i, new TranslatedItem("RU","FR","привет", "bonjour", "false", null));


        mHistoryRecycler.setAdapter(new StoredRecyclerAdapter(
                        mHistoryTranslatedItems,
                        (item)->clickOnItemStoredRecycler(item,translatorNavView),
                        (item)->clickOnSetFavoriteItem(item),
                        UNIQUE_HISTORY_FRAGMENT_ID));
        registerForContextMenu(mHistoryRecycler);

        chooseCurView();

        return mView;
    }

    private void clickOnItemStoredRecycler(final TranslatedItem item, final View view){
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(EDITTEXT_DATA, item.getSrcMeaning());
        editor.putString(SRC_LANG, item.getSrcLanguageForUser());
        editor.putString(TRG_LANG, item.getTrgLanguageForUser());
        editor.putString(TRANSL_RESULT, item.getTrgMeaning());
        editor.putString(TRANSL_CONTENT, item.getDictDefinition());
        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, item.getSrcLanguageForAPI());
        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, item.getTrgLanguageForAPI());
        editor.apply();
        view.performClick();
//                        TranslatorStateHolder.getInstance().notifyShowSelectedItem();
//        UIUtils.showToast(getContext(), "item was clicked in history");
    }

    private void clickOnSetFavoriteItem(final TranslatedItem item){
        if (item.isFavorite()){
            item.isFavoriteUp(false);
            mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
        } else {
            item.isFavoriteUp(true);
            mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
        }
        mRepository.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
        mHistoryRecycler.getAdapter().notifyItemChanged(mHistoryTranslatedItems.indexOf(item));

//        UIUtils.showToast(getContext(), "isFavorite was clicked in history");
    }

    private void findViewsOnFragment(){

        mEmptyView = mView.findViewById(R.id.include_content_history_empty_item_list);
        mContentView = mView.findViewById(R.id.include_content_history_full_item_list);
        mEmptySearchView = mView.findViewById(R.id.include_content_history_empty_search);

        mTextViewEmptyContent = (TextView) mView.findViewById(R.id.textview_empty_item_list);
        mImageViewEmptyContent = (ImageView) mView.findViewById(R.id.imageview_empty_item_list);

        mTextViewEmptySearch = (TextView) mView.findViewById(R.id.textview_empty_search);
        mImageViewEmptySearch = (ImageView) mView.findViewById(R.id.imageview_empty_search);

        mHistoryRecycler = (RecyclerView) mView.findViewById(R.id.history_items_list);
        mSearchViewHistory = (SearchView) mView.findViewById(R.id.search_view_history);
    }

    @Override
    public void onStart() {
        super.onStart();

        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);
        mContentManager.addContentObserver(this);

    }

    private void chooseCurView(){
        if (!mHistoryTranslatedItems.isEmpty()){
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

        mRepository.removeHistoryContentObserver(this);
        mRepository.removeFavoritesContentObserver(this);
        mContentManager.removeContentObserver(this);
    }


    @Override
    public void onHistoryTranslatedItemsChanged() {

        mMainHandler.post(() -> {
            mHistoryTranslatedItems.clear();
            mHistoryTranslatedItems.addAll(mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY));
            Collections.reverse(mHistoryTranslatedItems);

//            mHistoryRecycler.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mFavoritesTranslatedItems.clear();
            mFavoritesTranslatedItems.addAll(mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES));
            Collections.reverse(mFavoritesTranslatedItems);

        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
        newText = newText.toLowerCase();
        final ArrayList<TranslatedItem> newList = new ArrayList<>();
        for (TranslatedItem item : mHistoryTranslatedItems){
            final String srcMeaning = item.getSrcMeaning().toLowerCase();
            final String trgMeaning = item.getTrgMeaning().toLowerCase();
            if (srcMeaning.contains(newText) || trgMeaning.contains(newText)){
                newList.add(item);
            }
        }
        StoredRecyclerAdapter adapter = (StoredRecyclerAdapter)mHistoryRecycler.getAdapter();
        adapter.setFilter(newList);

        chooseCurSearchView(newList);
        return true;
    }

    private void chooseCurSearchView(final List<TranslatedItem> list){
        if (list.isEmpty()){
            mEmptySearchView.setVisibility(View.VISIBLE);
            mHistoryRecycler.setVisibility(View.INVISIBLE);
        } else {
            mEmptySearchView.setVisibility(View.INVISIBLE);
            mHistoryRecycler.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onTranslatedItemsChanged() {
        if (mHistoryRecycler != null) {
            mHistoryTranslatedItems.clear();
            mHistoryTranslatedItems.addAll(
                    mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY));
            Collections.reverse(mHistoryTranslatedItems);
            chooseCurView();
            mHistoryRecycler.getAdapter().notifyDataSetChanged();
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
                    chooseClearStoredVisibility();
//                    UIUtils.showToast(getContext(), "item was longclicked contextmenu in history");
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    private void chooseClearStoredVisibility(){
        if (!mHistoryTranslatedItems.isEmpty()){
            mClearStored.setVisibility(View.VISIBLE);
        } else {
            mClearStored.setVisibility(View.INVISIBLE);
        }
    }

    private void performContextItemDeletion(){
        final StoredRecyclerAdapter adapter = (StoredRecyclerAdapter) mHistoryRecycler.getAdapter();
        final int position = adapter.getPosition();
        final TranslatedItem item = mHistoryTranslatedItems.get(position);
        mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY,item);
        mHistoryTranslatedItems.remove(position);
        mHistoryRecycler.getAdapter().notifyItemRemoved(position);
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
