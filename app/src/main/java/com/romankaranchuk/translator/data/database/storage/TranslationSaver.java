package com.romankaranchuk.translator.data.database.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.romankaranchuk.translator.common.Constants;
import com.romankaranchuk.translator.data.database.TablePersistenceContract;
import com.romankaranchuk.translator.data.database.model.TranslatedItem;
import com.romankaranchuk.translator.data.database.repository.TranslatorLocalRepository;
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository;
import com.romankaranchuk.translator.data.datasource.LanguagesLocalDataSource;

import java.util.List;
import java.util.Map;


public class TranslationSaver implements Runnable {
    private TranslatedItem mCurTranslatedItem;
    private Map<String, Object> mSavedData;
    private TranslatorRepository mRepository;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private Gson mGson;
    private Context context;
    private LanguagesLocalDataSource languagesLocalDataSource;

    public TranslationSaver(Context context, Gson gson, SharedPreferences sharedPreferences, LanguagesLocalDataSource languagesLocalDataSource, TranslatorLocalRepository translatorLocalRepository) {
        mGson = gson;
        this.context = context;
        mCurTranslatedItem = mGson.fromJson(sharedPreferences.getString(Constants.CUR_TRANSLATED_ITEM, ""), TranslatedItem.class);
        this.languagesLocalDataSource = languagesLocalDataSource;
        mRepository = translatorLocalRepository;
        mHistoryTranslatedItems = mRepository.getTranslatedItems(TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);
    }

    @Nullable
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
        String sourceLang = ((String) mSavedData.get(Constants.SRC_LANG)).toLowerCase();
        String targetLang = ((String) mSavedData.get(Constants.TRG_LANG)).toLowerCase();
        List<String> mLanguagesMap = languagesLocalDataSource.getLanguages(sourceLang, targetLang);
        mCurTranslatedItem = new TranslatedItem(
                mLanguagesMap.get(0),
                mLanguagesMap.get(1),
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
