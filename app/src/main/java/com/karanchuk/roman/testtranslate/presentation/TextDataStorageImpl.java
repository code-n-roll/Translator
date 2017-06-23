package com.karanchuk.roman.testtranslate.presentation;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;

import java.util.HashMap;
import java.util.Map;

import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_DICT_DEFINITION;
import static com.karanchuk.roman.testtranslate.presentation.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.presentation.Constants.IS_FAVORITE;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRG_LANG;

/**
 * Created by roman on 22.6.17.
 */

public class TextDataStorageImpl implements TextDataStorage {
    private SharedPreferences mSettings;
    private Gson mGson;
    private TranslationSaver mSaver;

    public TextDataStorageImpl(Context context) {
        mSettings = context.getSharedPreferences(PREFS_NAME, 0);
        mGson = new Gson();
        mSaver = new TranslationSaver(context);
    }

    @Override
    public void saveToSharedPreferences(Map<String, Object> savedData) {
        final SharedPreferences.Editor editor = mSettings.edit();
        HashMap<String, Object> data = (HashMap<String, Object>) savedData;
        editor.putString(EDITTEXT_DATA, (String) data.get(EDITTEXT_DATA));
        editor.putString(SRC_LANG, (String) data.get(SRC_LANG));
        editor.putString(TRG_LANG, (String) data.get(TRG_LANG));
        editor.putString(TRANSL_RESULT,(String) data.get(TRANSL_RESULT));
        DictDefinition curDictDefinition = (DictDefinition) data.get(CUR_DICT_DEFINITION);

        if (mSaver.getCurTranslatedItem()!= null && mSaver.getCurTranslatedItem().getIsFavorite()!=null) {
            editor.putString(IS_FAVORITE, mSaver.getCurTranslatedItem().getIsFavorite());
        } else {
            editor.putString(IS_FAVORITE, String.valueOf(false));
        }
        if (mSaver.getDictDefinition() != null) {
            editor.putString(TRANSL_CONTENT, mGson.toJson(mSaver.getDictDefinition()));
        } else if (curDictDefinition != null){
            editor.putString(TRANSL_CONTENT, mGson.toJson(curDictDefinition));
        } else {
            editor.putString(TRANSL_CONTENT, "");
        }

        editor.apply();
    }
}
