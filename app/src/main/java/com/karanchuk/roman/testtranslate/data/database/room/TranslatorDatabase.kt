package com.karanchuk.roman.testtranslate.data.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationFavoriteDao
import com.karanchuk.roman.testtranslate.data.database.room.dao.TranslationHistoryDao
import com.karanchuk.roman.testtranslate.data.database.room.table.TranslationFavorite
import com.karanchuk.roman.testtranslate.data.database.room.table.TranslationHistory


@Database(
    version = 1,
    entities = [TranslationHistory::class, TranslationFavorite::class],
    exportSchema = false
)
abstract class TranslatorDatabase : RoomDatabase() {

    abstract val translationFavoriteDao: TranslationFavoriteDao
    abstract val translationHistoryDao: TranslationHistoryDao
}