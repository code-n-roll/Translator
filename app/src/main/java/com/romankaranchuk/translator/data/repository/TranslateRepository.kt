package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.common.EnvironmentHolder
import com.romankaranchuk.translator.data.database.model.TranslationResponse
import com.romankaranchuk.translator.data.network.YandexTranslateApi


interface TranslateRepository {

    suspend fun getTranslation(
        text: String,
        langs: List<String>
    ): String
}

class TranslateRepositoryImpl(
    private val environmentHolder: EnvironmentHolder,
    private val api: YandexTranslateApi
) : TranslateRepository {

    override suspend fun getTranslation(
        text: String,
        langs: List<String>
    ): String {
        val translationDirection = "${langs[0]}-${langs[1]}"
        return "mocked translation"//api.getTranslation(environmentHolder.YANDEX_TRANSLATE_API_KEY, text, translationDirection).text?.get(0)
    }
}