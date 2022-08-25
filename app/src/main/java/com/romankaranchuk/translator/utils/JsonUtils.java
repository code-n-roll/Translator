package com.romankaranchuk.translator.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.romankaranchuk.translator.data.database.model.Language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class JsonUtils {
    public static JsonObject getJsonObjectFromAssetsFile(Context context, Gson gson, String filename){
        try {
            InputStream is = context.getAssets().open(filename);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(is));
            return gson.fromJson(jsonReader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Language> getLangsFromJson(JsonObject langsJson) {
        List<Language> items = new ArrayList<>();
        for (Map.Entry<String,JsonElement> o : langsJson.entrySet()){
            String lang = o.getKey();
            String abbr = o.getValue().getAsString();
            String firstCapitalize = lang.substring(0,1).toUpperCase().concat(lang.substring(1));
            items.add(new Language(firstCapitalize,abbr,false));
        }
        return items;
    }
}
