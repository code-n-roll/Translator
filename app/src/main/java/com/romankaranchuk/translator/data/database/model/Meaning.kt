package com.romankaranchuk.translator.data.database.model

import com.google.gson.annotations.SerializedName

class Meaning {
    @SerializedName("text")
    var text: String? = null
    override fun toString(): String {
        return text!!
    }
}