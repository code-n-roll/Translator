package com.karanchuk.roman.testtranslate.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 19.4.17.
 */

public class DictDefinition {
    private String mText;
    private String mTranscription;

    @SerializedName("def")
    private List<PartOfSpeech> mPartsOfSpeech;

    private String mJsonToStringRepr;

    public DictDefinition(final String text,
                          final String transcription,
                          final List<PartOfSpeech> partsOfSpeech,
                          final String jsonToStringRepr) {
        mText = text;
        mTranscription = transcription;
        mPartsOfSpeech = partsOfSpeech;
        mJsonToStringRepr = jsonToStringRepr;
    }



    public String getJsonToStringRepr() {
        return mJsonToStringRepr;
    }

    public void setJsonToStringRepr(final String jsonToStringRepr) {
        mJsonToStringRepr = jsonToStringRepr;
    }

    public String getText() {
        return mText;
    }

    public void setText(final String text) {
        mText = text;
    }

    public String getTranscription() {
        return mTranscription;
    }

    public void setTranscription(final String transcription) {
        mTranscription = transcription;
    }

    public List<PartOfSpeech> getPartsOfSpeech() {
        return mPartsOfSpeech;
    }

    public void setPartsOfSpeech(final List<PartOfSpeech> partsOfSpeech) {
        mPartsOfSpeech = partsOfSpeech;
    }


    @Override
    public String toString() {
        String result = "";
        for (PartOfSpeech pOfs : mPartsOfSpeech){
            result = result.concat(pOfs.toString()).concat("\n");
        }
        return result;
    }

    public List<Translation> getTranslations(){
        final List<Translation> translations = new ArrayList<>();
        for (PartOfSpeech POS : mPartsOfSpeech){
            translations.addAll(POS.getTranslations());
        }
        return translations;
    }
}
