package com.romankaranchuk.translator.ui.stored.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;

import com.romankaranchuk.translator.common.Constants;
import com.romankaranchuk.translator.data.database.model.TranslatedItem;
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository;
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository;
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl;
import com.romankaranchuk.translator.utils.ContentManager;
import com.romankaranchuk.translator.data.database.TablePersistenceContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by roman on 29.6.17.
 */

public class HistoryPresenterImpl implements HistoryContract.HistoryPresenter,
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        TranslatorRepositoryImpl.FavoritesTranslatedItemsRepositoryObserver,
        ContentManager.TranslatedItemChanged {
    private static final String MAIN_HANDLER_THREAD = HistoryPresenterImpl.class.getName() + ".MAIN_HANDLER_THREAD";

    private TranslatorRepositoryImpl mRepository;
    private List<TranslatedItem> mHistoryTranslatedItems = new ArrayList<>();
    private List<TranslatedItem> mFavoritesTranslatedItems = new ArrayList<>();
    private Handler mMainHandler;
    private HandlerThread mMainHandlerThread;
    private SharedPreferences mSettings;
    private ContentManager mContentManager;

    private HistoryFragment mView;

    public HistoryPresenterImpl(Context context) {
        TranslatorRepository localDataSource = TranslatorLocalRepository.getInstance(context);
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);

        mSettings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        mContentManager = ContentManager.getInstance();
    }

    @Override
    public void attachView(HistoryFragment fragment) {
        mView = (HistoryFragment) fragment;

        mRepository.addHistoryContentObserver(this);
        mRepository.addFavoritesContentObserver(this);
        mContentManager.addContentObserver(this);

        mMainHandlerThread = new HandlerThread(MAIN_HANDLER_THREAD);
        mMainHandlerThread.start();
        mMainHandler = new Handler(mMainHandlerThread.getLooper());

        mMainHandler.post(() -> {
            mHistoryTranslatedItems = mRepository.getTranslatedItems(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);
            mFavoritesTranslatedItems = mRepository.getTranslatedItems(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES);
            Collections.reverse(mHistoryTranslatedItems);
        });
    }

    @Override
    public void detachView() {
        mRepository.removeHistoryContentObserver(this);
        mRepository.removeFavoritesContentObserver(this);
        mContentManager.removeContentObserver(this);
        mRepository = null;
        mHistoryTranslatedItems = null;
        mFavoritesTranslatedItems = null;
        mSettings = null;
        mContentManager = null;
        mView = null;

        mMainHandlerThread.quit();
        mMainHandlerThread = null;
        mMainHandler = null;
    }

    @Override
    public List<TranslatedItem> getHistoryTranslatedItems() {
        return mHistoryTranslatedItems;
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mHistoryTranslatedItems.clear();
            mHistoryTranslatedItems.addAll(mRepository.getTranslatedItems(
                    TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
            Collections.reverse(mHistoryTranslatedItems);
//            mHistoryRecycler.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void onFavoritesTranslatedItemsChanged() {
        mMainHandler.post(() -> {
            mFavoritesTranslatedItems.clear();
            mFavoritesTranslatedItems.addAll(mRepository.getTranslatedItems(
                    TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES));
            Collections.reverse(mFavoritesTranslatedItems);
        });
    }

    @Override
    public void clickOnSetFavoriteItem(TranslatedItem item) {
        if (item.isFavorite()){
            item.isFavoriteUp(false);
            mRepository.deleteTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
        } else {
            item.isFavoriteUp(true);
            mRepository.saveTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES,item);
        }
        mRepository.updateTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, item);
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
                    mRepository.getTranslatedItems(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
            Collections.reverse(mHistoryTranslatedItems);
            mView.chooseCurView();
            mView.mHistoryRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void performContextItemDeletion(int position) {
        final TranslatedItem item = mHistoryTranslatedItems.get(position);
        mRepository.deleteTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY,item);
        mHistoryTranslatedItems.remove(position);
    }

    @Override
    public void clickOnItemStoredRecycler(TranslatedItem item) {
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Constants.EDITTEXT_DATA, item.getSrcMeaning());
        editor.putString(Constants.SRC_LANG, item.getSrcLanguageForUser());
        editor.putString(Constants.TRG_LANG, item.getTrgLanguageForUser());
        editor.putString(Constants.TRANSL_RESULT, item.getTrgMeaning());
        editor.putString(Constants.TRANSL_CONTENT, item.getDictDefinitionJSON());
        editor.putString(Constants.CUR_SELECTED_ITEM_SRC_LANG, item.getSrcLanguageForAPI());
        editor.putString(Constants.CUR_SELECTED_ITEM_TRG_LANG, item.getTrgLanguageForAPI());
        editor.apply();
    }
}
