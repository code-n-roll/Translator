package com.karanchuk.roman.testtranslate.di

import com.karanchuk.roman.testtranslate.data.network.RetrofitCreator
import com.karanchuk.roman.testtranslate.data.network.YandexDictionaryApi
import com.karanchuk.roman.testtranslate.data.network.YandexTranslateApi
import com.karanchuk.roman.testtranslate.repository.YandexDictionaryRepository
import com.karanchuk.roman.testtranslate.repository.YandexDictionaryRepositoryImpl
import com.karanchuk.roman.testtranslate.repository.YandexTranslateRepository
import com.karanchuk.roman.testtranslate.repository.YandexTranslateRepositoryImpl
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class NetworkModule {

    @Provides
    fun provideYandexTranslateApi(): YandexTranslateApi {
        return RetrofitCreator.getInstance(RetrofitCreator.API_BASE_URL_TRANSLATOR, OkHttpClient())
                .create(YandexTranslateApi::class.java)
    }

    @Provides
    fun provideYandexDictionaryApi(): YandexDictionaryApi {
        return RetrofitCreator.getInstance(RetrofitCreator.API_BASE_URL_DICTIONARY, OkHttpClient())
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