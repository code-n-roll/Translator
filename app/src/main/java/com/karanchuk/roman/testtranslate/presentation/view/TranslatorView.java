package com.karanchuk.roman.testtranslate.presentation.view;

import com.karanchuk.roman.testtranslate.presentation.presenter.TranslatorPresenter;

/**
 * Created by roman on 16.6.17.
 */

public interface TranslatorView extends BaseView<TranslatorPresenter> {
    void showLoading();
    void hideLoading();
    void showRetry();
    void hideRetry();
    void showSuccess();
    void hideSuccess();
    void showActiveInput();
    void hideActiveInput();
    void showKeyboard();
    void hideKeyboard();
    void showClear();
    void hideClear();
}
