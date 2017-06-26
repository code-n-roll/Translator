package com.karanchuk.roman.testtranslate.presentation.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.presentation.model.Synonym;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.TranslatorRecyclerAdapter;

/**
 * need to add gen to string before setSpan!!!
 * Created by roman on 23.6.17.
 */

public class CustomSynonyms {
    private static final String COMMA = ",";
    private Context mContext;
    private Translation mTranslation;

    private ClickableSpan mSpanTextListener;
    private ClickableSpan mSpanGenListener;
    private ClickableSpan mSpanCommaListener;

    public CustomSynonyms(Context context, Translation translation) {
        mContext = context;
        mTranslation = translation;

        mSpanGenListener = new ClickableSpan() {
            @Override
            public void onClick(View view) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                ds.setColor(ContextCompat.getColor(mContext, android.R.color.darker_gray));
                ds.setTextSize(50f);
            }
        };
        mSpanCommaListener = new ClickableSpan() {
            @Override
            public void onClick(View view) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
                ds.setTextSize(50f);
                ds.setColor(ContextCompat.getColor(mContext, R.color.colorTransl));
            }
        };
    }


    public Spannable toSpannable(TranslatorRecyclerAdapter.OnItemClickListener onItemClicklistener){
        int textLength = 0;
        int accumLength = 0;
        int genLength = 0;
        int commaLength = 0;
        Spannable resultSpan = new SpannableString(mTranslation.getRepresentSynonyms());

        if (mTranslation.getText() != null) {
            textLength = mTranslation.getText().length();
        }
        resultSpan.setSpan(new TextClickableSpan(mContext, onItemClicklistener, mTranslation.getText()),
                accumLength, accumLength + textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        accumLength += textLength;

        if (mTranslation.getGen() != null && !mTranslation.getGen().isEmpty()) {
            genLength = " ".length() + mTranslation.getGen().length();
            resultSpan.setSpan(new GenClickableSpan(mContext), accumLength, accumLength + genLength,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            accumLength += genLength;
        }

        if (mTranslation.getSynonyms() != null) {
            commaLength = ", ".length();
        } else {
            commaLength = 0;
        }
        resultSpan.setSpan(new CommaClickableSpan(mContext), accumLength, accumLength + commaLength,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        accumLength += commaLength;

        if (mTranslation.getSynonyms() != null) {
            Log.d("myLogs", "length = " + String.valueOf(mTranslation.getRepresentSynonyms().length()));
            for (Synonym synonym : mTranslation.getSynonyms()) {
                textLength = synonym.getText().length();
                resultSpan.setSpan(new TextClickableSpan(mContext, onItemClicklistener, synonym.getText()), accumLength,
                        accumLength + textLength,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                accumLength += textLength;

                if (synonym.getGen() != null && !synonym.getGen().isEmpty()){
                    genLength = " ".length() + synonym.getGen().length();
                } else {
                    genLength = 0;
                }

                resultSpan.setSpan(new GenClickableSpan(mContext), accumLength, accumLength + genLength,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                accumLength += genLength;

                commaLength = 0;
                if (accumLength != mTranslation.getRepresentSynonyms().length()) {
                    commaLength = 2;
                }
                resultSpan.setSpan(new CommaClickableSpan(mContext), accumLength, accumLength + commaLength,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                accumLength += commaLength;
            }
        }
        return resultSpan;
    }
}
