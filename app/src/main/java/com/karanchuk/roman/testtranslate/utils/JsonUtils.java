package com.karanchuk.roman.testtranslate.utils;

import android.content.res.AssetManager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created by roman on 18.4.17.
 */

public class JsonUtils {
    public static JsonObject readJson(AssetManager manager, String fileName){
        byte[] buffer = null;
        String s;
        JsonObject jo = new JsonObject();
        try {
            InputStream is = manager.open(fileName);
            buffer = new byte[is.available()];
            int temp = is.read(buffer);
        } catch (IOException e){
            e.printStackTrace();
        }
        if (buffer != null) {
            s = new String(buffer);
            jo = (JsonObject) new JsonParser().parse(s);
        }
        return jo;
    }
}
