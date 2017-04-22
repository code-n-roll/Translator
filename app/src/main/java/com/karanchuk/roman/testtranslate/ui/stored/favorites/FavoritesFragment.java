package com.karanchuk.roman.testtranslate.ui.stored.favorites;

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
import android.widget.Toast;

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

public class FavoritesFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        ContentManager.TranslatedItemChanged
{
    private RecyclerView mFavoritesRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private SearchView mSearchViewFavorites;
    private ImageButton mButtonIsFavorite,mClearStored;
    private TranslatorRepository mRepository;
    private List<TranslatedItem> mFavoritesTranslatedItems,
                                    mHistoryTranslatedItems,
                                    mCopyFavoritesTranslatedItems;
    private Handler mMainHandler;
    private View mView, mEmptyView, mContentView, mEmptySearchView;
    private TextView mTextViewEmptyFavorites, mTextViewEmptySearch;
    private ImageView mImageViewEmptyFavorites, mImageViewEmptySearch;
    private ContentManager mContentManager;
    private SharedPreferences mSettings;



    public static int UNIQUE_FAVORITES_FRAGMENT_ID = 2;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainHandler = new Handler(getContext().getMainLooper());


        mSettings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        View parent = getParentFragment().getView();
        if (parent != null)
            mClearStored = (ImageButton) parent.findViewById(R.id.imagebutton_clear_stored);

        mContentManager = ContentManager.getInstance();
        mContentManager.addContentObserver(this);

//        mFavoritesTranslatedItems = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            mFavoritesTranslatedItems.add(i, new TranslatedItem("RU", "FR", "привет", "bonjour", "false", null));
//        }


        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addFavoritesContentObserver(this);
        mRepository.addHistoryContentObserver(this);

        mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
        mCopyFavoritesTranslatedItems = new ArrayList<>(mFavoritesTranslatedItems);

        Collections.reverse(mFavoritesTranslatedItems);
        Collections.reverse(mCopyFavoritesTranslatedItems);
        Collections.reverse(mHistoryTranslatedItems);

        mView = inflater.inflate(R.layout.content_favorites, container, false);

        mEmptyView =  mView.findViewById(R.id.include_content_favorites_empty_item_list);
        mContentView = mView.findViewById(R.id.include_content_favorites_full_item_list);
        mEmptySearchView = mView.findViewById(R.id.include_content_favorites_empty_search);
        mEmptySearchView.setVisibility(View.INVISIBLE);


        mTextViewEmptyFavorites = (TextView) mView.findViewById(R.id.textview_empty_item_list);
        mTextViewEmptyFavorites.setText(R.string.empty_favorites);
        mImageViewEmptyFavorites = (ImageView) mView.findViewById(R.id.imageview_empty_item_list);
        mImageViewEmptyFavorites.setImageResource(R.drawable.bookmark_black_shape_light512);

        mTextViewEmptySearch = (TextView) mView.findViewById(R.id.textview_empty_search);
        mTextViewEmptySearch.setText(R.string.empty_search);
        mImageViewEmptySearch = (ImageView) mView.findViewById(R.id.imageview_empty_search);
        mImageViewEmptySearch.setImageResource(R.drawable.bookmark_black_shape_light512);


        mButtonIsFavorite = (ImageButton) mView.findViewById(R.id.imagebutton_isfavorite_favorite_item);


        mLayoutManager = new LinearLayoutManager(mView.getContext());
        mFavoritesRecycler = (RecyclerView) mView.findViewById(R.id.favorites_items_list);
        mFavoritesRecycler.setLayoutManager(mLayoutManager);

        mSearchViewFavorites = (SearchView) mView.findViewById(R.id.search_view_favorites);
        mSearchViewFavorites.setIconifiedByDefault(false);
        mSearchViewFavorites.setQueryHint("Search in Favorites");
        mSearchViewFavorites.setOnQueryTextListener(this);


        mDividerItemDecoration = new DividerItemDecoration(
                mFavoritesRecycler.getContext(),
                RecyclerView.VERTICAL);
        mFavoritesRecycler.addItemDecoration(mDividerItemDecoration);


        final BottomNavigationView navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        final View translatorNavigationItem = navigation.findViewById(R.id.navigation_translate);



