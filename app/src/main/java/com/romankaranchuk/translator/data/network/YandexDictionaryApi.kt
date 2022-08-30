package com.romankaranchuk.translator.data.network

import com.romankaranchuk.translator.data.database.model.DictDefinition
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexDictionaryApi {

    @GET("api/v1/dicservice.json/lookup?")
    suspend fun getDictDefinition(
        @Query("key") key: String,
        @Query("text") text: String,
        @Query("lang") lang: String
    ): DictDefinition?
}