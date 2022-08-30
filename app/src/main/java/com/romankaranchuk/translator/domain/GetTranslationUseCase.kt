package com.romankaranchuk.translator.domain

import com.romankaranchuk.translator.data.database.storage.TranslationSaver
import com.romankaranchuk.translator.data.repository.LanguagesRepository
import com.romankaranchuk.translator.data.repository.TranslateRepository
import javax.inject.Inject

interface IGetTranslationUseCase {
    suspend fun getTranslation(inputText: String, sourceLang: String, targetLang: String): String?
}

class GetTranslationUseCase @Inject constructor(
    private val languagesRepository: LanguagesRepository,
    private val translationSaver: TranslationSaver,
    private val translateRepository: TranslateRepository
) : IGetTranslationUseCase {

    @Deprecated("remove source/target lang")
    override suspend fun getTranslation(inputText: String, sourceLang: String, targetLang: String): String? {
        val langs = languagesRepository.getLanguages(sourceLang, targetLang)

        val currentTranslatedItem = translationSaver.curTranslatedItem
        if (currentTranslatedItem?.srcMeaning == inputText) {
            return null
        }

        return translateRepository.getTranslation(inputText, langs)
    }
}