package com.karanchuk.roman.testtranslate.presentation.model;

import java.util.List;

/**
 * Created by roman on 11.4.17.
 */

public class Translation {
    private String mNumber;
    private List<Synonym> mSynonyms;
    private String mMeanings;
    private String mExpressions;
    private String mRepresentSynonyms;

    public Translation(final String number,
                       final List<Synonym> synonyms,
                       final String meanings,
                       final String expressions,
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


    public String getMeanings() {
        return mMeanings;
    }

    public void setMeanings(final String meanings) {
        mMeanings = meanings;
    }

    public String getExpressions() {
        return mExpressions;
    }

    public void setExpressions(final String expressions) {
        mExpressions = expressions;
    }


    @Override
    public String toString() {
        String result = "".concat(
                mNumber.concat(" "));
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
            result = result.concat("\n").concat(mMeanings);
        if (!mExpressions.isEmpty())
            result = result.concat("\n").concat(mExpressions);
        return result;
    }
}
