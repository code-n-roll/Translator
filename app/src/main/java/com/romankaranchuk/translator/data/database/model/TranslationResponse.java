package com.romankaranchuk.translator.data.database.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


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
