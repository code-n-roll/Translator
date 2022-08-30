package com.romankaranchuk.translator.ui.translator

//import com.romankaranchuk.translator.common.Recognizer
//import com.romankaranchuk.translator.common.Vocalizer
//import ru.yandex.speechkit.Language
import android.content.SharedPreferences
import com.google.gson.Gson
import com.romankaranchuk.translator.common.Constants
import com.romankaranchuk.translator.data.database.TablePersistenceContract.TranslatedItemEntry
import com.romankaranchuk.translator.data.database.model.*
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver
import com.romankaranchuk.translator.data.database.storage.TextDataStorage
import com.romankaranchuk.translator.data.database.storage.TranslationSaver
import com.romankaranchuk.translator.domain.IGetDefinitionUseCase
import com.romankaranchuk.translator.domain.IGetTranslationUseCase
import com.romankaranchuk.translator.ui.base.BaseViewModel
import com.romankaranchuk.translator.ui.base.launchOnIO
import com.romankaranchuk.translator.ui.base.switchToUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject

class TranslatorViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson,
    private val translationSaver: TranslationSaver,
    private val translatorRepository: TranslatorRepository,
//    private val vocalizer: Vocalizer,
//    private val recognizer: Recognizer,
    private val textDataStorage: TextDataStorage,
    private val getDefinitionUseCase: IGetDefinitionUseCase,
    private val getTranslationUseCase: IGetTranslationUseCase
) : BaseViewModel(), HistoryTranslatedItemsRepositoryObserver {

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

        var dictDefinition: DictDefinition? = null
        if (dictDefString.isNotEmpty()) {
            dictDefinition = gson.fromJson(dictDefString, DictDefinition::class.java)
        }

        switchToUi {
            _viewState.emit(ViewState.ShowTranslations(dictDefinition))
        }
    }

    fun translate(
        sourceLang: String,
        targetLang: String,
        inputText: String
    ) = launchOnIO {
        _viewState.emit(ViewState.ShowTranslationLoading())
        try {
            translation = getTranslationUseCase.getTranslation(inputText, sourceLang, targetLang)
            val _translation = translation
            if (_translation == null) {
                _viewState.emit(ViewState.ShowTranslationError())
            } else {
                _viewState.emit(ViewState.ShowTranslationSuccess(_translation))
            }
        } catch (e: Exception) {
            _viewState.emit(ViewState.ShowTranslationError())
        }
    }

    fun define(
        sourceLang: String,
        targetLang: String,
        inputText: String
    ) = launchOnIO {
        _viewState.emit(ViewState.ShowDefinitionLoading())
        try {
            val dictDefinition = getDefinitionUseCase.getDefinition(inputText, sourceLang, targetLang)
            if (dictDefinition == null) {
                _viewState.emit(ViewState.ShowDefinitionError())
            } else {
                val translations: MutableList<Translation> = ArrayList()
                for (partOfSpeech in dictDefinition.partsOfSpeech) {
                    translations.addAll(partOfSpeech.translations)
                    for ((index, translation) in partOfSpeech.translations.withIndex()) {
                        translation.number = "${index+1}"
                    }
                }

                _viewState.emit(ViewState.ShowDefinitionSuccess(dictDefinition))
                // TODO() disable temporarily
//                    handleDictionaryResponse(inputText, dictDefinition)
            }
        } catch (e: Exception) {
            Timber.d(e.stackTraceToString())
            _viewState.emit(ViewState.ShowDefinitionError())
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
        class ShowTranslationSuccess(val translation: String) : ViewState()
        class ShowTranslationError() : ViewState()
        class ShowTranslationLoading() : ViewState()
        class ShowTranslations(val dictDefinition: DictDefinition?): ViewState()

        class ShowDefinitionSuccess(val dictDefinition: DictDefinition) : ViewState()
        class ShowDefinitionError() : ViewState()
        class ShowDefinitionLoading() : ViewState()
    }
}