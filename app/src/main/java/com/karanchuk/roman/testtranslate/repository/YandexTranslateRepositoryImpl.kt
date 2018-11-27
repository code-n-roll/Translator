package com.karanchuk.roman.testtranslate.repository

import com.karanchuk.roman.testtranslate.data.database.model.TranslationResponse
import com.karanchuk.roman.testtranslate.data.network.YandexTranslateApi
import io.reactivex.Single


class YandexTranslateRepositoryImpl(
        private val api: YandexTranslateApi
) : YandexTranslateRepository {

    override fun getTranslation(key: String,
                                text: String,
                                lang: String): Single<TranslationResponse> {
        return api.getTranslation(key, text, lang)
    }
}