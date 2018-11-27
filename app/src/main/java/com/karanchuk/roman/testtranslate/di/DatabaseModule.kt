package com.karanchuk.roman.testtranslate.di

import android.arch.persistence.room.Room
import com.karanchuk.roman.testtranslate.data.database.room.TestTranslatorDatabase
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationFavoriteDao
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationHistoryDao
import com.karanchuk.roman.testtranslate.presentation.TestTranslatorApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: TestTranslatorApp): TestTranslatorDatabase {
        return Room.databaseBuilder(app, TestTranslatorDatabase::class.java, "testtranslatordb")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    fun provideTranslationFavoriteDao(db: TestTranslatorDatabase): TranslationFavoriteDao {
        return db.translationFavoriteDao
    }

    @Provides
    @Singleton
    fun provideTranslationHistoryDao(db: TestTranslatorDatabase): TranslationHistoryDao {
        return db.translationHistoryDao
    }
}