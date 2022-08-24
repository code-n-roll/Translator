package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.data.database.model.DictDefinition
import com.romankaranchuk.translator.data.network.YandexDictionaryApi


interface DictionaryRepository {

    suspend fun getValueFromDictionary(
        key: String,
        text: String,
        lang: String
    ): DictDefinition
}

class DictionaryRepositoryImpl(
    private val api: YandexDictionaryApi
) : DictionaryRepository {

    override suspend fun getValueFromDictionary(
        key: String,
        text: String,
        lang: String
    ): DictDefinition {
        return api.getValueFromDictionaryCoroutine(key, text, lang)
    }
}