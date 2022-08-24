package com.romankaranchuk.translator.data.database.model

import java.util.Objects

/**
 * Created by roman on 18.4.17.
 */
data class Language(
    var name: String,
    var abbr: String,
    var isSelected: Boolean
) : Comparable<Language> {

    override fun compareTo(o: Language): Int {
        return name.compareTo(o.name)
    }

    override fun toString(): String {
        return name
    }

    override fun hashCode(): Int {
        return Objects.hash(abbr)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is Language) {
            return Objects.equals(abbr, obj.abbr)
        }
        return false
    }
}