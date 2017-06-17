package com.karanchuk.roman.testtranslate.presentation.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roman on 20.4.17.
 */

public class Synonym {
    @SerializedName("text")
    private String mText;

    @SerializedName("pos")
    private String mGen;

    public Synonym(final String text,
                   final String gen) {
        mText = text;
        mGen = gen;
    }


    public String getText() {
        return mText;
    }

    public void setText(final String text) {
        mText = text;
    }

    public String getGen() {
        return mGen;
    }

    public void setGen(final String gen) {
        mGen = gen;
    }

    @Override
    public String toString() {
        return mText.concat(" ").concat(mGen);
    }
}
