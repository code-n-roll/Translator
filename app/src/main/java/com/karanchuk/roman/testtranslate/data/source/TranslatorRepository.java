package com.karanchuk.roman.testtranslate.data.source;

import android.support.annotation.NonNull;

import com.karanchuk.roman.testtranslate.data.TranslatedItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatorRepository  implements TranslatorDataSource{
    private static TranslatorRepository INSTANCE = null;

    private final TranslatorDataSource mTranslatorLocalDataSource;

    private List<TranslatedItemsRepositoryObserver> mObservers = new ArrayList<>();

    private Map<String, TranslatedItem> mCachedTranslatedItems,
                                        mCurCachedTranslatedItems;

    private boolean mCacheTranslatedItemsIsDirty = true;

    public static TranslatorRepository getInstance(TranslatorDataSource translatorLocalDataSource){
        if (INSTANCE == null){
            INSTANCE = new TranslatorRepository(translatorLocalDataSource);
        }
        return INSTANCE;
    }

    private TranslatorRepository(@NonNull TranslatorDataSource translatorLocalDataSource){
        mTranslatorLocalDataSource = checkNotNull(translatorLocalDataSource);
    }



    private List<TranslatedItem> getCachedTranslatedItems(){
        if (mCurCachedTranslatedItems == null){
            mCurCachedTranslatedItems = new LinkedHashMap<>();
        }
        if (mCachedTranslatedItems == null ) {
            return new ArrayList<>();
        } else {
            ArrayList<TranslatedItem> old = new ArrayList<>(mCachedTranslatedItems.values());
            old.addAll(mCurCachedTranslatedItems.values());
            return old;
        }
    }


    public void addContentObserver(TranslatedItemsRepositoryObserver observer){
        if (!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    public void removeContentObserver(TranslatedItemsRepositoryObserver observer){
        if (mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }

    private void notifyTranslatedItemsChanged(){
        for (TranslatedItemsRepositoryObserver observer : mObservers){
            observer.onTranslatedItemsChanged();
        }
    }

    @Override
    public boolean saveTranslatedItem(@NonNull TranslatedItem translatedItem) {
        if (mCurCachedTranslatedItems == null){
            mCurCachedTranslatedItems = new LinkedHashMap<>();
        }

        if (!mCurCachedTranslatedItems.containsValue(translatedItem)) {
            mCurCachedTranslatedItems.put(translatedItem.getId(), translatedItem);
        } else {
            mCurCachedTranslatedItems.values().remove(translatedItem);
            mCurCachedTranslatedItems.put(translatedItem.getId(), translatedItem);
            mTranslatorLocalDataSource.deleteTranslatedItem(translatedItem);
        }
        mTranslatorLocalDataSource.saveTranslatedItem(translatedItem);
        notifyTranslatedItemsChanged();
        return true;
    }



    @Override
    public void deleteTranslatedItem(@NonNull TranslatedItem translatedItem) {
        mTranslatorLocalDataSource.deleteTranslatedItem(translatedItem);

        if (mCachedTranslatedItems != null && mCachedTranslatedItems.containsKey(translatedItem.getId())){
            mCachedTranslatedItems.remove(translatedItem.getId());
        }

        notifyTranslatedItemsChanged();
    }

    @NonNull
    @Override
    public List<TranslatedItem> getTranslatedItems() {
        if (!mCacheTranslatedItemsIsDirty){
            return getCachedTranslatedItems();
        } else {
            List<TranslatedItem> items = mTranslatorLocalDataSource.getTranslatedItems();

            mCachedTranslatedItems = new LinkedHashMap<>();
            for (TranslatedItem item : items) {
                mCachedTranslatedItems.put(item.getId(), item);
            }
            mCacheTranslatedItemsIsDirty = false;

            return items;
        }
    }

    @Override
    public void deleteTranslatedItems() {
        mTranslatorLocalDataSource.deleteTranslatedItems();

        if (mCachedTranslatedItems != null && !mCachedTranslatedItems.isEmpty()){
            mCachedTranslatedItems.clear();
        }

        if (mCurCachedTranslatedItems != null && !mCurCachedTranslatedItems.isEmpty()){
            mCurCachedTranslatedItems.clear();
        }
        notifyTranslatedItemsChanged();
    }

    public interface TranslatedItemsRepositoryObserver {
        void onTranslatedItemsChanged();
    }
}
