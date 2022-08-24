package com.romankaranchuk.translator.data.database.model

import com.romankaranchuk.translator.data.database.model.Synonym
import com.romankaranchuk.translator.data.database.model.Meaning
import com.google.gson.annotations.SerializedName

/**
 * Created by roman on 11.4.17.
 */
class Translation(
    var number: String?,
    @SerializedName("syn") var synonyms: List<Synonym>?,
    @SerializedName("mean") var meanings: List<Meaning>?,
    @SerializedName("ex") var expressions: List<Expression>?,
    private var mRepresentSynonyms: String
) {

    @SerializedName("text")
    var text: String? = null

    @SerializedName("gen")
    var gen: String? = null

    private var mRepresentMeanings: String? = null
    private var mRepresentExpressions: String? = null
    var representExpressions: String?
        get() {
            mRepresentExpressions = ""
            if (expressions != null) {
                for (expression in expressions!!) {
                    mRepresentExpressions = mRepresentExpressions + expression.toString()
                }
            }
            val length = mRepresentExpressions!!.length
            if (length >= 1) {
                mRepresentExpressions = mRepresentExpressions!!.substring(0, length - 1)
            }
            return mRepresentExpressions
        }
        set(representExpressions) {
            mRepresentExpressions = representExpressions
        }
    var representMeanings: String?
        get() {
            if (meanings != null) {
                mRepresentMeanings = "("
                for (meaning in meanings!!) {
                    mRepresentMeanings = "$mRepresentMeanings$meaning, "
                }
            }
            return mRepresentMeanings!!.substring(0, mRepresentMeanings!!.length - 2) + ")"
        }
        set(representMeanings) {
            mRepresentMeanings = representMeanings
        }
    var representSynonyms: String
        get() {
            if (text != null) {
                mRepresentSynonyms = text!!
            }
            if (gen != null) {
                mRepresentSynonyms += " " + gen + ", "
            } else {
                if (synonyms != null) {
                    mRepresentSynonyms += ", "
                }
            }
            if (synonyms != null) {
                for (synonym in synonyms!!) {
                    mRepresentSynonyms += if (synonym.gen != null) {
                        synonym.toString() + " " + synonym.gen + ", "
                    } else {
                        "$synonym, "
                    }
                }
            }
            val length = mRepresentSynonyms.length
            if (mRepresentSynonyms.endsWith(", ")) {
                mRepresentSynonyms = mRepresentSynonyms.substring(0, length - 2)
            } else if (mRepresentSynonyms.endsWith(",")) {
                mRepresentSynonyms = mRepresentSynonyms.substring(0, length - 1)
            }
            return mRepresentSynonyms
        }
        set(representSynonyms) {
            mRepresentSynonyms = representSynonyms
        }

    override fun toString(): String {
        var result = ""
        if (number != null) {
            result = number + " "
        }
        if (synonyms != null) {
            for (synonym in synonyms!!) {
                result += if (synonym.gen != null && !synonym.gen.isEmpty()) {
                    synonym.toString() + " " + synonym.gen + ", "
                } else {
                    "$synonym, "
                }
            }
        }
        if (result.length >= 2) {
            result = result.substring(0, result.length - 2)
        }
        result = "$result\n("
        if (meanings != null) {
            for (meaning in meanings!!) {
                result = "$result$meaning "
            }
        }
        if (result.length >= 1) {
            result = result.substring(0, result.length - 1)
        }
        result = "$result)\n"
        if (expressions != null) {
            for (expression in expressions!!) {
                result = result + expression.toString()
            }
        }
        return result
    }
}