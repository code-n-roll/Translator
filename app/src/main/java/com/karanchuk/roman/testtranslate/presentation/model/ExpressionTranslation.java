package com.karanchuk.roman.testtranslate.presentation.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roman on 18.6.17.
 */

public class ExpressionTranslation {
    @SerializedName("text")
    private String mText;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
