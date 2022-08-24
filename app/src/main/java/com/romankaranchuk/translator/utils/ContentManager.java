package com.romankaranchuk.translator.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 17.4.17.
 */

public class ContentManager {
    private static ContentManager INSTANCE = null;
    private List<TranslatedItemChanged> mObservers = new ArrayList<>();

    public static ContentManager getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ContentManager();
        }
        return INSTANCE;
    }

    public interface TranslatedItemChanged{
        void onTranslatedItemsChanged();
    }

    public void notifyTranslatedItemChanged(){
        for (TranslatedItemChanged observer : mObservers){
            observer.onTranslatedItemsChanged();
        }
    }



    public void addContentObserver(final TranslatedItemChanged observer){
        if (!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    public void removeContentObserver(final TranslatedItemChanged observer){
        if (mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }
}
