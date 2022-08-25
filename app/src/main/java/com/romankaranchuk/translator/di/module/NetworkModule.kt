package com.romankaranchuk.translator.di.module

import com.romankaranchuk.translator.common.EnvironmentHolder
import com.romankaranchuk.translator.data.network.RetrofitFactory
import com.romankaranchuk.translator.data.network.YandexDictionaryApi
import com.romankaranchuk.translator.data.network.YandexTranslateApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    fun provideYandexTranslateApi(environmentHolder: EnvironmentHolder, okHttpClient: OkHttpClient): YandexTranslateApi {
        return RetrofitFactory.getInstance(environmentHolder.YANDEX_TRANSLATE_API_BASE_URL, okHttpClient)
                .create(YandexTranslateApi::class.java)
    }

    @Provides
    fun provideYandexDictionaryApi(environmentHolder: EnvironmentHolder, okHttpClient: OkHttpClient): YandexDictionaryApi {
        return RetrofitFactory.getInstance(environmentHolder.YANDEX_DICTIONARY_API_BASE_URL, okHttpClient)
                .create(YandexDictionaryApi::class.java)
    }
}