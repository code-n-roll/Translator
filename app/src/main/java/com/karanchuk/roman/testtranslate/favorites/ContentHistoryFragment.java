package com.karanchuk.roman.testtranslate.favorites;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class ContentHistoryFragment extends Fragment implements TranslatorRepository.TranslatedItemsRepositoryObserver{
    private RecyclerView mHistoryRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TranslatedItem> mTranslatedItems;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private SearchView mSearchViewHistory;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_history, container, false);

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addContentObserver(this);
        mTranslatedItems = mRepository.getTranslatedItems();
        Collections.reverse(mTranslatedItems);

        mMainHandler = new Handler(getContext().getMainLooper());

        mLayoutManager = new LinearLayoutManager(view.getContext());
        mHistoryRecycler = (RecyclerView) view.findViewById(R.id.history_items_list);
        mHistoryRecycler.setLayoutManager(mLayoutManager);

        mSearchViewHistory = (SearchView) view.findViewById(R.id.search_view_history);
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


        return view;
    }

    @Override
    public void onTranslatedItemsChanged() {

    }
}
