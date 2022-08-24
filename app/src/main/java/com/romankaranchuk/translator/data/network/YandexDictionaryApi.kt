package com.romankaranchuk.translator.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by roman on 17.6.17.
 */
interface YandexDictionaryApi {

//    @GET("api/v1/dicservice.json/lookup?")
//    fun getValueFromDictionary(
//        @Query("key") key: String,
//        @Query("text") text: String,
//        @Query("lang") lang: String
//    ): Single<com.romankaranchuk.translator.data.database.model.DictDefinition>

    @GET("api/v1/dicservice.json/lookup?")
    suspend fun getValueFromDictionaryCoroutine(
        @Query("key") key: String,
        @Query("text") text: String,
        @Query("lang") lang: String
    ): com.romankaranchuk.translator.data.database.model.DictDefinition
}