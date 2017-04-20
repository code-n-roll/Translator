package com.karanchuk.roman.testtranslate.ui.stored.history;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.stored.favorites.SearchListRecyclerAdapter;
import com.karanchuk.roman.testtranslate.utils.ContentManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class HistoryFragment extends Fragment implements
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver,
        SearchView.OnQueryTextListener,
        ContentManager.TranslatedItemChanged
{
    private RecyclerView mHistoryRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private SearchView mSearchViewHistory;
    private List<TranslatedItem> mHistoryTranslatedItems,
                                mFavoritesTranslatedItems;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private View mView, mEmptyView, mContentView;
    private TextView mTextViewEmptyHistory;
    private ImageView mImageViewEmptyHistory;
    private ContentManager mContentManager;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainHandler = new Handler(getContext().getMainLooper());

        mContentManager = ContentManager.getInstance();
        mContentManager.addContentObserver(this);

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);

        mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
        mFavoritesTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES);
        Collections.reverse(mHistoryTranslatedItems);

        mView = inflater.inflate(R.layout.content_history, container, false);
        mEmptyView = mView.findViewById(R.id.include_content_history_empty_item_list);
        mContentView = mView.findViewById(R.id.include_content_history_full_item_list);

        mTextViewEmptyHistory = (TextView) mView.findViewById(R.id.textview_empty_item_list);
        mTextViewEmptyHistory.setText(R.string.empty_history);
        mImageViewEmptyHistory = (ImageView) mView.findViewById(R.id.imageview_empty_item_list);
        mImageViewEmptyHistory.setImageResource(R.drawable.history_light512);

        mLayoutManager = new LinearLayoutManager(mView.getContext());
        mHistoryRecycler = (RecyclerView) mView.findViewById(R.id.history_items_list);
        mHistoryRecycler.setLayoutManager(mLayoutManager);

        mSearchViewHistory = (SearchView) mView.findViewById(R.id.search_view_history);
        mSearchViewHistory.setIconifiedByDefault(false);
        mSearchViewHistory.setQueryHint("Search in History");
        mSearchViewHistory.setOnQueryTextListener(this);

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

        mDividerItemDecoration = new DividerItemDecoration(mHistoryRecycler.getContext(),
                RecyclerView.VERTICAL);
        mHistoryRecycler.addItemDecoration(mDividerItemDecoration);

//        mHistoryTranslatedItems = new ArrayList<>();
//        for (int i = 0; i < 100; i++)
//            mHistoryTranslatedItems.add(i, new TranslatedItem("RU","FR","привет", "bonjour", "false", null));

        SearchListRecyclerAdapter.OnItemClickListener itemClickListener = new
                SearchListRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick (TranslatedItem item){

                        Toast.makeText(getContext(),"item was clicked in history", Toast.LENGTH_SHORT).show();
                    }
                };
        SearchListRecyclerAdapter.OnItemClickListener isFavoriteClickListener = new
                SearchListRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(TranslatedItem item) {
                        if (item.isFavorite()){
                            item.isFavoriteUp(false);
                            mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
                        } else {
                            item.isFavoriteUp(true);
                            mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
                        }
                        mRepository.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
                        mHistoryRecycler.getAdapter().notifyItemChanged(mHistoryTranslatedItems.indexOf(item));

                        Toast.makeText(getContext(),"isFavorite was clicked in history", Toast.LENGTH_SHORT).show();
                    }
                };
        mHistoryRecycler.setAdapter(
                new SearchListRecyclerAdapter(mHistoryTranslatedItems, itemClickListener, isFavoriteClickListener));

        chooseCurView();

        return mView;
    }

    public void chooseCurView(){
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
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mFavoritesTranslatedItems.clear();
                mFavoritesTranslatedItems.addAll(mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_FAVORITES));
                Collections.reverse(mFavoritesTranslatedItems);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<TranslatedItem> newList = new ArrayList<>();
        for (TranslatedItem item : mHistoryTranslatedItems){
            String srcMeaning = item.getSrcMeaning().toLowerCase(),
                    trgMeaning = item.getTrgMeaning().toLowerCase();
            if (srcMeaning.contains(newText) ||
                    trgMeaning.contains(newText)){
                newList.add(item);
            }
        }
        SearchListRecyclerAdapter adapter = (SearchListRecyclerAdapter)mHistoryRecycler.getAdapter();
        adapter.setFilter(newList);
        return true;
    }


    @Override
    public void onTranslatedItemChanged() {
        if (mHistoryRecycler != null) {
            mHistoryTranslatedItems.clear();
            mHistoryTranslatedItems.addAll(mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY));
            Collections.reverse(mHistoryTranslatedItems);
            chooseCurView();
            mHistoryRecycler.getAdapter().notifyDataSetChanged();
        }
    }
}
