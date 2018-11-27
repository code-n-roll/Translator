package com.karanchuk.roman.testtranslate.presentation.ui.translator;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ImageButton;

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

        void handleDictionaryResponse(DictDefinition dictDefinition);

        boolean requestTranslatorAPI();

        void requestDictionaryAPI();

        void saveToSharedPreferences();

        void clearContainerSuccess();

        void vocalizeSourceText();

        void vocalizeTargetText();

        void recognizeSourceText();

        void resetRecognizer();

        boolean clickOnGeneralContainer(android.view.View view, MotionEvent event);

        void clickOnSrcLangButton(android.view.View view);

        void clickOnSwitchLangButton(android.view.View view);

        void clickOnTrgLangButton(android.view.View view);

        void clickOnRetryButton(android.view.View view);

        void clickOnFullscreenButton(android.view.View view);

        void clickOnClearEditText(android.view.View view);

        void clickOnRecognizePhotoOrVocalizeSourceText(android.view.View view);

        void clickOnRecognizeSourceText(android.view.View view);

        void clickOnSetFavoriteButton(final ImageButton view);

        void clickOnShareButton(android.view.View view);

        void clickOnSynonymItem(android.view.View view, String text);

        void clickOnVocalizeTargetText(android.view.View view);
    }

    interface View extends BaseView<Presenter> {

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
