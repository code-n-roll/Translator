package com.karanchuk.roman.testtranslate.di.module

import android.content.Context
import androidx.room.Room
import com.karanchuk.roman.testtranslate.data.database.room.TestTranslatorDatabase
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationFavoriteDao
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationHistoryDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): TestTranslatorDatabase {
        return Room.databaseBuilder(context, TestTranslatorDatabase::class.java, "testtranslatordb")
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