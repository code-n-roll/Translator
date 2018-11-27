package com.karanchuk.roman.testtranslate.data.database.repository;

import android.support.annotation.NonNull;

import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatorRepositoryImpl implements TranslatorRepository {
    private static TranslatorRepositoryImpl INSTANCE = null;

    private final TranslatorRepository mTranslatorLocalDataSource;

    private final List<HistoryTranslatedItemsRepositoryObserver> mHistoryObservers = new ArrayList<>();
    private final List<FavoritesTranslatedItemsRepositoryObserver> mFavoritesObservers = new ArrayList<>();

    private Map<String, TranslatedItem> mHistoryCachedTranslatedItems,
                                        mFavoritesCachedTranslatedItems;

    private boolean mHistoryCacheTranslatedItemsIsDirty = true,
                    mFavoritesCacheTranslatedItemsIdDirty = true;

    public static TranslatorRepositoryImpl getInstance(final TranslatorRepository translatorLocalDataSource){
        if (INSTANCE == null){
            INSTANCE = new TranslatorRepositoryImpl(translatorLocalDataSource);
        }
        return INSTANCE;
    }

    private TranslatorRepositoryImpl(@NonNull final TranslatorRepository translatorLocalDataSource){
        mTranslatorLocalDataSource = checkNotNull(translatorLocalDataSource);
    }



    private List<TranslatedItem> getHistoryCachedTranslatedItems(){
        if (mHistoryCachedTranslatedItems == null ) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(mHistoryCachedTranslatedItems.values());
        }
    }


    public void addHistoryContentObserver(final HistoryTranslatedItemsRepositoryObserver observer){
        if (!mHistoryObservers.contains(observer)){
            mHistoryObservers.add(observer);
        }
    }

    public void removeHistoryContentObserver(final HistoryTranslatedItemsRepositoryObserver observer){
        if (mHistoryObservers.contains(observer)){
            mHistoryObservers.remove(observer);
        }
    }

    private void notifyHistoryTranslatedItemsChanged(){
        for (HistoryTranslatedItemsRepositoryObserver observer : mHistoryObservers){
            observer.onHistoryTranslatedItemsChanged();
        }
    }

    @Override
    public synchronized boolean saveTranslatedItem(@NonNull final String tableName,
                                      @NonNull final TranslatedItem translatedItem) {
        if (tableName.equals(TranslatedItemEntry.TABLE_NAME_HISTORY)){
            if (mHistoryCachedTranslatedItems == null){
                mHistoryCachedTranslatedItems = new LinkedHashMap<>();
            }

            if (!mHistoryCachedTranslatedItems.containsValue(translatedItem)) {
                mHistoryCachedTranslatedItems.put(translatedItem.getId(), translatedItem);
            } else {
                mHistoryCachedTranslatedItems.values().remove(translatedItem);
                mHistoryCachedTranslatedItems.put(translatedItem.getId(), translatedItem);
                mTranslatorLocalDataSource.deleteTranslatedItem(tableName, translatedItem);
            }
            mTranslatorLocalDataSource.saveTranslatedItem(tableName, translatedItem);
            notifyHistoryTranslatedItemsChanged();
        } else if (tableName.equals(TranslatedItemEntry.TABLE_NAME_FAVORITES)){
            if (mFavoritesCachedTranslatedItems == null){
                mFavoritesCachedTranslatedItems = new LinkedHashMap<>();
            }

            if (!mFavoritesCachedTranslatedItems.containsValue(translatedItem)){
                mFavoritesCachedTranslatedItems.put(translatedItem.getId(), translatedItem);
                mTranslatorLocalDataSource.saveTranslatedItem(tableName, translatedItem);
            }
            notifyFavoritesTranslatedItemsChanged();
        }
        return true;
    }



    @Override
    public void deleteTranslatedItem(@NonNull final String tableName,
                                     @NonNull final TranslatedItem translatedItem) {
        if (tableName.equals(TranslatedItemEntry.TABLE_NAME_HISTORY)) {
            mTranslatorLocalDataSource.deleteTranslatedItem(tableName, translatedItem);

            if (mHistoryCachedTranslatedItems != null && mHistoryCachedTranslatedItems.containsKey(translatedItem.getId())) {
                mHistoryCachedTranslatedItems.remove(translatedItem.getId());
            }

            notifyHistoryTranslatedItemsChanged();
        } else if (tableName.equals(TranslatedItemEntry.TABLE_NAME_FAVORITES)){
            mTranslatorLocalDataSource.deleteTranslatedItem(tableName, translatedItem);

            if (mFavoritesCachedTranslatedItems != null && mFavoritesCachedTranslatedItems.containsKey(translatedItem.getId())){
                mFavoritesCachedTranslatedItems.remove(translatedItem.getId());
            }

            notifyFavoritesTranslatedItemsChanged();
        }
    }

    @NonNull
    @Override
    public List<TranslatedItem> getTranslatedItems(@NonNull final String tableName) {
        if (tableName.equals(TranslatedItemEntry.TABLE_NAME_HISTORY)) {
            if (!mHistoryCacheTranslatedItemsIsDirty) {
                return getHistoryCachedTranslatedItems();
            }
            final List<TranslatedItem> items = mTranslatorLocalDataSource.getTranslatedItems(tableName);

            mHistoryCachedTranslatedItems = new LinkedHashMap<>();
            for (TranslatedItem item : items) {
                mHistoryCachedTranslatedItems.put(item.getId(), item);
            }
            mHistoryCacheTranslatedItemsIsDirty = false;

            return items;

        }
        if (!mFavoritesCacheTranslatedItemsIdDirty){
            return getFavoritesCachedTranslatedItems();
        }
        final List<TranslatedItem> items = mTranslatorLocalDataSource.getTranslatedItems(tableName);

        mFavoritesCachedTranslatedItems = new LinkedHashMap<>();
        for (TranslatedItem item : items) {
            mFavoritesCachedTranslatedItems.put(item.getId(), item);
        }
        mFavoritesCacheTranslatedItemsIdDirty = false;

        return items;
    }

    @Override
    public void deleteTranslatedItems(@NonNull final String tableName) {
        if (tableName.equals(TranslatedItemEntry.TABLE_NAME_HISTORY)) {
            mTranslatorLocalDataSource.deleteTranslatedItems(tableName);

            if (mHistoryCachedTranslatedItems != null && !mHistoryCachedTranslatedItems.isEmpty()) {
                mHistoryCachedTranslatedItems.clear();
            }

            notifyHistoryTranslatedItemsChanged();
        } else if (tableName.equals(TranslatedItemEntry.TABLE_NAME_FAVORITES)){
            mTranslatorLocalDataSource.deleteTranslatedItems(tableName);

            if (mFavoritesCachedTranslatedItems != null &&
                    !mFavoritesCachedTranslatedItems.isEmpty()){
                mFavoritesCachedTranslatedItems.clear();
            }
            notifyFavoritesTranslatedItemsChanged();
        }
    }

    @Override
    public void updateTranslatedItem(@NonNull final String tableName,
                                     @NonNull final TranslatedItem translatedItem) {
        if (tableName.equals(TranslatedItemEntry.TABLE_NAME_HISTORY)) {
            mTranslatorLocalDataSource.updateTranslatedItem(tableName, translatedItem);
            notifyHistoryTranslatedItemsChanged();
        } else if (tableName.equals(TranslatedItemEntry.TABLE_NAME_FAVORITES)){
            mTranslatorLocalDataSource.updateTranslatedItem(tableName, translatedItem);
            notifyFavoritesTranslatedItemsChanged();
        }
    }

    @Override
    public void updateIsFavoriteTranslatedItems(@NonNull final String tableName,
                                                @NonNull final boolean isFavorite){
        mTranslatorLocalDataSource.updateIsFavoriteTranslatedItems(tableName, isFavorite);
        if (tableName.equals(TranslatedItemEntry.TABLE_NAME_FAVORITES)) {
            notifyFavoritesTranslatedItemsChanged();
        } else if (tableName.equals(TranslatedItemEntry.TABLE_NAME_HISTORY)){
            notifyHistoryTranslatedItemsChanged();
        }
    }

    @Override
    public void printAllTranslatedItems(@NonNull final String tableName) {
        mTranslatorLocalDataSource.printAllTranslatedItems(tableName);
    }


    public void addFavoritesContentObserver(
            final FavoritesTranslatedItemsRepositoryObserver observer){
        if (!mFavoritesObservers.contains(observer)){
            mFavoritesObservers.add(observer);
        }
    }

    public void removeFavoritesContentObserver(
            final FavoritesTranslatedItemsRepositoryObserver observer){
        if (mFavoritesObservers.contains(observer)){
            mFavoritesObservers.remove(observer);
        }
    }

    private void notifyFavoritesTranslatedItemsChanged(){
        for (FavoritesTranslatedItemsRepositoryObserver observer : mFavoritesObservers){
            observer.onFavoritesTranslatedItemsChanged();
        }
    }

    private List<TranslatedItem> getFavoritesCachedTranslatedItems() {
        if (mFavoritesCachedTranslatedItems == null){
            return new ArrayList<>();
        } else {
            return new ArrayList<>(mFavoritesCachedTranslatedItems.values());
        }
    }


    public interface HistoryTranslatedItemsRepositoryObserver {
        void onHistoryTranslatedItemsChanged();
    }

    public interface FavoritesTranslatedItemsRepositoryObserver {
        void onFavoritesTranslatedItemsChanged();
    }
}
