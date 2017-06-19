package com.karanchuk.roman.testtranslate.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by roman on 18.4.17.
 */

public class JsonUtils {
    public static JsonObject getJsonObjectFromAssetsFile(Context context, String filename){
        try {
            InputStream is = context.getAssets().open(filename);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(is));
            return new Gson().fromJson(jsonReader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
