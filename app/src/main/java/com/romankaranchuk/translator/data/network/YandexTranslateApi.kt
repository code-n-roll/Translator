package com.romankaranchuk.translator.data.network

import com.romankaranchuk.translator.data.database.model.TranslationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexTranslateApi {

    @GET("api/v1.5/tr.json/translate?")
    suspend fun getTranslation(
        @Query("key") key: String,
        @Query("text") text: String,
        @Query("lang") lang: String
    ): TranslationResponse
}