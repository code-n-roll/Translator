package com.romankaranchuk.translator.ui.view

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.data.database.model.Translation
import timber.log.Timber

/**
 * need to add gen to string before setSpan!!!
 * Created by roman on 23.6.17.
 */
class CustomSynonyms(private val mContext: Context, private val mTranslation: Translation) {
    private val mSpanTextListener: ClickableSpan? = null
    private val mSpanGenListener: ClickableSpan
    private val mSpanCommaListener: ClickableSpan
    fun toSpannable(onItemClicklistener: (View, String) -> Unit): Spannable {
        var textLength = 0
        var accumLength = 0
        var genLength = 0
        var commaLength = 0
        val resultSpan: Spannable = SpannableString(mTranslation.representSynonyms)
        if (mTranslation.text != null) {
            textLength = mTranslation.text!!.length
        }
        resultSpan.setSpan(
            TextClickableSpan(mContext, onItemClicklistener, mTranslation.text!!),
            accumLength, accumLength + textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        accumLength += textLength
        if (mTranslation.gen != null && !mTranslation.gen!!.isEmpty()) {
            genLength = " ".length + mTranslation.gen!!.length
            resultSpan.setSpan(
                GenClickableSpan(
                    mContext
                ), accumLength, accumLength + genLength,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            accumLength += genLength
        }
        commaLength = if (mTranslation.synonyms != null) {
            ", ".length
        } else {
            0
        }
        resultSpan.setSpan(
            CommaClickableSpan(
                mContext
            ), accumLength, accumLength + commaLength,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        accumLength += commaLength
        if (mTranslation.synonyms != null) {
            Timber.d("length = " + mTranslation.representSynonyms.length.toString())
            for (synonym in mTranslation.synonyms!!) {
                textLength = synonym.text.length
                resultSpan.setSpan(
                    TextClickableSpan(mContext, onItemClicklistener, synonym.text), accumLength,
                    accumLength + textLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                accumLength += textLength
                genLength = if (synonym.gen != null && !synonym.gen.isEmpty()) {
                    " ".length + synonym.gen.length
                } else {
                    0
                }
                resultSpan.setSpan(
                    GenClickableSpan(
                        mContext
                    ), accumLength, accumLength + genLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                accumLength += genLength
                commaLength = 0
                if (accumLength != mTranslation.representSynonyms.length) {
                    commaLength = 2
                }
                resultSpan.setSpan(
                    CommaClickableSpan(
                        mContext
                    ), accumLength, accumLength + commaLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                accumLength += commaLength
            }
        }
        return resultSpan
    }

    companion object {
        private const val COMMA = ","
    }

    init {
        mSpanGenListener = object : ClickableSpan() {
            override fun onClick(view: View) {}
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
                ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                ds.color = ContextCompat.getColor(mContext, android.R.color.darker_gray)
                ds.textSize = 50f
            }
        }
        mSpanCommaListener = object : ClickableSpan() {
            override fun onClick(view: View) {}
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
                ds.textSize = 50f
                ds.color = ContextCompat.getColor(mContext, R.color.colorTransl)
            }
        }
    }
}