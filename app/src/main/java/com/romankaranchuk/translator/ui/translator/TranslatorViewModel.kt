package com.romankaranchuk.translator.ui.translator

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.romankaranchuk.translator.common.Recognizer
import com.romankaranchuk.translator.common.Vocalizer
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver
import com.romankaranchuk.translator.data.repository.YandexDictionaryRepository
import com.romankaranchuk.translator.data.repository.YandexTranslateRepository
import com.romankaranchuk.translator.ui.base.BaseViewModel
import com.romankaranchuk.translator.ui.base.launchOnIO
import com.romankaranchuk.translator.ui.base.switchToUi
import com.romankaranchuk.translator.utils.network.ContentResult
import ru.yandex.speechkit.Language
import timber.log.Timber
import java.util.HashMap
import javax.inject.Inject

class TranslatorViewModel @Inject constructor(
    private val context: Context,
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val translationSaver: com.romankaranchuk.translator.data.database.storage.TranslationSaver,
    private val yandexTranslateRepository: YandexTranslateRepository,
    private val yandexDictionaryRepository: YandexDictionaryRepository,
    private val translatorRepository: com.romankaranchuk.translator.data.database.repository.TranslatorRepository,
    private val vocalizer: Vocalizer,
    private val recognizer: Recognizer,
    private val textDataStorage: com.romankaranchuk.translator.data.database.storage.TextDataStorage
) : BaseViewModel(), HistoryTranslatedItemsRepositoryObserver {

    val translationsLiveData = MutableLiveData<Pair<List<com.romankaranchuk.translator.data.database.model.Translation>, List<com.romankaranchuk.translator.data.database.model.PartOfSpeech>>>()
    val translateLiveData = MutableLiveData<ContentResult<com.romankaranchuk.translator.data.database.model.TranslationResponse>>()
    val definitionLiveData = MutableLiveData<ContentResult<com.romankaranchuk.translator.data.database.model.DictDefinition>>()

    var translation: String? = null

    private var mCurDictDefinition: com.romankaranchuk.translator.data.database.model.DictDefinition? = null
    var mHistoryTranslatedItems: List<com.romankaranchuk.translator.data.database.model.TranslatedItem>? = null

    init {
        translatorRepository.addHistoryContentObserver(this)

        launchOnIO {
            mHistoryTranslatedItems = translatorRepository.getTranslatedItems(
                com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY
            )

            val dictDefString: String = sharedPrefs.getString(com.romankaranchuk.translator.common.Constants.TRANSL_CONTENT, "") ?: ""
            if (dictDefString != "null") {
                mCurDictDefinition = gson.fromJson<com.romankaranchuk.translator.data.database.model.DictDefinition>(
                    dictDefString,
                    com.romankaranchuk.translator.data.database.model.DictDefinition::class.java
                )
            }
        }
    }

    fun loadTranslations() = launchOnIO {
        val dictDefString = sharedPrefs.getString(com.romankaranchuk.translator.common.Constants.TRANSL_CONTENT, "") ?: ""

        val translations: MutableList<com.romankaranchuk.translator.data.database.model.Translation> = mutableListOf()

        var dictDefinition: com.romankaranchuk.translator.data.database.model.DictDefinition? = null
        if (dictDefString.isNotEmpty()) {
            dictDefinition = gson.fromJson(dictDefString, com.romankaranchuk.translator.data.database.model.DictDefinition::class.java)
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
        val langs = com.romankaranchuk.translator.utils.JsonUtils.getJsonObjectFromAssetsFile(context, gson, "langs.json")

        val currentTranslatedItem = translationSaver.curTranslatedItem
        if (currentTranslatedItem != null && currentTranslatedItem.srcMeaning != inputText
            || currentTranslatedItem == null
        ) {
            val srcLangAPI = langs[sourceLang].asString
            val trgLangAPI = langs[targetLang].asString
            val mTranslationDirection = "$srcLangAPI-$trgLangAPI"
            val translation: com.romankaranchuk.translator.data.database.model.TranslationResponse
            try {
                translation = yandexTranslateRepository.getTranslationCoroutine(
                    com.romankaranchuk.translator.common.Constants.TRANSLATOR_API_KEY,
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
        val dictDefinition: com.romankaranchuk.translator.data.database.model.DictDefinition?
        val langs = com.romankaranchuk.translator.utils.JsonUtils.getJsonObjectFromAssetsFile(context, gson, "langs.json")
        try {
            dictDefinition = yandexDictionaryRepository.getValueFromDictionaryCoroutine(
                com.romankaranchuk.translator.common.Constants.DICTIONARY_API_KEY,
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
            vocalizer.reset()
            vocalizer.init(language)
            vocalizer.vocalize(text)
        }
    }

    fun startRecognizeText() {
        recognizer.reset()
        recognizer.init(Language.ENGLISH)
        recognizer.recognize()
    }

    fun stopRecognizeText() {
        recognizer.reset()
    }

    private fun saveToRepository(
        sourceLang: String,
        targetLang: String,
        inputText: String,
        dictDefinition: com.romankaranchuk.translator.data.database.model.DictDefinition
    ) {
        mCurDictDefinition = dictDefinition
        val savedData = HashMap<String, Any>()
        savedData[com.romankaranchuk.translator.common.Constants.SRC_LANG] = sourceLang
        savedData[com.romankaranchuk.translator.common.Constants.TRG_LANG] = targetLang
        savedData[com.romankaranchuk.translator.common.Constants.EDITTEXT_DATA] = inputText
        savedData[com.romankaranchuk.translator.common.Constants.TRANSL_RESULT] = translation ?: ""
        savedData[com.romankaranchuk.translator.common.Constants.TRANSL_CONTENT] = dictDefinition
        translationSaver.setSavedData(savedData)
        Thread(translationSaver).start()
    }

    private fun handleDictionaryResponse(inputText: String, dictDefinition: com.romankaranchuk.translator.data.database.model.DictDefinition) {
        val curEditTextContent = inputText.trim { it <= ' ' }
        val srcLangAPI = sharedPrefs.getString(com.romankaranchuk.translator.common.Constants.CUR_SELECTED_ITEM_SRC_LANG, "") ?: ""
        val trgLangAPI = sharedPrefs.getString(com.romankaranchuk.translator.common.Constants.CUR_SELECTED_ITEM_TRG_LANG, "") ?: ""
        val maybeExistedItem =
            com.romankaranchuk.translator.data.database.model.TranslatedItem(
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
            com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY
        )
        if (!mHistoryTranslatedItems.contains(maybeExistedItem)) {
            saveToRepository(srcLangAPI, trgLangAPI, inputText, dictDefinition)
        }
    }

    fun getTranslatedItemFromCache(maybeExistedItem: com.romankaranchuk.translator.data.database.model.TranslatedItem) {
        val translatedItems = translatorRepository.getTranslatedItems(
            com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY
        )
        val id = translatedItems.indexOf(maybeExistedItem)
        if (id != -1) {
            val dictDefinitionJSON = translatedItems[id].dictDefinitionJSON
            val existedItem: com.romankaranchuk.translator.data.database.model.DictDefinition = gson.fromJson(
                dictDefinitionJSON,
                com.romankaranchuk.translator.data.database.model.DictDefinition::class.java
            )
            handleDictionaryResponse(translation ?: "", existedItem)
//            mTranslatedResult.text = translatedItems[id].trgMeaning
            Log.d("myLogs", translatedItems[id].trgMeaning)
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
        data[com.romankaranchuk.translator.common.Constants.EDITTEXT_DATA] = inputText
        data[com.romankaranchuk.translator.common.Constants.SRC_LANG] = sourceLang
        data[com.romankaranchuk.translator.common.Constants.TRG_LANG] = targetLang
        data[com.romankaranchuk.translator.common.Constants.TRANSL_RESULT] = outputText
        data[com.romankaranchuk.translator.common.Constants.TRANSL_CONTENT] = mCurDictDefinition!!
        if (translationSaver.curTranslatedItem != null) {
            data[com.romankaranchuk.translator.common.Constants.CUR_TRANSLATED_ITEM] = translationSaver.curTranslatedItem.toString()
        }
        textDataStorage.saveToSharedPreferences(data)
    }

    override fun onHistoryTranslatedItemsChanged() {
        launchOnIO {
            translatorRepository.getTranslatedItems(
                com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY
            ).also {
                mHistoryTranslatedItems = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        translatorRepository.removeHistoryContentObserver(this)
    }
}