package com.karanchuk.roman.testtranslate.data.database.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roman on 18.6.17.
 */

public class Expression {
    @SerializedName("text")
    private String mText;

    @SerializedName("tr")
    private List<ExpressionTranslation> mExpressionTranslation;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public List<ExpressionTranslation> getExpressionTranslation() {
        return mExpressionTranslation;
    }

    public void setExpressionTranslation(List<ExpressionTranslation> expressionTranslation) {
        mExpressionTranslation = expressionTranslation;
    }

    @Override
    public String toString() {
        String result = mText + " \u2014 ";
        for (ExpressionTranslation exprTransl : mExpressionTranslation){
            result = result.concat(exprTransl.getText() + "\n");
        }
        return result;
    }
}
