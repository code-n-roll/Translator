package com.karanchuk.roman.testtranslate.presentation.ui.translator;

import android.content.Context;

import com.karanchuk.roman.testtranslate.common.BaseView;
import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;

/**
 * Created by roman on 11.12.17.
 */

public interface TranslatorContract {

    interface Presenter {

        void attachView(Context context);
        void detachView();

        boolean requestTranslatorAPI();
        void requestDictionaryAPI();

        void vocalizeSourceText();
        void vocalizeTargetText();

        void recognizeSourceText();
        void resetRecognizer();

        void saveToSharedPreferences();
        void handleDictionaryResponse(DictDefinition dictDefinition);
        void clearContainerSuccess();
    }

    interface View extends BaseView<Presenter> {

        void showError();

        void setHintOnInput();

        void clickOnButtonSwitchLang();

        TranslatedItem createPredictedTranslatedItem();

        String getTextButtonSrcLang();

        String getTextButtonTrgLang();

        void setTextButtonSrcLang(final String text);

        void setTextButtonTrgLang(final String text);

        boolean isEmptyTranslatedResultView();

        String getTextTranslatedResultView();

        void setTextCustomEditText(final String text);

        boolean isEmptyCustomEditText();

        void clearCustomEditText();

        boolean isRecognizingSourceText();

        void requestRecordAudioPermissions();

        void showAnimationMicroWaves();

        boolean isRecordAudioGranted();

        void getTranslatedItemFromCache(TranslatedItem maybeExistedItem);

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

        void stopAnimationMicroWaves();
    }
}