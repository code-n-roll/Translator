package com.romankaranchuk.translator.data.repository

import com.romankaranchuk.translator.data.database.model.Language
import com.romankaranchuk.translator.data.datasource.LanguagesLocalDataSource
import javax.inject.Inject

interface ILanguagesRepository {
    fun saveSelectedLanguage(isSource: Boolean, language: Language)
    fun restoreSelectedLanguage(isSource: Boolean): String
    fun getLanguages(): List<Language>
    fun getLanguages(vararg jsonKeys: String): List<String>
}

class LanguagesRepository @Inject constructor(
    private val languagesLocalDataSource: LanguagesLocalDataSource
): ILanguagesRepository {

    override fun saveSelectedLanguage(isSource: Boolean, language: Language) {
        languagesLocalDataSource.saveSelectedLanguage(isSource, language)
    }

    override fun restoreSelectedLanguage(isSource: Boolean): String {
        return languagesLocalDataSource.restoreSelectedLanguage(isSource)
    }

    override fun getLanguages(): List<Language> {
        return languagesLocalDataSource.getLanguages()
    }

    override fun getLanguages(vararg jsonKeys: String): List<String> {
        return languagesLocalDataSource.getLanguages(*jsonKeys)
    }
}