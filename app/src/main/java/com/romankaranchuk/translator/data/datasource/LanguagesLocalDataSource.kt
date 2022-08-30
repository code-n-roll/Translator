package com.romankaranchuk.translator.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.romankaranchuk.translator.common.Constants
import com.romankaranchuk.translator.data.database.model.Language
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

interface ILanguagesLocalDataSource {

    fun saveSelectedLanguage(isSource: Boolean, language: Language)
    fun restoreSelectedLanguage(isSource: Boolean): String
    fun getLanguages(): List<Language>
    fun getLanguages(vararg jsonKeys: String): List<String>
}

class LanguagesLocalDataSource constructor(
    private val context: Context,
    private val gson: Gson,
    private val sharedPrefs: SharedPreferences
) : ILanguagesLocalDataSource {

    override fun restoreSelectedLanguage(isSource: Boolean): String {
        return sharedPrefs.getString(
            if (isSource) Constants.CUR_SELECTED_ITEM_SRC_LANG else Constants.CUR_SELECTED_ITEM_TRG_LANG,
            "Unknown"
        ) ?: "Unknown"
    }

    override fun saveSelectedLanguage(isSource: Boolean, language: Language) {
        sharedPrefs.edit()
            .putString(
                if (isSource) Constants.CUR_SELECTED_ITEM_SRC_LANG
                else Constants.CUR_SELECTED_ITEM_TRG_LANG,
                language.abbr
            )
            .apply()
    }

    override fun getLanguages(): List<Language> {
        val langsJson = getJsonObjectFromAssetsFile(Constants.LANGS_FILE_NAME)
        return getLangsFromJson(langsJson)
    }

    override fun getLanguages(vararg jsonKeys: String): List<String> {
        val langsJson = getJsonObjectFromAssetsFile(Constants.LANGS_FILE_NAME)
        val res = mutableListOf<String>()
        for (jsonKey in jsonKeys) {
            val langString = langsJson?.get(jsonKey)?.asString ?: continue
            res.add(langString)
        }
        return res
    }

    fun getJsonObjectFromAssetsFile(filename: String?): JsonObject? {
        context.assets.open(filename!!).use { inputStream ->
            JsonReader(InputStreamReader(inputStream)).use { jsonReader ->
                return gson.fromJson(jsonReader, JsonObject::class.java)
            }
        }
    }

    fun getLangsFromJson(langsJson: JsonObject?): List<Language> {
        val items: MutableList<Language> = ArrayList()
        for ((lang, value) in langsJson!!.entrySet()) {
            val abbr = value.asString
            val firstCapitalize =
                lang.substring(0, 1).uppercase(Locale.getDefault()) + lang.substring(1)
            items.add(Language(firstCapitalize, abbr, false))
        }
        return items
    }
}