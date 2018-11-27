package com.karanchuk.roman.testtranslate.data.database.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl;
import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;

import java.util.List;
import java.util.Map;

import static com.karanchuk.roman.testtranslate.common.Constants.CUR_TRANSLATED_ITEM;
import static com.karanchuk.roman.testtranslate.common.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.common.Constants.LANGS_FILE_NAME;
import static com.karanchuk.roman.testtranslate.common.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.common.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRG_LANG;

/**
 * Created by roman on 22.6.17.
 */

public class TranslationSaver implements Runnable{
    private TranslatedItem mCurTranslatedItem;
    private Gson mGson;
    private Map<String, Object> mSavedData;
    private JsonObject mLanguagesMap;
    private TranslatorRepositoryImpl mRepository;
    private List<TranslatedItem> mHistoryTranslatedItems;

    public TranslationSaver(Context context) {
        mGson = new Gson();
        mLanguagesMap = JsonUtils.getJsonObjectFromAssetsFile(context, LANGS_FILE_NAME);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        mCurTranslatedItem = mGson.fromJson(settings.getString(CUR_TRANSLATED_ITEM, ""),
                TranslatedItem.class);


        TranslatorRepository localDataSource = TranslatorLocalRepository.getInstance(context);
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(
                TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);
    }

    public TranslatedItem getCurTranslatedItem() {
        return mCurTranslatedItem;
    }

    public Map<String, Object> getSavedData() {
        return mSavedData;
    }

    public void setSavedData(Map<String, Object> savedData) {
        mSavedData = savedData;
    }


    @Override
    public void run() {
        mCurTranslatedItem = new TranslatedItem(
                mLanguagesMap.get(((String) mSavedData.get(SRC_LANG)).toLowerCase()).getAsString(),
                mLanguagesMap.get(((String) mSavedData.get(TRG_LANG)).toLowerCase()).getAsString(),
                (String) mSavedData.get(SRC_LANG),
                (String) mSavedData.get(TRG_LANG),
                (String) mSavedData.get(EDITTEXT_DATA),
                (String) mSavedData.get(TRANSL_RESULT),
                "false",
                mGson.toJson(mSavedData.get(TRANSL_CONTENT)));
        if (!mHistoryTranslatedItems.contains(mCurTranslatedItem)) {
            mRepository.saveTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
        } else {
            final int index = mHistoryTranslatedItems.indexOf(mCurTranslatedItem);
            mCurTranslatedItem.setIsFavorite(mHistoryTranslatedItems.get(index).getIsFavorite());
            mRepository.saveTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
            if (mCurTranslatedItem.isFavorite()){
                mRepository.deleteTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES, mHistoryTranslatedItems.get(index));
                mRepository.saveTranslatedItem(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES, mCurTranslatedItem);
            }
        }
    }
}
