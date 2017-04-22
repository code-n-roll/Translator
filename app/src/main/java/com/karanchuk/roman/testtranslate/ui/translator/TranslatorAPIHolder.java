package com.karanchuk.roman.testtranslate.ui.translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 21.4.17.
 */

public class TranslatorAPIHolder {
    private static TranslatorAPIHolder INSTANCE = null;

    private List<OnTranslatorAPIResultObserver> mObservers = new ArrayList<>();


    public static TranslatorAPIHolder getInstance(){
        if (INSTANCE == null){
            INSTANCE = new TranslatorAPIHolder();
        }
        return INSTANCE;
    }

    public void removeOnTranslatorAPIResultObserver(OnTranslatorAPIResultObserver observer){
        if (mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }

    public void addOnTranslatorAPIResultObserver(OnTranslatorAPIResultObserver observer){
        if (!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    public void notifyTranslatorAPIResult(boolean success){
        for (OnTranslatorAPIResultObserver observer : mObservers){
            observer.onTranslatorAPIResult(success);
        }
    }

    public interface OnTranslatorAPIResultObserver {
        void onTranslatorAPIResult(boolean success);
    }
}
