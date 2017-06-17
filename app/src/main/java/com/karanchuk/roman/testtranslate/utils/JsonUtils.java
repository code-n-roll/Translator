package com.karanchuk.roman.testtranslate.utils;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;
import com.karanchuk.roman.testtranslate.presentation.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.presentation.model.Synonym;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 18.4.17.
 */

public class JsonUtils {
    public static JsonObject getJsonObjectFromFile(final AssetManager manager,
                                                   final String fileName){
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

    public static DictDefinition getDictDefinitionFromJson(final JsonObject jsonObject){
        JsonElement dictDefJSON = jsonObject.get("def");
        List<PartOfSpeech> dictDefinition = new ArrayList<>();
        if (dictDefJSON != null) {
            for (JsonElement pOfsJSON : dictDefJSON.getAsJsonArray()) {
                JsonElement translationsJSON = pOfsJSON.getAsJsonObject().get("tr");
                int i = 1;
                ArrayList<Translation> translations = new ArrayList<>();
                for (JsonElement translationJSON : translationsJSON.getAsJsonArray()) {
                    JsonElement synonymsJSON = translationJSON.getAsJsonObject().get("syn");
                    String firstText = "";
                    if (translationJSON.getAsJsonObject().get("text") != null)
                        firstText = translationJSON.getAsJsonObject().get("text").getAsString();

                    String firstGen = "";
                    if (translationJSON.getAsJsonObject().get("gen") != null)
                        firstGen = translationJSON.getAsJsonObject().get("gen").getAsString();
                    Synonym firstSynonym = new Synonym(firstText, firstGen);
                    List<Synonym> synonyms = new ArrayList<>();
                    synonyms.add(firstSynonym);
                    if (synonymsJSON != null) {
                        for (JsonElement synonymJSON : synonymsJSON.getAsJsonArray()) {
                            JsonObject synObjJSON = synonymJSON.getAsJsonObject();
                            String text = "", gen = "";
                            if (synObjJSON.get("text") != null)
                                text = synObjJSON.get("text").getAsString();
                            if (synObjJSON.get("gen") != null)
                                gen = synObjJSON.get("gen").getAsString();
                            Synonym synonym = new Synonym(text, gen);
                            synonyms.add(synonym);
                        }
                    }

                    JsonElement meaningsJSON = translationJSON.getAsJsonObject().get("mean");
                    String meanings = "(";
                    if (meaningsJSON != null) {
                        for (JsonElement meaningJSON : meaningsJSON.getAsJsonArray()) {
                            JsonObject meanObjJSON = meaningJSON.getAsJsonObject();
                            meanings = meanings.concat(meanObjJSON.get("text").getAsString()).concat(", ");
                        }
                    }
                    if (meanings.length() >= 2) {
                        meanings = meanings.substring(0, meanings.length() - 2).concat(")");
                    } else {
                        meanings = "";
                    }

                    JsonElement exprsJSON = translationJSON.getAsJsonObject().get("ex");
                    String exprs = "";
                    if (exprsJSON != null) {
                        for (JsonElement exprJSON : exprsJSON.getAsJsonArray()) {
                            JsonObject exprObjJSON = exprJSON.getAsJsonObject();
                            exprs = exprs.concat(exprObjJSON.get("text").getAsString()).
                                    concat(" \u2014 ").
                                    concat(exprObjJSON.get("tr").
                                            getAsJsonArray().get(0).getAsJsonObject().
                                            get("text").getAsString()).
                                    concat("\n");
                        }
                    }
                    if (!exprs.isEmpty()) {
                        exprs = exprs.substring(0, exprs.length() - 1);
                    }


//                    translations.add(new Translation(String.valueOf(i), synonyms, meanings, exprs,
//                            JsonUtils.getRepresentSynonyms(synonyms)));
                    ++i;
                }
                String namePOS = pOfsJSON.getAsJsonObject().get("pos").getAsString();
                dictDefinition.add(new PartOfSpeech(namePOS, translations));
            }
        }
        String text = "";
        String ts = "";
        if (dictDefJSON != null && dictDefJSON.getAsJsonArray().size() != 0) {
            JsonElement textJSON = dictDefJSON.getAsJsonArray().get(0).getAsJsonObject().get("text");
            if (textJSON != null)
                text = textJSON.getAsString();

            JsonElement tsJSON = dictDefJSON.getAsJsonArray().get(0).getAsJsonObject().get("ts");
            if (tsJSON != null)
                ts = "[".concat(tsJSON.getAsString()).concat("]");
        }
        DictDefinition dictDef = new DictDefinition(text,ts,dictDefinition,jsonObject.toString());
        Log.d("parse_json_response", dictDef.toString());
        return dictDef;
    }

    public static String getRepresentSynonyms(final List<Synonym> synonyms){
        String result = "";
        for (Synonym synonym : synonyms) {
            result = result.concat(
                    synonym.getText().
                            concat(" ").
                            concat(synonym.getGen()).
                            concat(", "));
        }
        if (result.length() >= 2) {
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

}
