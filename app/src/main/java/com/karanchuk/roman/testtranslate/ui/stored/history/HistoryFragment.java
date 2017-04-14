package com.karanchuk.roman.testtranslate.ui.stored.history;

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

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.stored.favorites.FavoritesRecyclerAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class HistoryFragment extends Fragment implements TranslatorRepository.TranslatedItemsRepositoryObserver{
    private RecyclerView mHistoryRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private SearchView mSearchViewHistory;
    private List<TranslatedItem> mTranslatedItems;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private View mView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainHandler = new Handler(getContext().getMainLooper());


        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addContentObserver(this);
        mTranslatedItems = mRepository.getTranslatedItems();
        Collections.reverse(mTranslatedItems);

        if (!mTranslatedItems.isEmpty()) {
            mView = inflater.inflate(R.layout.content_history, container, false);
        } else {
            mView = inflater.inflate(R.layout.content_empty_history, container, false);
            return mView;
        }



        mLayoutManager = new LinearLayoutManager(mView.getContext());
        mHistoryRecycler = (RecyclerView) mView.findViewById(R.id.history_items_list);
        mHistoryRecycler.setLayoutManager(mLayoutManager);

        mSearchViewHistory = (SearchView) mView.findViewById(R.id.search_view_history);
        mSearchViewHistory.setIconifiedByDefault(false);
        mSearchViewHistory.setQueryHint("Search in History");

        mDividerItemDecoration = new DividerItemDecoration(mHistoryRecycler.getContext(),
                RecyclerView.VERTICAL);
        mHistoryRecycler.addItemDecoration(mDividerItemDecoration);

//        mTranslatedItems = new ArrayList<>();

//        for (int i = 0; i < 100; i++)
//            mTranslatedItems.add(i, new TranslatedItem("RU","FR","привет", "bonjour", "false", null));

        FavoritesRecyclerAdapter.OnItemClickListener itemClickListener = new
                FavoritesRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick (TranslatedItem item){

                    }
                };
        mHistoryRecycler.setAdapter(new FavoritesRecyclerAdapter(mTranslatedItems, itemClickListener));


        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mRepository.removeContentObserver(this);
    }

    @Override
    public void onTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mTranslatedItems = mRepository.getTranslatedItems();
            }
        });
    }
}
