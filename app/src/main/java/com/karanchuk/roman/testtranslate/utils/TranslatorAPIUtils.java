package com.karanchuk.roman.testtranslate.utils;

import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karanchuk.roman.testtranslate.data.DictDefinition;
import com.karanchuk.roman.testtranslate.data.PartOfSpeech;
import com.karanchuk.roman.testtranslate.data.Translation;
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

import static com.karanchuk.roman.testtranslate.utils.DictionaryAPIUtils.lookup;

/**
 * Created by roman on 11.4.17.
 */

public class TranslatorAPIUtils {

    public static void getTranslate(final String translatedText,
                                    AssetManager manager,
                                    String srcLang,
                                    String trgLang,
                                    final TextView tvTranslateResult,
                                    final RecyclerView rvTranslate,
                                    final TranslatorFragment.TranslationSaver saver
    )
            throws IOException{
        OkHttpClient client = new OkHttpClient();
        final Handler mHandler = new Handler(Looper.getMainLooper());

        JsonObject langs = JsonUtils.getJsonObjectFromFile(manager, "langs.json");


        final String translDirection = langs.get(srcLang.toLowerCase()).getAsString().
                concat("-").
                concat(langs.get(trgLang.toLowerCase()).getAsString());

        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate?"+
                "key=trnsl.1.1.20170410T011338Z.07c9f77e0dd5777b.400bcbcacf7bdafcdaa1a38cfa576dbc9fae4010"+
                "&text="+translatedText+
                "&lang="+translDirection;
//                "& [format=<формат текста>]"+
//                "& [options=<опции перевода>]"+
//                "& [callback=<имя callback-функции>]";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String strResponse = response.body().string();
                final JsonObject jo = (JsonObject)new JsonParser().parse(strResponse);
                final String result = jo.get("text").getAsString();
                Log.d("api response", strResponse);
                Log.d("http response", response.toString());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!result.isEmpty()) {
                            tvTranslateResult.setText(result);
                            DictionaryAPIUtils.lookup(mHandler, translatedText,translDirection,rvTranslate, saver);
                        }

                    }
                });

            }
        });

    }
}
