package com.karanchuk.roman.testtranslate.presentation.presenter;

/**
 * Created by roman on 16.6.17.
 */

public interface TranslatorPresenter extends BasePresenter {
    void requestTranslatorAPI();
    void requestDictionaryAPI();
    void saveToSharedPreferences();
    void clearContainerSuccess();
    void vocalizeSourceText();
    void vocalizeTargetText();
    void recognizeSourceText();
}
