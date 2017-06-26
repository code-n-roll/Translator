package com.karanchuk.roman.testtranslate.data.storage;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;

import java.util.List;
import java.util.Map;

import static com.karanchuk.roman.testtranslate.presentation.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRG_LANG;

/**
 * Created by roman on 22.6.17.
 */

public class TranslationSaver implements Runnable{
    private TranslatedItem mCurTranslatedItem;
    private Gson mGson;
    private Map<String, Object> mSavedData;
    private JsonObject mLanguagesMap;
    private TranslatorRepository mRepository;
    private List<TranslatedItem> mHistoryTranslatedItems;

    public TranslationSaver(Context context) {
        mGson = new Gson();
        mLanguagesMap = JsonUtils.getJsonObjectFromAssetsFile(context, "langs.json");

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(context);
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(
                TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);
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
            mRepository.saveTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
        } else {
            final int index = mHistoryTranslatedItems.indexOf(mCurTranslatedItem);
            mCurTranslatedItem.setIsFavorite(mHistoryTranslatedItems.get(index).getIsFavorite());
            mRepository.saveTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
            if (mCurTranslatedItem.isFavorite()){
                mRepository.deleteTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES, mHistoryTranslatedItems.get(index));
                mRepository.saveTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES, mCurTranslatedItem);
            }
        }
    }
}
