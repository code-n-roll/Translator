package com.karanchuk.roman.testtranslate.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roman on 19.4.17.
 */

public class PartOfSpeech {
    @SerializedName("text")
    private String mText;

    @SerializedName("ts")
    private String mTranscription;

    @SerializedName("pos")
    private String mName;

    @SerializedName("tr")
    private List<Translation> mTranslations;

    public PartOfSpeech(final String name,
                        final List<Translation> translations){
        mName = name;
        mTranslations = translations;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTranscription() {
        return mTranscription;
    }

    public void setTranscription(String transcription) {
        mTranscription = transcription;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }


    public List<Translation> getTranslations() {
        return mTranslations;
    }

    public void setTranslations(final List<Translation> translations) {
        mTranslations = translations;
    }

    @Override
    public String toString() {
        String result = mName.concat("\n");
        for (Translation t : mTranslations){
            result = result.concat(t.toString()).concat("\n");
        }
        return result;
    }
}
