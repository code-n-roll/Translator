package com.karanchuk.roman.testtranslate.ui.translator

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.karanchuk.roman.testtranslate.common.Constants
import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition
import com.karanchuk.roman.testtranslate.data.database.model.PartOfSpeech
import com.karanchuk.roman.testtranslate.data.database.model.Translation
import com.karanchuk.roman.testtranslate.ui.base.BaseViewModel
import com.karanchuk.roman.testtranslate.ui.base.launchOnIO
import com.karanchuk.roman.testtranslate.ui.base.switchToUi
import javax.inject.Inject

class TranslatorViewModel @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) : BaseViewModel() {

    val translationsLiveData = MutableLiveData<Pair<List<Translation>, List<PartOfSpeech>>>()

    fun loadTranslations() = launchOnIO {
        val dictDefString = sharedPrefs.getString(Constants.TRANSL_CONTENT, "") ?: ""

        val translations: MutableList<Translation> = mutableListOf()

        var dictDefinition: DictDefinition? = null
        if (dictDefString.isNotEmpty()) {
            dictDefinition = gson.fromJson(dictDefString, DictDefinition::class.java)
            if (dictDefinition != null) {
                for (POS in dictDefinition.partsOfSpeech) {
                    translations.addAll(POS.translations)
                }
            }
        }

        switchToUi {
            translationsLiveData.value = Pair(
                translations,
                dictDefinition?.partsOfSpeech ?: emptyList()
            )
        }
    }
}