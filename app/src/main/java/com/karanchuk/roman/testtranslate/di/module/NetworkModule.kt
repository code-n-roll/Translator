package com.karanchuk.roman.testtranslate.di.module

import com.karanchuk.roman.testtranslate.data.network.RetrofitCreator
import com.karanchuk.roman.testtranslate.data.network.YandexDictionaryApi
import com.karanchuk.roman.testtranslate.data.network.YandexTranslateApi
import com.karanchuk.roman.testtranslate.data.repository.YandexDictionaryRepository
import com.karanchuk.roman.testtranslate.data.repository.YandexDictionaryRepositoryImpl
import com.karanchuk.roman.testtranslate.data.repository.YandexTranslateRepository
import com.karanchuk.roman.testtranslate.data.repository.YandexTranslateRepositoryImpl
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
    fun provideYandexTranslateApi(okHttpClient: OkHttpClient): YandexTranslateApi {
        return RetrofitCreator.getInstance(RetrofitCreator.API_BASE_URL_TRANSLATOR, okHttpClient)
                .create(YandexTranslateApi::class.java)
    }

    @Provides
    fun provideYandexDictionaryApi(okHttpClient: OkHttpClient): YandexDictionaryApi {
        return RetrofitCreator.getInstance(RetrofitCreator.API_BASE_URL_DICTIONARY, okHttpClient)
                .create(YandexDictionaryApi::class.java)
    }
    @Provides
    fun provideYandexTranslateRepository(api: YandexTranslateApi): YandexTranslateRepository {
        return YandexTranslateRepositoryImpl(api)
    }

    @Provides
    fun provideYandexDictionaryRepository(api: YandexDictionaryApi): YandexDictionaryRepository {
        return YandexDictionaryRepositoryImpl(api)
    }
}