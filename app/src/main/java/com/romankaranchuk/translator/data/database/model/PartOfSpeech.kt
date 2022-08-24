package com.romankaranchuk.translator.data.database.model

import com.romankaranchuk.translator.data.database.model.Translation
import com.google.gson.annotations.SerializedName

class PartOfSpeech(
    @SerializedName("pos") var name: String,
    @SerializedName("tr") var translations: List<Translation>
) {
    @SerializedName("text")
    var text: String? = null

    @SerializedName("ts")
    var transcription: String? = null

    override fun toString(): String {
        var result = """
             ${name}
             
             """.trimIndent()
        for (t in translations) {
            result = """
                $result$t
                
                """.trimIndent()
        }
        return result
    }
}