package com.romankaranchuk.translator.di.module

import com.romankaranchuk.translator.data.database.storage.TranslationSaver
import com.romankaranchuk.translator.data.repository.DictionaryRepository
import com.romankaranchuk.translator.data.repository.LanguagesRepository
import com.romankaranchuk.translator.data.repository.TranslateRepository
import com.romankaranchuk.translator.domain.IGetDefinitionUseCase
import com.romankaranchuk.translator.domain.IGetTranslationUseCase
import com.romankaranchuk.translator.domain.GetDefinitionUseCase
import com.romankaranchuk.translator.domain.GetTranslationUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UseCaseModule {

    @Singleton
    @Provides
    fun provideGetDefinitionUseCase(
        languagesRepository: LanguagesRepository,
        dictionaryRepository: DictionaryRepository
    ): IGetDefinitionUseCase {
        return GetDefinitionUseCase(languagesRepository, dictionaryRepository)
    }

    @Singleton
    @Provides
    fun provideGetTranslationUseCase(
        languagesRepository: LanguagesRepository,
        translationSaver: TranslationSaver,
        translateRepository: TranslateRepository
    ): IGetTranslationUseCase {
        return GetTranslationUseCase(languagesRepository, translationSaver, translateRepository)
    }
}