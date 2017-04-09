package com.karanchuk.roman.testtranslate.Favorites;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;

import java.util.ArrayList;

/**
 * Created by roman on 9.4.17.
 */

public class ContentFavoritesFragment extends Fragment {
    private RecyclerView mFavoritesRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TranslatedItem> mItems;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_favorites, container, false);


        mLayoutManager = new LinearLayoutManager(view.getContext());
        mFavoritesRecycler = (RecyclerView) view.findViewById(R.id.favorites_items_list);
        mFavoritesRecycler.setLayoutManager(mLayoutManager);

        mItems = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            mItems.add(i, new TranslatedItem("RU","FR","привет", "bonjour", false));

        FavoritesRecyclerAdapter.OnItemClickListener itemClickListener = new
            FavoritesRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick (TranslatedItem item){

            }
        };
        mFavoritesRecycler.setAdapter(new FavoritesRecyclerAdapter(mItems, itemClickListener));


        return view;
    }
}
