package com.karanchuk.roman.testtranslate.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roman on 11.4.17.
 */

public class Translation {
    private String mNumber;

    @SerializedName("text")
    private String mText;

    @SerializedName("gen")
    private String mGen;

    @SerializedName("syn")
    private List<Synonym> mSynonyms;

    @SerializedName("mean")
    private List<Meaning> mMeanings;

    @SerializedName("ex")
    private List<Expression> mExpressions;

    private String mRepresentSynonyms;
    private String mRepresentMeanings;
    private String mRepresentExpressions;

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

    public String getRepresentExpressions(){
        mRepresentExpressions = "";
        if (mExpressions != null) {
            for (Expression expression : mExpressions) {
                mRepresentExpressions = mRepresentExpressions.concat(expression.toString());
            }
        }
        int length = mRepresentExpressions.length();
        if (length >= 1){
            mRepresentExpressions = mRepresentExpressions.substring(0, length-1);
        }
        return mRepresentExpressions;
    }

    public String getRepresentMeanings() {
        if (mMeanings != null) {
            mRepresentMeanings = "(";
            for (Meaning meaning : mMeanings) {
                mRepresentMeanings = mRepresentMeanings.concat(meaning.toString() + ", ");
            }
        }
        return mRepresentMeanings.substring(0, mRepresentMeanings.length()-2) + ")";
    }

    public String getGen() {
        return mGen;
    }

    public void setGen(String gen) {
        mGen = gen;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setRepresentExpressions(String representExpressions) {
        mRepresentExpressions = representExpressions;
    }

    public void setRepresentMeanings(String representMeanings) {
        mRepresentMeanings = representMeanings;
    }

    public void setRepresentSynonyms(final String representSynonyms) {
        mRepresentSynonyms = representSynonyms;
    }

    public String getRepresentSynonyms(){
        if (mText != null){
            mRepresentSynonyms = mText;
        }
        if (mGen != null) {
            mRepresentSynonyms += " " + mGen.concat(", ");
        } else {
            if (mSynonyms != null){
                mRepresentSynonyms += ", ";
            }
        }
        if (mSynonyms != null) {
            for (Synonym synonym : mSynonyms) {
                if (synonym.getGen() != null) {
                    mRepresentSynonyms += synonym.toString() + " " + synonym.getGen() + ", ";
                } else {
                    mRepresentSynonyms += synonym.toString() + ", ";
                }
            }
        }
        int length = mRepresentSynonyms.length();
        if (mRepresentSynonyms.endsWith(", ")){
            mRepresentSynonyms = mRepresentSynonyms.substring(0, length - 2);
        } else if (mRepresentSynonyms.endsWith(",")){
            mRepresentSynonyms = mRepresentSynonyms.substring(0, length - 1);
        }
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
        if (mSynonyms != null) {
            for (Synonym synonym : mSynonyms) {
                if (synonym.getGen() != null && !synonym.getGen().isEmpty()) {
                    result += synonym.toString() + " " + synonym.getGen() + ", ";
                } else {
                    result += synonym.toString() + ", ";
                }
            }
        }
        if (result.length() >= 2){
            result = result.substring(0, result.length()-2);
        }
        result = result.concat("\n").concat("(");
        if (mMeanings != null) {
            for (Meaning meaning : mMeanings) {
                result = result.concat(meaning.toString() + " ");
            }
        }
        if (result.length() >= 1){
            result = result.substring(0, result.length()-1);
        }
        result = result.concat(")").concat("\n");
        if (mExpressions != null) {
            for (Expression expression : mExpressions) {
                result = result.concat(expression.toString());
            }
        }
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
