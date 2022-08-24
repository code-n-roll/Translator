package com.romankaranchuk.translator.ui.translator

import com.romankaranchuk.translator.data.database.model.TranslatedItem

interface TranslatorContract {

    interface View {
        fun setHintOnInput()
        fun createPredictedTranslatedItem(): TranslatedItem
        fun setTextButtonSrcLang(text: String)
        fun getTextButtonSrcLang(): String
        fun setTextButtonTrgLang(text: String)
        fun getTextButtonTrgLang(): String
        fun isEmptyTranslatedResultView(): Boolean
        fun getTextTranslatedResultView(): String
        fun setTextCustomEditText(text: String)
        fun isEmptyCustomEditText(): Boolean
        fun clearCustomEditText()
        fun isRecognizingSourceText(): Boolean
        fun requestRecordAudioPermissions()
        fun isRecordAudioGranted(): Boolean
        fun showLoadingDictionary()
        fun hideLoadingDictionary()
        fun showRetry()
        fun hideRetry()
        fun showSuccess()
        fun hideSuccess()
        fun showError()
        fun showActiveInput()
        fun hideActiveInput()
        fun showKeyboard()
        fun hideKeyboard()
        fun showClear()
        fun hideClear()
        fun showLoadingTargetVoice()
        fun hideLoadingTargetVoice()
        fun showIconTargetVoice()
        fun hideIconTargetVoice()
        fun showLoadingSourceVoice()
        fun hideLoadingSourceVoice()
        fun showIconSourceVoice()
        fun hideIconSourceVoice()
        fun activateVoiceRecognizer()
        fun deactivateVoiceRecognizer()
        fun stopAnimationMicroWaves()
        fun showAnimationMicroWaves()
    }
}