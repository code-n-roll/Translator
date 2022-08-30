package com.romankaranchuk.translator.data.database.model

import com.google.gson.annotations.SerializedName

data class TranslationResponse(
    @SerializedName("text") val text: List<String>
)