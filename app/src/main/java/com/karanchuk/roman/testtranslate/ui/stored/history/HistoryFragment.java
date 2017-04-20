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
    private SharedPreferences mSettings;
    private ImageButton mClearStored;

    public static int UNIQUE_HISTORY_FRAGMENT_ID = 1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainHandler = new Handler(getContext().getMainLooper());

        mSettings = getActivity().getSharedPreferences(PREFS_NAME, 0);


        View parent = getParentFragment().getView();
        if (parent != null)
            mClearStored = (ImageButton) parent.findViewById(R.id.imagebutton_clear_stored);


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

        final BottomNavigationView navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        final View translatorNavigationItem = navigation.findViewById(R.id.navigation_translate);

        mDividerItemDecoration = new DividerItemDecoration(mHistoryRecycler.getContext(),
                RecyclerView.VERTICAL);
        mHistoryRecycler.addItemDecoration(mDividerItemDecoration);

//        mHistoryTranslatedItems = new ArrayList<>();
//        for (int i = 0; i < 100; i++)
//            mHistoryTranslatedItems.add(i, new TranslatedItem("RU","FR","привет", "bonjour", "false", null));

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
                        editor.apply();
                        translatorNavigationItem.performClick();
                        Toast.makeText(getContext(),"item was clicked in history", Toast.LENGTH_SHORT).show();
                    }
                };
        StoredRecyclerAdapter.OnItemClickListener isFavoriteClickListener = new
                StoredRecyclerAdapter.OnItemClickListener() {
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
                new StoredRecyclerAdapter(mHistoryTranslatedItems,
                        itemClickListener,
                        isFavoriteClickListener,
                        UNIQUE_HISTORY_FRAGMENT_ID));
        registerForContextMenu(mHistoryRecycler);

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
        unregisterForContextMenu(mHistoryRecycler);
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
        StoredRecyclerAdapter adapter = (StoredRecyclerAdapter)mHistoryRecycler.getAdapter();
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
                    chooseClearStoredVisility();
                    Toast.makeText(getContext(), "item was longclicked contextmenu in history", Toast.LENGTH_SHORT).show();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    public void chooseClearStoredVisility(){
        if (!mHistoryTranslatedItems.isEmpty()){
            mClearStored.setVisibility(View.VISIBLE);
        } else {
            mClearStored.setVisibility(View.INVISIBLE);
        }
    }

    public void performContextItemDeletion(){
        StoredRecyclerAdapter adapter = (StoredRecyclerAdapter) mHistoryRecycler.getAdapter();
        int position = adapter.getPosition();
        mHistoryTranslatedItems.remove(position);
        mHistoryRecycler.getAdapter().notifyItemRemoved(position);
    }
}
