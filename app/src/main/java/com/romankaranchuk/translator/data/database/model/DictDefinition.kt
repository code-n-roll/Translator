package com.romankaranchuk.translator.data.database.model

import com.romankaranchuk.translator.data.database.model.PartOfSpeech
import com.google.gson.annotations.SerializedName
import com.romankaranchuk.translator.data.database.model.Translation
import java.util.ArrayList

/**
 * Created by roman on 19.4.17.
 */
class DictDefinition(
    var text: String,
    var transcription: String,
    @SerializedName("def") var partsOfSpeech: List<PartOfSpeech>,
    var jsonToStringRepr: String
) {

    override fun toString(): String {
        var result = ""
        for (pOfs in partsOfSpeech) {
            result = """
                $result$pOfs
                
                """.trimIndent()
        }
        return result
    }

    val translations: List<Translation>
        get() {
            val translations: MutableList<Translation> = ArrayList()
            for (POS in partsOfSpeech) {
                translations.addAll(POS.translations)
            }
            return translations
        }
}