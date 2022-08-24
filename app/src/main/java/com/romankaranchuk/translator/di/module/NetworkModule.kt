package com.romankaranchuk.translator.di.module

import com.romankaranchuk.translator.data.network.RetrofitCreator
import com.romankaranchuk.translator.data.network.YandexDictionaryApi
import com.romankaranchuk.translator.data.network.YandexTranslateApi
import com.romankaranchuk.translator.data.repository.DictionaryRepository
import com.romankaranchuk.translator.data.repository.DictionaryRepositoryImpl
import com.romankaranchuk.translator.data.repository.TranslateRepository
import com.romankaranchuk.translator.data.repository.TranslateRepositoryImpl
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
    fun provideTranslateRepository(api: YandexTranslateApi): TranslateRepository {
        return TranslateRepositoryImpl(api)
    }

    @Provides
    fun provideDictionaryRepository(api: YandexDictionaryApi): DictionaryRepository {
        return DictionaryRepositoryImpl(api)
    }
}