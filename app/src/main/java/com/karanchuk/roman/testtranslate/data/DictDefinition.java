package com.karanchuk.roman.testtranslate.data;

import java.util.List;

/**
 * Created by roman on 19.4.17.
 */

public class DictDefinition {
    private String mText;
    private String mTranscription;
    private List<PartOfSpeech> mPartsOfSpeech;

    public DictDefinition(String text, String transcription, List<PartOfSpeech> partsOfSpeech) {
        mText = text;
        mTranscription = transcription;
        mPartsOfSpeech = partsOfSpeech;
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

    public List<PartOfSpeech> getPartsOfSpeech() {
        return mPartsOfSpeech;
    }

    public void setPartsOfSpeech(List<PartOfSpeech> partsOfSpeech) {
        mPartsOfSpeech = partsOfSpeech;
    }


    @Override
    public String toString() {
        String result = mText.
                concat(" ").
                concat(mTranscription).
                concat("\n");
        for (PartOfSpeech pOfs : mPartsOfSpeech){
            result = result.concat(pOfs.toString()).concat("\n");
        }
        return result;
    }
}
