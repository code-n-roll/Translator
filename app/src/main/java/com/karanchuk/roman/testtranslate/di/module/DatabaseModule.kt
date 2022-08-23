package com.karanchuk.roman.testtranslate.di.module

import android.content.Context
import androidx.room.Room
import com.karanchuk.roman.testtranslate.data.database.room.TranslatorDatabase
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationFavoriteDao
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationHistoryDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): TranslatorDatabase {
        return Room.databaseBuilder(context, TranslatorDatabase::class.java, "translatordb")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTranslationFavoriteDao(db: TranslatorDatabase): TranslationFavoriteDao {
        return db.translationFavoriteDao
    }

    @Provides
    @Singleton
    fun provideTranslationHistoryDao(db: TranslatorDatabase): TranslationHistoryDao {
        return db.translationHistoryDao
    }
}