package com.karanchuk.roman.testtranslate.di.module

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.karanchuk.roman.testtranslate.TestTranslatorApplication
import com.karanchuk.roman.testtranslate.common.Constants
import com.karanchuk.roman.testtranslate.common.Recognizer
import com.karanchuk.roman.testtranslate.common.RecognizerImpl
import com.karanchuk.roman.testtranslate.common.Vocalizer
import com.karanchuk.roman.testtranslate.common.VocalizerImpl
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepository
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl
import com.karanchuk.roman.testtranslate.data.database.storage.TextDataStorage
import com.karanchuk.roman.testtranslate.data.database.storage.TextDataStorageImpl
import com.karanchuk.roman.testtranslate.data.database.storage.TranslationSaver
import com.karanchuk.roman.testtranslate.ui.stored.history.HistoryContract
import com.karanchuk.roman.testtranslate.ui.stored.history.HistoryPresenterImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: TestTranslatorApplication) {

    @Singleton
    @Provides
    internal fun provideContext(): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideTextDataStorage(context: Context, gson: Gson): TextDataStorage {
        return TextDataStorageImpl(context, gson)
    }

    @Singleton
    @Provides
    fun provideTranslatorRepository(context: Context): TranslatorRepository {
        return TranslatorRepositoryImpl.getInstance(TranslatorLocalRepository.getInstance(context))
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideHistoryPresenter(context: Context): HistoryContract.HistoryPresenter {
        return HistoryPresenterImpl(context)
    }

    @Singleton
    @Provides
    fun provideTranslationSaver(context: Context, gson: Gson): TranslationSaver {
        return TranslationSaver(context, gson)
    }

    @Singleton
    @Provides
    fun provideVocalizer(): Vocalizer {
        return VocalizerImpl()
    }

    @Singleton
    @Provides
    fun provideRecognizer(): Recognizer {
        return RecognizerImpl()
    }
}
