package com.romankaranchuk.translator.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by roman on 15.6.17.
 */
interface YandexTranslateApi {

    @GET("api/v1.5/tr.json/translate?")
    fun getTranslation(
        @Query("key") key: String,
        @Query("text") text: String,
        @Query("lang") lang: String
    ): Single<com.romankaranchuk.translator.data.database.model.TranslationResponse>

    @GET("api/v1.5/tr.json/translate?")
    suspend fun getTranslationCoroutine(
        @Query("key") key: String,
        @Query("text") text: String,
        @Query("lang") lang: String
    ): com.romankaranchuk.translator.data.database.model.TranslationResponse
}