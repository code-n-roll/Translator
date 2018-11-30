package com.karanchuk.roman.testtranslate.data.database.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.karanchuk.roman.testtranslate.common.Constants.CUR_TRANSLATED_ITEM;
import static com.karanchuk.roman.testtranslate.common.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.common.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.common.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRG_LANG;

/**
 * Created by roman on 22.6.17.
 */

public class TextDataStorageImpl implements TextDataStorage {
    private SharedPreferences mSettings;
    private TranslationSaver mSaver;
    private Gson mGson;

    public TextDataStorageImpl(Context context, Gson gson) {
        mSettings = context.getSharedPreferences(PREFS_NAME, 0);
        mGson = gson;
        mSaver = new TranslationSaver(context, mGson);
    }

    @Override
    public void saveToSharedPreferences(Map<String, Object> savedData) {
        final SharedPreferences.Editor editor = mSettings.edit();
        HashMap<String, Object> data = (HashMap<String, Object>) savedData;
        editor.putString(EDITTEXT_DATA, (String) data.get(EDITTEXT_DATA));
        editor.putString(SRC_LANG, (String) data.get(SRC_LANG));
        editor.putString(TRG_LANG, (String) data.get(TRG_LANG));
        editor.putString(TRANSL_RESULT,(String) data.get(TRANSL_RESULT));
        editor.putString(TRANSL_CONTENT, mGson.toJson(data.get(TRANSL_CONTENT)));
        editor.putString(CUR_TRANSLATED_ITEM, (String) data.get(CUR_TRANSLATED_ITEM));
//        DictDefinition curDictDefinition = (DictDefinition) data.get(TRANSL_CONTENT);

//        if (mSaver.getCurTranslatedItem()!= null && mSaver.getCurTranslatedItem().getIsFavorite()!=null) {
//            editor.putString(IS_FAVORITE, mSaver.getCurTranslatedItem().getIsFavorite());
//        } else {
//            editor.putString(IS_FAVORITE, String.valueOf(false));
//        }
//        editor.putString(TRANSL_CONTENT, mGson.toJson(curDictDefinition));

        editor.apply();
    }
}
