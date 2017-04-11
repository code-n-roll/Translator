package com.karanchuk.roman.testtranslate.utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by roman on 11.4.17.
 */

public class DictionaryAPIUtils {
    public static void lookup(String lookupText, String translDirection){
        OkHttpClient client = new OkHttpClient();

        String url = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?"+
                "key=dict.1.1.20170410T015740Z.ee40f6ea33d8dc7b.e7d46a78203404678611c91d1ff0e4183b6aae13"+
                "&lang="+lookupText+
                "&text="+translDirection;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
