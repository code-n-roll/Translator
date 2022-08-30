package com.romankaranchuk.translator.di.module

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.romankaranchuk.translator.TranslatorApplication
import com.romankaranchuk.translator.common.*
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl
import com.romankaranchuk.translator.data.database.storage.TextDataStorage
import com.romankaranchuk.translator.data.database.storage.TextDataStorageImpl
import com.romankaranchuk.translator.data.database.storage.TranslationSaver
import com.romankaranchuk.translator.data.datasource.LanguagesLocalDataSource
import com.romankaranchuk.translator.ui.stored.history.HistoryContract
import com.romankaranchuk.translator.ui.stored.history.HistoryPresenterImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: TranslatorApplication) {

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
    fun provideTextDataStorage(
        context: Context,
        gson: Gson,
        sharedPreferences: SharedPreferences,
        languagesLocalDataSource: LanguagesLocalDataSource,
        translatorLocalRepository: TranslatorLocalRepository
    ): TextDataStorage {
        return TextDataStorageImpl(
            context,
            gson,
            sharedPreferences,
            languagesLocalDataSource,
            translatorLocalRepository
        )
    }

    @Singleton
    @Provides
    fun provideTranslatorLocalRepository(context: Context): TranslatorLocalRepository {
        return TranslatorLocalRepository.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideTranslatorRepository(translatorLocalRepository: TranslatorLocalRepository): TranslatorRepository {
        return TranslatorRepositoryImpl.getInstance(translatorLocalRepository)
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
    fun provideTranslationSaver(
        context: Context,
        gson: Gson,
        languagesLocalDataSource: LanguagesLocalDataSource,
        sharedPreferences: SharedPreferences,
        translatorLocalRepository: TranslatorLocalRepository
    ): TranslationSaver {
        return TranslationSaver(context, gson, sharedPreferences, languagesLocalDataSource, translatorLocalRepository)
    }

    @Provides
    fun provideEnvironmentHolder(): EnvironmentHolder {
        return EnvironmentHolder
    }

//    @Singleton
//    @Provides
//    fun provideVocalizer(): Vocalizer {
//        return VocalizerImpl()
//    }
//
//    @Singleton
//    @Provides
//    fun provideRecognizer(): Recognizer {
//        return RecognizerImpl()
//    }
}
