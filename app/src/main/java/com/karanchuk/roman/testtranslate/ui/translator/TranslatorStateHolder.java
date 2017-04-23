package com.karanchuk.roman.testtranslate.ui.translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 21.4.17.
 */

public class TranslatorStateHolder {
    private static TranslatorStateHolder INSTANCE = null;

    private List<OnTranslatorStateObserver> mObservers = new ArrayList<>();


    public static TranslatorStateHolder getInstance(){
        if (INSTANCE == null){
            INSTANCE = new TranslatorStateHolder();
        }
        return INSTANCE;
    }

    public void removeOnTranslatorAPIResultObserver(OnTranslatorStateObserver observer){
        if (mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }

    public void addOnTranslatorAPIResultObserver(OnTranslatorStateObserver observer){
        if (!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    public void notifyTranslatorAPIResult(boolean success){
        for (OnTranslatorStateObserver observer : mObservers){
            observer.onTranslatorAPIResult(success);
        }
    }

    public void notifyShowSelectedItem(){
        for (OnTranslatorStateObserver observer : mObservers){
            observer.onShowSelectedItem();
        }
    }

    public interface OnTranslatorStateObserver {
        void onTranslatorAPIResult(boolean success);

        void onShowSelectedItem();
    }
}
