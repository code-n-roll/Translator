package com.romankaranchuk.translator.data.database.model

import com.google.gson.annotations.SerializedName
import com.romankaranchuk.translator.data.database.model.ExpressionTranslation

class Expression {
    @SerializedName("text")
    var text: String? = null

    @SerializedName("tr")
    var expressionTranslation: List<ExpressionTranslation>? = null
    override fun toString(): String {
        var result = text + " \u2014 "
        for (exprTransl in expressionTranslation!!) {
            result = """
                $result${exprTransl.text}
                
                """.trimIndent()
        }
        return result
    }
}