        StoredRecyclerAdapter.OnItemClickListener itemClickListener = new
            StoredRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick (TranslatedItem item){
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(EDITTEXT_DATA, item.getSrcMeaning());
                editor.putString(SRC_LANG, item.getSrcLanguageForUser());
                editor.putString(TRG_LANG, item.getTrgLanguageForUser());
                editor.putString(TRANSL_RESULT, item.getTrgMeaning());
                editor.putString(TRANSL_CONTENT, item.getDictDefinition());
                editor.putString(CUR_SELECTED_ITEM_SRC_LANG, item.getSrcLanguageForAPI());
                editor.putString(CUR_SELECTED_ITEM_TRG_LANG, item.getTrgLanguageForAPI());
                editor.apply();
                translatorNavigationItem.performClick();
                Toast.makeText(getContext(),"item was clicked in favorites", Toast.LENGTH_SHORT).show();
            }
        };

        StoredRecyclerAdapter.OnItemClickListener isFavoriteClickListener = new
            StoredRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TranslatedItem item) {
                if (item.isFavorite()){
                    item.isFavoriteUp(false);
                    mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
                    mFavoritesRecycler.getAdapter().notifyItemChanged(mFavoritesTranslatedItems.indexOf(item));
                } else {
                    item.isFavoriteUp(true);
                    mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
                    mFavoritesRecycler.getAdapter().notifyItemChanged(mFavoritesTranslatedItems.indexOf(item));
                }
                mRepository.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);

                Toast.makeText(getContext(),"isFavorite was clicked in favorites", Toast.LENGTH_SHORT).show();
            }
        };


        mFavoritesRecycler.setAdapter(
                new StoredRecyclerAdapter(mFavoritesTranslatedItems, itemClickListener,
                        isFavoriteClickListener, UNIQUE_FAVORITES_FRAGMENT_ID));

        registerForContextMenu(mFavoritesRecycler);

        chooseCurView();


        return mView;
    }

    public void chooseCurView(){
        if (!mFavoritesTranslatedItems.isEmpty()){
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
        unregisterForContextMenu(mFavoritesRecycler);
//        remove observers here is bad idea!
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        remove observers here is bad idea!
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRepository.removeHistoryContentObserver(this);
        mRepository.removeFavoritesContentObserver(this);
        mContentManager.removeContentObserver(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<TranslatedItem> newList = new ArrayList<>();
        for (TranslatedItem item : mFavoritesTranslatedItems){
            String srcMeaning = item.getSrcMeaning(),
                    trgMeaning = item.getTrgMeaning();
            if (srcMeaning.contains(newText) ||
                    trgMeaning.contains(newText)){
                newList.add(item);
            }
        }
        StoredRecyclerAdapter adapter = (StoredRecyclerAdapter)mFavoritesRecycler.getAdapter();
        adapter.setFilter(newList);

        chooseCurSearchView(newList);
        return true;
    }

    public void chooseCurSearchView(List<TranslatedItem> list){
        if (list.isEmpty()){
            mEmptySearchView.setVisibility(View.VISIBLE);
            mFavoritesRecycler.setVisibility(View.INVISIBLE);
        } else {
            mEmptySearchView.setVisibility(View.INVISIBLE);
            mFavoritesRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mCopyFavoritesTranslatedItems.clear();
                mCopyFavoritesTranslatedItems.addAll(mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES));
                Collections.reverse(mCopyFavoritesTranslatedItems);
            }
        });
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mHistoryTranslatedItems.clear();
                mHistoryTranslatedItems.addAll(mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY));
                Collections.reverse(mHistoryTranslatedItems);

            }
        });
    }

    @Override
    public void onTranslatedItemChanged() {
        if (mFavoritesRecycler != null) {
            mFavoritesTranslatedItems.clear();
            mFavoritesTranslatedItems.addAll(mCopyFavoritesTranslatedItems);
            chooseCurView();
            mFavoritesRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == UNIQUE_FAVORITES_FRAGMENT_ID) {
            switch (item.getItemId()) {
                case R.id.menu_item_delete:
                    performContextItemDeletion();
                    chooseCurView();
                    chooseClearStoredVisibility();
                    Toast.makeText(getContext(), "item was longclicked contextmenu in favorites", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    public void chooseClearStoredVisibility(){
        if (!mFavoritesTranslatedItems.isEmpty()){
            mClearStored.setVisibility(View.VISIBLE);
        } else {
            mClearStored.setVisibility(View.INVISIBLE);
        }
    }

    public void performContextItemDeletion(){
        StoredRecyclerAdapter adapter = (StoredRecyclerAdapter) mFavoritesRecycler.getAdapter();
        int position = adapter.getPosition();
        TranslatedItem item = mFavoritesTranslatedItems.get(position);
        mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
        item.isFavoriteUp(false);
        mRepository.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
        mFavoritesTranslatedItems.remove(position);
        mFavoritesRecycler.getAdapter().notifyItemRemoved(position);
    }
}
