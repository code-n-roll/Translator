package com.romankaranchuk.translator.ui.translator

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.romankaranchuk.translator.common.Constants
//import com.romankaranchuk.translator.common.Recognizer
//import com.romankaranchuk.translator.common.Vocalizer
import com.romankaranchuk.translator.data.database.TablePersistenceContract.*
import com.romankaranchuk.translator.data.database.model.*
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver
import com.romankaranchuk.translator.data.database.storage.TextDataStorage
import com.romankaranchuk.translator.data.database.storage.TranslationSaver
import com.romankaranchuk.translator.data.repository.DictionaryRepository
import com.romankaranchuk.translator.data.repository.TranslateRepository
import com.romankaranchuk.translator.ui.base.BaseViewModel
import com.romankaranchuk.translator.ui.base.launchOnIO
import com.romankaranchuk.translator.ui.base.switchToUi
import com.romankaranchuk.translator.utils.JsonUtils
import com.romankaranchuk.translator.utils.network.ContentResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
//import ru.yandex.speechkit.Language
import timber.log.Timber
import java.util.HashMap
import javax.inject.Inject

class TranslatorViewModel @Inject constructor(
    private val context: Context,
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val translationSaver: TranslationSaver,
    private val translateRepository: TranslateRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val translatorRepository: TranslatorRepository,
//    private val vocalizer: Vocalizer,
//    private val recognizer: Recognizer,
    private val textDataStorage: TextDataStorage
) : BaseViewModel(), HistoryTranslatedItemsRepositoryObserver {

    val translationsLiveData = MutableLiveData<Pair<List<Translation>, List<PartOfSpeech>>>()
    val translateLiveData = MutableLiveData<ContentResult<TranslationResponse>>()
    val definitionLiveData = MutableLiveData<ContentResult<DictDefinition>>()
    private val _viewState = MutableSharedFlow<ViewState>()
    val viewState = _viewState.asSharedFlow()

    var translation: String? = null

    private var mCurDictDefinition: DictDefinition? = null
    var mHistoryTranslatedItems: List<TranslatedItem>? = null

    init {
        translatorRepository.addHistoryContentObserver(this)

        launchOnIO {
            mHistoryTranslatedItems = translatorRepository.getTranslatedItems(
                TranslatedItemEntry.TABLE_NAME_HISTORY
            )

            val dictDefString: String = sharedPrefs.getString(Constants.TRANSL_CONTENT, "") ?: ""
            if (dictDefString != "null") {
                mCurDictDefinition = gson.fromJson(
                    dictDefString,
                    DictDefinition::class.java
                )
            }
        }
    }

    fun loadTranslations() = launchOnIO {
        val dictDefString = sharedPrefs.getString(Constants.TRANSL_CONTENT, "") ?: ""

        val translations: MutableList<Translation> = mutableListOf()

        var dictDefinition: DictDefinition? = null
        if (dictDefString.isNotEmpty()) {
            dictDefinition = gson.fromJson(dictDefString, DictDefinition::class.java)
            if (dictDefinition != null) {
                for (POS in dictDefinition.partsOfSpeech) {
                    translations.addAll(POS.translations)
                }
            }
        }

        switchToUi {
            translationsLiveData.value = Pair(
                translations,
                dictDefinition?.partsOfSpeech ?: emptyList()
            )
        }
    }

    fun translate(
        sourceLang: String,
        targetLang: String,
        inputText: String
    ) = launchOnIO {
        val langs = JsonUtils.getJsonObjectFromAssetsFile(context, gson, "langs.json")

        val currentTranslatedItem = translationSaver.curTranslatedItem
        if (currentTranslatedItem != null && currentTranslatedItem.srcMeaning != inputText
            || currentTranslatedItem == null
        ) {
            val srcLangAPI = langs[sourceLang].asString
            val trgLangAPI = langs[targetLang].asString
            val mTranslationDirection = "$srcLangAPI-$trgLangAPI"
            val translation: TranslationResponse
            try {
                translation = translateRepository.getTranslation(
                    Constants.TRANSLATOR_API_KEY,
                    inputText,
                    mTranslationDirection
                )
                this@TranslatorViewModel.translation = translation.text?.get(0)
//                switchToUi {
                    translateLiveData.postValue(ContentResult.Success(translation))
//                }
            } catch (e: Exception) {
                switchToUi {
                    translateLiveData.value = ContentResult.Error("translate is failed")
                }
            }
        } else {
        }
    }

    fun loadDefinition(
        inputText: String,
        sourceLang: String,
        targetLang: String
    ) = launchOnIO {
        val dictDefinition: DictDefinition?
        val langs = JsonUtils.getJsonObjectFromAssetsFile(context, gson, "langs.json")
        try {
            dictDefinition = dictionaryRepository.getValueFromDictionary(
                Constants.DICTIONARY_API_KEY,
                inputText,
                "${langs[sourceLang].asString}-${langs[targetLang].asString}"
            )

            switchToUi {
                if (dictDefinition == null) {
                    definitionLiveData.value = ContentResult.Error("response body is null")
                } else {
                    definitionLiveData.value = ContentResult.Success(dictDefinition)
                    // TODO() disable temporarily
//                    handleDictionaryResponse(inputText, dictDefinition)
                }
            }
        } catch (e: Exception) {
            Timber.d(e.stackTraceToString())
            switchToUi {
                definitionLiveData.value = ContentResult.Error("getting definition is failed")
            }
        }
    }

    fun vocalizeText(
        text: String,
        language: Language
    ) {
        if (!text.isEmpty()) {
//            vocalizer.reset()
//            vocalizer.init(language)
//            vocalizer.vocalize(text)
        }
    }

    fun startRecognizeText() {
//        recognizer.reset()
//        recognizer.init(Language.ENGLISH)
//        recognizer.recognize()
    }

    fun stopRecognizeText() {
//        recognizer.reset()
    }

    private fun saveToRepository(
        sourceLang: String,
        targetLang: String,
        inputText: String,
        dictDefinition: DictDefinition
    ) {
        mCurDictDefinition = dictDefinition
        val savedData = HashMap<String, Any>()
        savedData[Constants.SRC_LANG] = sourceLang
        savedData[Constants.TRG_LANG] = targetLang
        savedData[Constants.EDITTEXT_DATA] = inputText
        savedData[Constants.TRANSL_RESULT] = translation ?: ""
        savedData[Constants.TRANSL_CONTENT] = dictDefinition
        translationSaver.setSavedData(savedData)
        Thread(translationSaver).start()
    }

    private fun handleDictionaryResponse(inputText: String, dictDefinition: DictDefinition) {
        val curEditTextContent = inputText.trim { it <= ' ' }
        val srcLangAPI = sharedPrefs.getString(Constants.CUR_SELECTED_ITEM_SRC_LANG, "") ?: ""
        val trgLangAPI = sharedPrefs.getString(Constants.CUR_SELECTED_ITEM_TRG_LANG, "") ?: ""
        val maybeExistedItem =
            TranslatedItem(
                srcLangAPI,
                trgLangAPI,
                null,
                null,
                curEditTextContent,
                null,
                null,
                null
            )
        val mHistoryTranslatedItems = translatorRepository.getTranslatedItems(
            TranslatedItemEntry.TABLE_NAME_HISTORY
        )
        if (!mHistoryTranslatedItems.contains(maybeExistedItem)) {
            saveToRepository(srcLangAPI, trgLangAPI, inputText, dictDefinition)
        }
    }

    fun getTranslatedItemFromCache(maybeExistedItem: TranslatedItem) {
        val translatedItems = translatorRepository.getTranslatedItems(
            TranslatedItemEntry.TABLE_NAME_HISTORY
        )
        val id = translatedItems.indexOf(maybeExistedItem)
        if (id != -1) {
            val dictDefinitionJSON = translatedItems[id].dictDefinitionJSON
            val existedItem: DictDefinition = gson.fromJson(
                dictDefinitionJSON,
                DictDefinition::class.java
            )
            handleDictionaryResponse(translation ?: "", existedItem)
//            mTranslatedResult.text = translatedItems[id].trgMeaning
            Timber.d(translatedItems[id].trgMeaning)
        }
    }

    fun clearContainerSuccess() {
        mCurDictDefinition = null
    }

    //    private boolean isOnline(){
    //        NetworkInfo networkInfo = null;
    //        if (mView != null && mView.getActivity() != null) {
    //            ConnectivityManager cm = (ConnectivityManager)
    //                    mView.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    //            networkInfo = cm.getActiveNetworkInfo();
    //        }
    //        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    //    }

    fun saveToSharedPreferences(
        sourceLang: String,
        targetLang: String,
        inputText: String,
        outputText: String
    ) {
        val data = HashMap<String, Any>()
        data[Constants.EDITTEXT_DATA] = inputText
        data[Constants.SRC_LANG] = sourceLang
        data[Constants.TRG_LANG] = targetLang
        data[Constants.TRANSL_RESULT] = outputText
        data[Constants.TRANSL_CONTENT] = mCurDictDefinition!!
        if (translationSaver.curTranslatedItem != null) {
            data[Constants.CUR_TRANSLATED_ITEM] = translationSaver.curTranslatedItem.toString()
        }
        textDataStorage.saveToSharedPreferences(data)
    }

    override fun onHistoryTranslatedItemsChanged() {
        launchOnIO {
            translatorRepository.getTranslatedItems(
                TranslatedItemEntry.TABLE_NAME_HISTORY
            ).also {
                mHistoryTranslatedItems = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        translatorRepository.removeHistoryContentObserver(this)
    }

    sealed class ViewState {
        class TranslateSuccess() : ViewState()
        class TranslateError() : ViewState()
        class TranslateLoading() : ViewState()

        class DictionarySuccess() : ViewState()
        class DictionaryError() : ViewState()
        class DictionaryLoading() : ViewState()
    }
}