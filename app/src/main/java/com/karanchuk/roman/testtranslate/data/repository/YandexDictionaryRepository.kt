package com.karanchuk.roman.testtranslate.data.repository

import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition
import io.reactivex.Single


interface YandexDictionaryRepository {

    fun getValueFromDictionary(key: String,
                               text: String,
                               lang: String): Single<DictDefinition>
}