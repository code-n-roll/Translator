package com.romankaranchuk.translator.di.module

import android.content.Context
import androidx.room.Room
import com.romankaranchuk.translator.data.database.room.TranslatorDatabase
import com.romankaranchuk.translator.data.database.room.dao.TranslationFavoriteDao
import com.romankaranchuk.translator.data.database.room.dao.TranslationHistoryDao
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