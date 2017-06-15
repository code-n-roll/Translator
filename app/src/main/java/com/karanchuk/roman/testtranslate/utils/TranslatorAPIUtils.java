package com.karanchuk.roman.testtranslate.utils;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.view.fragment.TranslatorFragment;
import com.karanchuk.roman.testtranslate.presentation.view.translator.TranslatorStateHolder;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by roman on 11.4.17.
 */

public class TranslatorAPIUtils {

    public static void getTranslate(final String translatedText,
                                    AssetManager manager,
                                    final String srcLang,
                                    String trgLang,
                                    final TextView tvTranslateResult,
                                    final RecyclerView rvTranslate,
                                    final TranslatorFragment.TranslationSaver saver,
                                    final List<TranslatedItem> historyTranslatedItems,
                                    final SharedPreferences settings
                                    )
            throws IOException{


        OkHttpClient client = new OkHttpClient();
        final Handler mHandler = new Handler(Looper.getMainLooper());


        JsonObject langs = JsonUtils.getJsonObjectFromFile(manager, "langs.json");


        final String srcLangAPI = langs.get(srcLang).getAsString();
        final String trgLangAPI = langs.get(trgLang).getAsString();
        final String translDirection = srcLangAPI.
                concat("-").
                concat(trgLangAPI);

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
                mHandler.post(() -> {
//                        TranslatedItem item = new TranslatedItem("srcLangAPI","trgLangAPI",
//                        srcLangUser,trgLangUser,"srcMean","trgMean","isFavor","dictDef");
//                        TranslatedItem item = new TranslatedItem(srcLangAPI,trgLangAPI,
//                                null,null,translatedText,null,null,null);
//                        if (!historyTranslatedItems.contains(item)) {
            TranslatorStateHolder.getInstance().notifyTranslatorAPIResult(false);
//                        } else {
//                            TranslatedItem newItem = historyTranslatedItems.get(historyTranslatedItems.indexOf(item));
//                            tvTranslateResult.setText(newItem.getTrgMeaning());

//                            List<Translation> newData = newItem.getDictDefinitionFromStringRepr(newItem.getDictDefinition()).getTranslations();
//                            ((TranslatorRecyclerAdapter)rvTranslate.getAdapter()).updateData(newData);


//                            saveToSharedPreferences(newItem);
//                            TranslatorStateHolder.getInstance().notifyTranslatorAPIResult(true);
//                        }
                });
            }

//            private void saveToSharedPreferences(TranslatedItem item){
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putString(EDITTEXT_DATA, item.getSrcMeaning());
//                editor.putString(SRC_LANG, item.getSrcLanguageForUser());
//                editor.putString(TRG_LANG, item.getTrgLanguageForUser());
//                editor.putString(TRANSL_RESULT, item.getTrgMeaning());
//                editor.putString(TRANSL_CONTENT, item.getDictDefinition());
//                editor.apply();
//            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String strResponse = response.body().string();
                final JsonObject jo = (JsonObject)new JsonParser().parse(strResponse);
                final String result = jo.get("text").getAsString();
                Log.d("api response", strResponse);
                Log.d("http response", response.toString());


                mHandler.post(() -> {
                    if (!result.isEmpty()) {
                        tvTranslateResult.setText(result);
                        DictionaryAPIUtils.lookup(mHandler, translatedText,translDirection,rvTranslate, saver);
                    }
                });

            }
        });

    }
}
