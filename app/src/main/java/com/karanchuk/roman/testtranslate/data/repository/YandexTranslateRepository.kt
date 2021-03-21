package com.karanchuk.roman.testtranslate.data.repository

import com.karanchuk.roman.testtranslate.data.database.model.TranslationResponse
import com.karanchuk.roman.testtranslate.data.network.YandexTranslateApi
import io.reactivex.Single


interface YandexTranslateRepository {

    fun getTranslation(
        key: String,
        text: String,
        lang: String
    ): Single<TranslationResponse>

    suspend fun getTranslationCoroutine(
        key: String,
        text: String,
        lang: String
    ): TranslationResponse
}

class YandexTranslateRepositoryImpl(
    private val api: YandexTranslateApi
) : YandexTranslateRepository {

    override fun getTranslation(
        key: String,
        text: String,
        lang: String
    ): Single<TranslationResponse> {
        return api.getTranslation(key, text, lang)
    }

    override suspend fun getTranslationCoroutine(
        key: String,
        text: String,
        lang: String
    ): TranslationResponse {
        return api.getTranslationCoroutine(key, text, lang)
    }
}