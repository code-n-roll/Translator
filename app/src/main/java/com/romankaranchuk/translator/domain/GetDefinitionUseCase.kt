package com.romankaranchuk.translator.domain

import com.romankaranchuk.translator.data.database.model.DictDefinition
import com.romankaranchuk.translator.data.repository.DictionaryRepository
import com.romankaranchuk.translator.data.repository.LanguagesRepository
import javax.inject.Inject

interface IGetDefinitionUseCase {
    suspend fun getDefinition(inputText: String, sourceLang: String, targetLang: String): DictDefinition?
}

class GetDefinitionUseCase @Inject constructor(
    private val languagesRepository: LanguagesRepository,
    private val dictionaryRepository: DictionaryRepository
) : IGetDefinitionUseCase {

    @Deprecated("remove source/target lang")
    override suspend fun getDefinition(inputText: String, sourceLang: String, targetLang: String): DictDefinition? {
        val langs = languagesRepository.getLanguages(sourceLang, targetLang)
        return dictionaryRepository.getDictDefinition(inputText, langs)
    }
}