package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.data.database.model.DictDefinition
import com.romankaranchuk.translator.data.network.YandexDictionaryApi
import io.reactivex.Single


interface YandexDictionaryRepository {

//    fun getValueFromDictionary(
//        key: String,
//        text: String,
//        lang: String
//    ): Single<DictDefinition>

    suspend fun getValueFromDictionaryCoroutine(
        key: String,
        text: String,
        lang: String
    ): DictDefinition
}

class YandexDictionaryRepositoryImpl(
    private val api: YandexDictionaryApi
) : YandexDictionaryRepository {

//    override fun getValueFromDictionary(
//        key: String,
//        text: String,
//        lang: String
//    ): Single<DictDefinition> {
//        return api.getValueFromDictionary(key, text, lang)
//    }

    override suspend fun getValueFromDictionaryCoroutine(
        key: String,
        text: String,
        lang: String
    ): DictDefinition {
        return api.getValueFromDictionaryCoroutine(key, text, lang)
    }
}