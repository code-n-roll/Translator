package com.romankaranchuk.translator.data.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.romankaranchuk.translator.data.database.room.table.TranslationHistory

@Dao
interface TranslationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(translation: TranslationHistory)

    @Query("DELETE FROM translation_history WHERE id = :id")
    fun delete(id: Long)

    @Query("SELECT * FROM translation_history")
    fun getAll(): List<TranslationHistory>

    @Query("DELETE FROM translation_history")
    fun deleteAll()
}