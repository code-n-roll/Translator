package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.common.EnvironmentHolder
import com.romankaranchuk.translator.data.database.model.TranslationResponse
import com.romankaranchuk.translator.data.network.YandexTranslateApi


interface TranslateRepository {

    suspend fun getTranslation(
        text: String,
        lang: String
    ): TranslationResponse
}

class TranslateRepositoryImpl(
    private val environmentHolder: EnvironmentHolder,
    private val api: YandexTranslateApi
) : TranslateRepository {

    override suspend fun getTranslation(
        text: String,
        lang: String
    ): TranslationResponse {
        return TranslationResponse()//api.getTranslation(environmentHolder.YANDEX_TRANSLATE_API_KEY, text, lang)
    }
}