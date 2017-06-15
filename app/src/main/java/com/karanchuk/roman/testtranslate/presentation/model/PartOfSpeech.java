package com.karanchuk.roman.testtranslate.presentation.model;

import java.util.List;

/**
 * Created by roman on 19.4.17.
 */

public class PartOfSpeech {
    private String mName;
    private List<Translation> mTranslations;

    public PartOfSpeech(final String name,
                        final List<Translation> translations){
        mName = name;
        mTranslations = translations;
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
