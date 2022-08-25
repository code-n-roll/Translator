package com.romankaranchuk.translator.data.database.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.romankaranchuk.translator.common.Constants;
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository;
import com.romankaranchuk.translator.data.datasource.LanguagesDataSource;

import java.util.HashMap;
import java.util.Map;


public class TextDataStorageImpl implements TextDataStorage {
    private SharedPreferences mSettings;
    private TranslationSaver mSaver;
    private Gson mGson;

    public TextDataStorageImpl(Context context, Gson gson, SharedPreferences sharedPreferences, LanguagesDataSource languagesDataSource, TranslatorLocalRepository translatorLocalRepository) {
        mSettings = sharedPreferences;
        mGson = gson;
        mSaver = new TranslationSaver(context, mGson, sharedPreferences,languagesDataSource, translatorLocalRepository);
    }

    @Override
    public void saveToSharedPreferences(Map<String, Object> savedData) {
        final SharedPreferences.Editor editor = mSettings.edit();
        HashMap<String, Object> data = (HashMap<String, Object>) savedData;
        editor.putString(Constants.EDITTEXT_DATA, (String) data.get(Constants.EDITTEXT_DATA));
        editor.putString(Constants.SRC_LANG, (String) data.get(Constants.SRC_LANG));
        editor.putString(Constants.TRG_LANG, (String) data.get(Constants.TRG_LANG));
        editor.putString(Constants.TRANSL_RESULT,(String) data.get(Constants.TRANSL_RESULT));
        editor.putString(Constants.TRANSL_CONTENT, mGson.toJson(data.get(Constants.TRANSL_CONTENT)));
        editor.putString(Constants.CUR_TRANSLATED_ITEM, (String) data.get(Constants.CUR_TRANSLATED_ITEM));
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
