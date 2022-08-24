package com.romankaranchuk.translator.data.database.model

import com.google.gson.annotations.SerializedName

class Synonym(
    @SerializedName("text") var text: String,
    @SerializedName("gen") var gen: String
) {

    override fun toString(): String {
        return text
    }
}