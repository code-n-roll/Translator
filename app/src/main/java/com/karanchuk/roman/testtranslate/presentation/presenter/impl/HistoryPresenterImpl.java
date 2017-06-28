package com.karanchuk.roman.testtranslate.presentation.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.presenter.HistoryPresenter;
import com.karanchuk.roman.testtranslate.presentation.view.HistoryView;
import com.karanchuk.roman.testtranslate.presentation.view.fragment.HistoryFragment;
import com.karanchuk.roman.testtranslate.utils.ContentManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_SELECTED_ITEM_SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_SELECTED_ITEM_TRG_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRG_LANG;

/**
 * Created by roman on 29.6.17.
 */

public class HistoryPresenterImpl implements HistoryPresenter,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepository.FavoritesTranslatedItemsRepositoryObserver,
        ContentManager.TranslatedItemChanged {
    private TranslatorRepository mRepository;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private List<TranslatedItem> mFavoritesTranslatedItems;
    private Handler mMainHandler;
    private SharedPreferences mSettings;
    private ContentManager mContentManager;

    private HistoryFragment mView;

    public HistoryPresenterImpl(HistoryView view, Context context) {
        mView = (HistoryFragment) view;
        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(context);
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);
        mFavoritesTranslatedItems = mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES);
        Collections.reverse(mHistoryTranslatedItems);

        mMainHandler = new Handler(context.getMainLooper());
        mSettings = context.getSharedPreferences(PREFS_NAME, 0);
        mContentManager = ContentManager.getInstance();
    }

    @Override
    public void subscribe() {
        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);
        mContentManager.addContentObserver(this);
    }

    @Override
    public void unsubscribe() {
        mRepository.removeHistoryContentObserver(this);
        mRepository.removeFavoritesContentObserver(this);
        mContentManager.removeContentObserver(this);
        mRepository = null;
        mHistoryTranslatedItems = null;
        mFavoritesTranslatedItems = null;
        mMainHandler = null;
        mSettings = null;
        mContentManager = null;
        mView = null;
    }

    public List<TranslatedItem> getHistoryTranslatedItems() {
        return mHistoryTranslatedItems;
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mHistoryTranslatedItems.clear();
            mHistoryTranslatedItems.addAll(mRepository.getTranslatedItems(
                    TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
            Collections.reverse(mHistoryTranslatedItems);
//            mHistoryRecycler.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mFavoritesTranslatedItems.clear();
            mFavoritesTranslatedItems.addAll(mRepository.getTranslatedItems(
                    TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES));
            Collections.reverse(mFavoritesTranslatedItems);
        });
    }

    @Override
    public void clickOnSetFavoriteItem(TranslatedItem item) {
        if (item.isFavorite()){
            item.isFavoriteUp(false);
            mRepository.deleteTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
        } else {
            item.isFavoriteUp(true);
            mRepository.saveTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
        }
        mRepository.updateTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, item);
    }

    @Override
    public List<TranslatedItem> getSearchedText(String newText) {
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
        return newList;
    }

    @Override
    public void onTranslatedItemsChanged() {
        if (mView != null && mView.mHistoryRecycler != null) {
            mHistoryTranslatedItems.clear();
            mHistoryTranslatedItems.addAll(
                    mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
            Collections.reverse(mHistoryTranslatedItems);
            mView.chooseCurView();
            mView.mHistoryRecycler.getAdapter().notifyDataSetChanged();
        }

    }

    @Override
    public void performContextItemDeletion(int position) {
        final TranslatedItem item = mHistoryTranslatedItems.get(position);
        mRepository.deleteTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY,item);
        mHistoryTranslatedItems.remove(position);
    }

    @Override
    public void clickOnItemStoredRecycler(TranslatedItem item) {
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(EDITTEXT_DATA, item.getSrcMeaning());
        editor.putString(SRC_LANG, item.getSrcLanguageForUser());
        editor.putString(TRG_LANG, item.getTrgLanguageForUser());
        editor.putString(TRANSL_RESULT, item.getTrgMeaning());
        editor.putString(TRANSL_CONTENT, item.getDictDefinition());
        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, item.getSrcLanguageForAPI());
        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, item.getTrgLanguageForAPI());
        editor.apply();
    }
}
