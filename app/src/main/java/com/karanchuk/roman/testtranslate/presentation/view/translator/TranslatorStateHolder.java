package com.karanchuk.roman.testtranslate.presentation.view.translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 21.4.17.
 *
 */

public class TranslatorStateHolder {
    private static TranslatorStateHolder INSTANCE = null;

    private final List<OnTranslatorStateObserver> mObservers = new ArrayList<>();


    public static TranslatorStateHolder getInstance(){
        if (INSTANCE == null){
            INSTANCE = new TranslatorStateHolder();
        }
        return INSTANCE;
    }

    public void removeOnTranslatorAPIResultObserver(final OnTranslatorStateObserver observer){
        if (mObservers.contains(observer)){
            mObservers.remove(observer);
        }
    }

    public void addOnTranslatorAPIResultObserver(final OnTranslatorStateObserver observer){
        if (!mObservers.contains(observer)){
            mObservers.add(observer);
        }
    }

    public void notifyTranslatorAPIResult(final boolean success){
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
        void onTranslatorAPIResult(final boolean success);

        void onShowSelectedItem();
    }
}
