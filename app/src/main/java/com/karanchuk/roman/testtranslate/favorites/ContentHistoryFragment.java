package com.karanchuk.roman.testtranslate.favorites;

import android.os.Bundle;
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

import java.util.ArrayList;

/**
 * Created by roman on 9.4.17.
 */

public class ContentHistoryFragment extends Fragment {
    private RecyclerView mHistoryRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TranslatedItem> mItems;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private SearchView mSearchViewHistory;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_history, container, false);


        mLayoutManager = new LinearLayoutManager(view.getContext());
        mHistoryRecycler = (RecyclerView) view.findViewById(R.id.history_items_list);
        mHistoryRecycler.setLayoutManager(mLayoutManager);

        mSearchViewHistory = (SearchView) view.findViewById(R.id.search_view_history);
        mSearchViewHistory.setIconifiedByDefault(false);
        mSearchViewHistory.setQueryHint("Search in History");

        mDividerItemDecoration = new DividerItemDecoration(mHistoryRecycler.getContext(),
                RecyclerView.VERTICAL);
        mHistoryRecycler.addItemDecoration(mDividerItemDecoration);

        mItems = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            mItems.add(i, new TranslatedItem("RU","FR","привет", "bonjour", false, null));

        FavoritesRecyclerAdapter.OnItemClickListener itemClickListener = new
                FavoritesRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick (TranslatedItem item){

                    }
                };
        mHistoryRecycler.setAdapter(new FavoritesRecyclerAdapter(mItems, itemClickListener));


        return view;
    }

}
