package com.karanchuk.roman.testtranslate.presentation.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roman on 17.6.17.
 */

public class TranslationResponse {
    @SerializedName("text")
    private List<String> text;

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }
}
