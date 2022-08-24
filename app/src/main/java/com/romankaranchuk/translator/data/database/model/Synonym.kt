package com.romankaranchuk.translator.data.database.model

import com.google.gson.annotations.SerializedName

/**
 * Created by roman on 20.4.17.
 */
class Synonym(
    @SerializedName("text") var text: String,
    @SerializedName("gen") var gen: String
) {

    override fun toString(): String {
        return text
    }
}