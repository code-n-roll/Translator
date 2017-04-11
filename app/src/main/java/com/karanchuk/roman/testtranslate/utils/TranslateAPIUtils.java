package com.karanchuk.roman.testtranslate.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by roman on 11.4.17.
 */

public class TranslateAPIUtils {

    public static void getTranslate(String translatedText,
                                      String translDirection,
                                      final TextView tvTranslateResult) throws IOException{
        OkHttpClient client = new OkHttpClient();
        final Handler mHandler = new Handler(Looper.getMainLooper());

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
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();
                JsonObject jo = (JsonObject)new JsonParser().parse(strResponse);
                final String result = jo.get("text").getAsString();
                Log.d("api response", strResponse);
                Log.d("http response", response.toString());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvTranslateResult.setText(result);
                    }
                });

            }
        });

    }
}
