package com.karanchuk.roman.testtranslate.utils;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karanchuk.roman.testtranslate.data.DictDefinition;
import com.karanchuk.roman.testtranslate.data.PartOfSpeech;
import com.karanchuk.roman.testtranslate.data.Translation;
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorAPIHolder;
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment;
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorRecyclerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by roman on 11.4.17.
 */

public class DictionaryAPIUtils {
    public static void lookup(
            final Handler handler,
            String lookupText,
            String translDirection,
            final RecyclerView rvTranslate,
            final TranslatorFragment.TranslationSaver saver
    ){


        OkHttpClient client = new OkHttpClient();

        String url = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?"+
                "key=dict.1.1.20170410T015740Z.ee40f6ea33d8dc7b.e7d46a78203404678611c91d1ff0e4183b6aae13"+
                "&text="+lookupText+
                "&lang="+translDirection;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();
                final JsonObject jo = (JsonObject)new JsonParser().parse(strResponse);
                Log.d("api response", strResponse);
                Log.d("http response", response.toString());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<Translation> translations = new ArrayList<>();
                        DictDefinition dictDefinition = JsonUtils.getDictDefinitionFromJson(jo);
                        for (PartOfSpeech POS : dictDefinition.getPartsOfSpeech()){
                            for (Translation transl : POS.getTranslations()){
                                translations.add(transl);
                            }
                        }
                        TranslatorRecyclerAdapter adapter = (TranslatorRecyclerAdapter)rvTranslate.getAdapter();
                        adapter.updateData(translations);

                        saver.setDictDefinition(dictDefinition);
                        new Thread(saver).start();

                        TranslatorAPIHolder.getInstance().notifyTranslatorAPIResult(true);
                    }
                });
            }
        });
    }
}
