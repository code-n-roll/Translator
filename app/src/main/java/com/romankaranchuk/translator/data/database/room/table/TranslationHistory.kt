package com.romankaranchuk.translator.data.database.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_history")
data class TranslationHistory (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "src_lang_api") var srcLangApi: String? = null,
    @ColumnInfo(name = "trg_lang_api") var trgLangApi: String? = null,
    @ColumnInfo(name = "src_lang_user") var srcLangUser: String? = null,
    @ColumnInfo(name = "trg_lang_user") var trgLangUser: String? = null,

    @ColumnInfo(name = "src_mean") var srcMean: String? = null,
    @ColumnInfo(name = "trg_mean") var trgMean: String? = null,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean = false,
    @ColumnInfo(name = "dict_definition") var dictDefinition: String? = null
)