package com.karanchuk.roman.testtranslate.ui.stored.favorites;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
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
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;

import static com.karanchuk.roman.testtranslate.ui.main.MainActivity.STORED_FRAGMENT;

/**
 * Created by roman on 9.4.17.
 */

public class FavoritesFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver
{
    private RecyclerView mFavoritesRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private SearchView mSearchViewFavorites;
    private ImageButton mButtonIsFavorite;
    private TranslatorRepository mRepository;
    private List<TranslatedItem> mFavoritesTranslatedItems,
                                    mHistoryTranslatedItems;
    private Handler mMainHandler;
    private View mView;
    private TextView mTextViewEmptyFavorites;
    private ImageView mImageViewEmptyFavorites;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainHandler = new Handler(getContext().getMainLooper());

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
        Collections.reverse(mFavoritesTranslatedItems);

        if (!mFavoritesTranslatedItems.isEmpty()) {
            mView = inflater.inflate(R.layout.content_favorites, container, false);
        } else {
            mView = inflater.inflate(R.layout.content_empty_item_list, container, false);
            mTextViewEmptyFavorites = (TextView) mView.findViewById(R.id.textview_empty_item_list);
            mTextViewEmptyFavorites.setText(R.string.empty_favorites);
            mImageViewEmptyFavorites = (ImageView) mView.findViewById(R.id.imageview_empty_item_list);
            mImageViewEmptyFavorites.setImageResource(R.drawable.bookmark_black_shape_light512);

            return mView;
        }


        mButtonIsFavorite = (ImageButton) mView.findViewById(R.id.imagebutton_isfavorite_favorite_item);

        mLayoutManager = new LinearLayoutManager(mView.getContext());
        mFavoritesRecycler = (RecyclerView) mView.findViewById(R.id.favorites_items_list);
        mFavoritesRecycler.setLayoutManager(mLayoutManager);

        mSearchViewFavorites = (SearchView) mView.findViewById(R.id.search_view_favorites);
        mSearchViewFavorites.setIconifiedByDefault(false);
        mSearchViewFavorites.setQueryHint("Search in Favorites");
        mSearchViewFavorites.setOnQueryTextListener(this);


        mDividerItemDecoration = new DividerItemDecoration(mFavoritesRecycler.getContext(),
                RecyclerView.VERTICAL);
        mFavoritesRecycler.addItemDecoration(mDividerItemDecoration);


        SearchListRecyclerAdapter.OnItemClickListener itemClickListener = new
            SearchListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick (TranslatedItem item){

                Toast.makeText(getContext(),"item was clicked in favorites", Toast.LENGTH_SHORT).show();
            }
        };

        SearchListRecyclerAdapter.OnItemClickListener isFavoriteClickListener = new
            SearchListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TranslatedItem item) {
                if (item.isFavorite()){
                    item.isFavoriteUp(false);
                    mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
                } else {
                    item.isFavoriteUp(true);
                    mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
                }
                mRepository.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
                mFavoritesRecycler.getAdapter().notifyDataSetChanged();
                Toast.makeText(getContext(),"isFavorite was clicked in favorites", Toast.LENGTH_SHORT).show();
            }
        };

        mFavoritesRecycler.setAdapter(
                new SearchListRecyclerAdapter(mFavoritesTranslatedItems, itemClickListener, isFavoriteClickListener));



        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRepository.removeFavoritesContentObserver(this);
        mRepository.removeHistoryContentObserver(this);
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
        SearchListRecyclerAdapter adapter = (SearchListRecyclerAdapter)mFavoritesRecycler.getAdapter();
        adapter.setFilter(newList);
        return true;
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

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
            }
        });
    }
}
