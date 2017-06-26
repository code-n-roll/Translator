package com.karanchuk.roman.testtranslate.presentation.view;

import com.karanchuk.roman.testtranslate.presentation.presenter.TranslatorPresenter;

/**
 * Created by roman on 16.6.17.
 */

public interface TranslatorView extends BaseView<TranslatorPresenter> {
    void showLoadingDictionary();
    void hideLoadingDictionary();
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
    void showLoadingTargetVoice();
    void hideLoadingTargetVoice();
    void showIconTargetVoice();
    void hideIconTargetVoice();
    void showLoadingSourceVoice();
    void hideLoadingSourceVoice();
    void showIconSourceVoice();
    void hideIconSourceVoice();
    void activateVoiceRecognizer();
    void desactivateVoiceRecognizer();
}
