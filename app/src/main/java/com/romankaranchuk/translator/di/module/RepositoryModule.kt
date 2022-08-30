package com.romankaranchuk.translator.di.module

import com.romankaranchuk.translator.common.EnvironmentHolder
import com.romankaranchuk.translator.data.datasource.LanguagesLocalDataSource
import com.romankaranchuk.translator.data.network.YandexDictionaryApi
import com.romankaranchuk.translator.data.network.YandexTranslateApi
import com.romankaranchuk.translator.data.repository.*
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideTranslateRepository(
        api: YandexTranslateApi,
        environmentHolder: EnvironmentHolder
    ): TranslateRepository {
        return TranslateRepositoryImpl(environmentHolder, api)
    }

    @Provides
    fun provideDictionaryRepository(
        api: YandexDictionaryApi,
        environmentHolder: EnvironmentHolder
    ): DictionaryRepository {
        return DictionaryRepositoryImpl(environmentHolder, api)
    }

    @Provides
    fun provideLanguagesRepository(
        languagesLocalDataSource: LanguagesLocalDataSource
    ): ILanguagesRepository {
        return LanguagesRepository(languagesLocalDataSource)
    }
}