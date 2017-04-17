package com.karanchuk.roman.testtranslate.utils;

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
        void onTranslatedItemChanged();
    }

    public void notifyTranslatedItemChanged(){
        for (TranslatedItemChanged observer : mObservers){
            observer.onTranslatedItemChanged();
        }
    }

    public void addContentObserver(TranslatedItemChanged observer){
        if (!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    public void removeContentObserver(TranslatedItemChanged observer){
        if (mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }
}
