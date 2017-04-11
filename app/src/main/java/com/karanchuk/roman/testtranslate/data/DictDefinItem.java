package com.karanchuk.roman.testtranslate.data;

/**
 * Created by roman on 11.4.17.
 */

public class DictDefinItem {
    private String mNumber;
    private String mTranslation;
    private String mMeaning;
    private String mExpression;


    public DictDefinItem(String number, String translation,
                         String meaning, String expression){
        mNumber = number;
        mTranslation = translation;
        mMeaning = meaning;
        mExpression = expression;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public String getTranslation() {
        return mTranslation;
    }

    public void setTranslation(String translation) {
        mTranslation = translation;
    }

    public String getMeaning() {
        return mMeaning;
    }

    public void setMeaning(String meaning) {
        mMeaning = meaning;
    }

    public String getExpression() {
        return mExpression;
    }

    public void setExpression(String expression) {
        mExpression = expression;
    }




}
