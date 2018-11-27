package com.karanchuk.roman.testtranslate.repository

import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition
import com.karanchuk.roman.testtranslate.data.network.YandexDictionaryApi
import io.reactivex.Single


class YandexDictionaryRepositoryImpl(
        private val api: YandexDictionaryApi
) : YandexDictionaryRepository {

    override fun getValueFromDictionary(key: String,
                                        text: String,
                                        lang: String): Single<DictDefinition> {
        return api.getValueFromDictionary(key, text, lang)
    }
}