package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.data.database.model.TranslationResponse
import com.romankaranchuk.translator.data.network.YandexTranslateApi


interface TranslateRepository {

    suspend fun getTranslation(
        key: String,
        text: String,
        lang: String
    ): TranslationResponse
}

class TranslateRepositoryImpl(
    private val api: YandexTranslateApi
) : TranslateRepository {

    override suspend fun getTranslation(
        key: String,
        text: String,
        lang: String
    ): TranslationResponse {
        return TranslationResponse()//api.getTranslation(key, text, lang)
    }
}