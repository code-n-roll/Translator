package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.common.EnvironmentHolder
import com.romankaranchuk.translator.data.database.model.DictDefinition
import com.romankaranchuk.translator.data.network.YandexDictionaryApi


interface DictionaryRepository {

    suspend fun getDictDefinition(
        text: String,
        lang: String
    ): DictDefinition
}

class DictionaryRepositoryImpl(
    private val environmentHolder: EnvironmentHolder,
    private val api: YandexDictionaryApi
) : DictionaryRepository {

    override suspend fun getDictDefinition(
        text: String,
        lang: String
    ): DictDefinition {
        return api.getDictDefinition(environmentHolder.YANDEX_DICTIONARY_API_KEY, text, lang)
    }
}