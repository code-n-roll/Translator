package com.karanchuk.roman.testtranslate.data.database.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roman on 18.6.17.
 */

public class Meaning {
    @SerializedName("text")
    private String mText;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    public String toString() {
        return mText;
    }
}
