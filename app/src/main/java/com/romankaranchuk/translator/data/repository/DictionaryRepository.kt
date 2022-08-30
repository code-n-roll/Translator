package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.common.EnvironmentHolder
import com.romankaranchuk.translator.data.database.model.DictDefinition
import com.romankaranchuk.translator.data.network.YandexDictionaryApi


interface DictionaryRepository {

    suspend fun getDictDefinition(
        text: String,
        langs: List<String>
    ): DictDefinition?
}

class DictionaryRepositoryImpl(
    private val environmentHolder: EnvironmentHolder,
    private val api: YandexDictionaryApi
) : DictionaryRepository {

    override suspend fun getDictDefinition(
        text: String,
        langs: List<String>
    ): DictDefinition? {
        val langsFormatted = "${langs[0]}-${langs[1]}"
        return api.getDictDefinition(environmentHolder.YANDEX_DICTIONARY_API_KEY, text, langsFormatted)
    }
}