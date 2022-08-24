package com.romankaranchuk.translator.data.database.model

import com.google.gson.annotations.SerializedName

/**
 * Created by roman on 18.6.17.
 */
class Meaning {
    @SerializedName("text")
    var text: String? = null
    override fun toString(): String {
        return text!!
    }
}