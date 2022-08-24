package com.romankaranchuk.translator.data.database.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.romankaranchuk.translator.common.Constants;
import com.romankaranchuk.translator.data.database.TablePersistenceContract;
import com.romankaranchuk.translator.data.database.model.TranslatedItem;
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository;
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository;
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl;
import com.romankaranchuk.translator.utils.JsonUtils;

import java.util.List;
import java.util.Map;


public class TranslationSaver implements Runnable {
    private TranslatedItem mCurTranslatedItem;
    private Map<String, Object> mSavedData;
    private JsonObject mLanguagesMap;
    private TranslatorRepositoryImpl mRepository;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private Gson mGson;

    public TranslationSaver(Context context, Gson gson) {
        mGson = gson;
        mLanguagesMap = JsonUtils.getJsonObjectFromAssetsFile(context, mGson, Constants.LANGS_FILE_NAME);

        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        mCurTranslatedItem = mGson.fromJson(settings.getString(Constants.CUR_TRANSLATED_ITEM, ""), TranslatedItem.class);

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
                mLanguagesMap.get(((String) mSavedData.get(Constants.SRC_LANG)).toLowerCase()).getAsString(),
                mLanguagesMap.get(((String) mSavedData.get(Constants.TRG_LANG)).toLowerCase()).getAsString(),
                (String) mSavedData.get(Constants.SRC_LANG),
                (String) mSavedData.get(Constants.TRG_LANG),
                (String) mSavedData.get(Constants.EDITTEXT_DATA),
                (String) mSavedData.get(Constants.TRANSL_RESULT),
                "false",
                mGson.toJson(mSavedData.get(Constants.TRANSL_CONTENT)));
        if (!mHistoryTranslatedItems.contains(mCurTranslatedItem)) {
            mRepository.saveTranslatedItem(
                    TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
        } else {
            final int index = mHistoryTranslatedItems.indexOf(mCurTranslatedItem);
            mCurTranslatedItem.setIsFavorite(mHistoryTranslatedItems.get(index).getIsFavorite());
            mRepository.saveTranslatedItem(
                    TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
            if (mCurTranslatedItem.isFavorite()){
                mRepository.deleteTranslatedItem(
                        TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES,
                        mHistoryTranslatedItems.get(index));
                mRepository.saveTranslatedItem(
                        TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES, mCurTranslatedItem);
            }
        }
    }
}
