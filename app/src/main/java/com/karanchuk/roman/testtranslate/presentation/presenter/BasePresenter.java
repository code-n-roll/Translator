package com.karanchuk.roman.testtranslate.presentation.presenter;

/**
 * Created by roman on 15.6.17.
 */

public interface BasePresenter {
    void subscribe();

    void unsubscribe();

    void onStart();

    void onStop();
}
