package com.karanchuk.roman.testtranslate.data;

/**
 * Created by roman on 20.4.17.
 */

public class Synonym {
    private String mText;
    private String mGen;

    public Synonym(String text, String gen) {
        mText = text;
        mGen = gen;
    }


    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getGen() {
        return mGen;
    }

    public void setGen(String gen) {
        mGen = gen;
    }

    @Override
    public String toString() {
        return mText.concat(" ").concat(mGen);
    }
}
