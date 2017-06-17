package com.karanchuk.roman.testtranslate.presentation.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roman on 11.4.17.
 */

public class Translation {
    private String mNumber;

    @SerializedName("syn")
    private List<Synonym> mSynonyms;

    @SerializedName("mean")
    private List<Meaning> mMeanings;

    @SerializedName("ex")
    private List<Expression> mExpressions;

    private String mRepresentSynonyms;

    public Translation(final String number,
                       final List<Synonym> synonyms,
                       final List<Meaning> meanings,
                       final List<Expression> expressions,
                       final String representSynonyms) {
        mNumber = number;
        mSynonyms = synonyms;
        mMeanings = meanings;
        mExpressions = expressions;
        mRepresentSynonyms = representSynonyms;
    }

    public void setRepresentSynonyms(final String representSynonyms) {
        mRepresentSynonyms = representSynonyms;
    }

    public String getRepresentSynonyms(){
        return mRepresentSynonyms;
    }

    public List<Synonym> getSynonyms() {
        return mSynonyms;
    }

    public void setSynonyms(final List<Synonym> synonyms) {
        mSynonyms = synonyms;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(final String number) {
        mNumber = number;
    }

    @Override
    public String toString() {
        String result = "";
        if (mNumber != null) {
            result = mNumber.concat(" ");
        }
        for (Synonym synonym : mSynonyms){
            result = result.concat(
                    synonym.getText().
                    concat(" ").
                    concat(synonym.getGen()).
                    concat(", "));
        }
        if (result.length() >= 2){
            result = result.substring(0, result.length()-2);
        }
        if (!mMeanings.isEmpty())
            result = result.concat("\n").concat(mMeanings.toString());
        if (!mExpressions.isEmpty())
            result = result.concat("\n").concat(mExpressions.toString());
        return result;
    }

    public List<Meaning> getMeanings() {
        return mMeanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        mMeanings = meanings;
    }

    public List<Expression> getExpressions() {
        return mExpressions;
    }

    public void setExpressions(List<Expression> expressions) {
        mExpressions = expressions;
    }
}
