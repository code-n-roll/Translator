package com.karanchuk.roman.testtranslate.data.database.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem

@Dao
interface TranslationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(translation: TranslatedItem)

    @Query("DELETE FROM translation_history WHERE id = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM translation_history")
    fun getAll(): List<TranslatedItem>

    @Query("DELETE FROM translation_history")
    fun deleteAll()
}