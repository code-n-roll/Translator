package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.data.network.YandexTranslateApi
import io.reactivex.Single


interface YandexTranslateRepository {

    fun getTranslation(
        key: String,
        text: String,
        lang: String
    ): Single<com.romankaranchuk.translator.data.database.model.TranslationResponse>

    suspend fun getTranslationCoroutine(
        key: String,
        text: String,
        lang: String
    ): com.romankaranchuk.translator.data.database.model.TranslationResponse
}

class YandexTranslateRepositoryImpl(
    private val api: YandexTranslateApi
) : YandexTranslateRepository {

    override fun getTranslation(
        key: String,
        text: String,
        lang: String
    ): Single<com.romankaranchuk.translator.data.database.model.TranslationResponse> {
        return api.getTranslation(key, text, lang)
    }

    override suspend fun getTranslationCoroutine(
        key: String,
        text: String,
        lang: String
    ): com.romankaranchuk.translator.data.database.model.TranslationResponse {
        return com.romankaranchuk.translator.data.database.model.TranslationResponse()//api.getTranslationCoroutine(key, text, lang)
    }
}