package com.karanchuk.roman.testtranslate.data.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitCreator {

    companion object {

        const val API_BASE_URL_TRANSLATOR = "https://translate.yandex.net/"
        const val API_BASE_URL_DICTIONARY = "https://dictionary.yandex.net/"

        fun getInstance(baseUrl: String,
                        client: OkHttpClient
        ): Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}