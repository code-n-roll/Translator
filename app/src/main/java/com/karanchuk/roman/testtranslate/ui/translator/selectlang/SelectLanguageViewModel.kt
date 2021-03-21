package com.karanchuk.roman.testtranslate.ui.translator.selectlang

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.karanchuk.roman.testtranslate.common.Constants
import com.karanchuk.roman.testtranslate.data.database.model.Language
import com.karanchuk.roman.testtranslate.ui.base.BaseViewModel
import com.karanchuk.roman.testtranslate.ui.base.launchOnIO
import com.karanchuk.roman.testtranslate.ui.base.switchToUi
import com.karanchuk.roman.testtranslate.utils.JsonUtils
import javax.inject.Inject

class SelectLanguageViewModel @Inject constructor(
    private val context: Context,
    private val gson: Gson,
    private val sharedPrefs: SharedPreferences
) : BaseViewModel() {

    val languagesLiveData = MutableLiveData<Pair<List<Language>, Int>>()

    fun loadLanguages(isSource: Boolean) = launchOnIO {
        val langsJson = JsonUtils.getJsonObjectFromAssetsFile(context, gson, Constants.LANGS_FILE_NAME)
        val items = JsonUtils.getLangsFromJson(langsJson).sorted()

        val abbr = sharedPrefs.getString(
            if (isSource) Constants.CUR_SELECTED_ITEM_SRC_LANG else Constants.CUR_SELECTED_ITEM_TRG_LANG,
            "Unknown"
        ) ?: "Unknown"
        val selectedLang = items.find { it.abbr == abbr } ?: "Unknown"
        val selectedLangIndex = items.indexOf(selectedLang)

        switchToUi {
            languagesLiveData.value = Pair(items, selectedLangIndex)
        }
    }

    fun saveSelectedLanguage(isSource: Boolean, language: Language) = launchOnIO {
        sharedPrefs.edit()
            .putString(
                if (isSource) Constants.CUR_SELECTED_ITEM_SRC_LANG
                else Constants.CUR_SELECTED_ITEM_TRG_LANG,
                language.abbr
            )
            .apply()
    }
}