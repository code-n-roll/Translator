package com.romankaranchuk.translator.data.database.model;

import com.google.gson.Gson;

import java.util.UUID;

public class TranslatedItem {
    private String mId;
    private String mSrcLanguageForAPI;
    private String mTrgLanguageForAPI;
    private String mSrcLanguageForUser;
    private String mTrgLanguageForUser;
    private String mSrcMeaning;
    private String mTrgMeaning;
    private String mIsFavorite;
    private String mDictDefinitionJSON;

    public TranslatedItem(final TranslatedItem translatedItem){
        mId = translatedItem.getId();
        mSrcLanguageForAPI = translatedItem.getSrcLanguageForAPI();
        mTrgLanguageForAPI = translatedItem.getTrgLanguageForAPI();
        mSrcLanguageForUser = translatedItem.getSrcLanguageForAPI();
        mTrgLanguageForUser = translatedItem.getTrgLanguageForAPI();
        mSrcMeaning = translatedItem.getSrcMeaning();
        mTrgMeaning = translatedItem.getTrgMeaning();
        mIsFavorite = translatedItem.getIsFavorite();
        mDictDefinitionJSON = translatedItem.getDictDefinitionJSON();

    }

    public TranslatedItem(final String id, String srcLanguageForAPI,
                          final String trgLanguageForAPI,
                          final String srcLanguageForUser,
                          final String trgLanguageForUser,
                          final String srcMeaning,
                          final String trgMeaning,
                          final String isFavorite,
                          final String dictDefinitionJSON) {
        mId = id;
        mSrcLanguageForAPI = srcLanguageForAPI;
        mTrgLanguageForAPI = trgLanguageForAPI;
        mSrcLanguageForUser = srcLanguageForUser;
        mTrgLanguageForUser = trgLanguageForUser;
        mSrcMeaning = srcMeaning;
        mTrgMeaning = trgMeaning;
        mIsFavorite = isFavorite;
        mDictDefinitionJSON = dictDefinitionJSON;
    }



    public TranslatedItem(final String srcLanguageForAPI,
                          final String trgLanguageForAPI,
                          final String srcLanguageForUser,
                          final String trgLanguageForUser,
                          final String srcMeaning,
                          final String trgMeaning,
                          final String isFavorite,
                          final String dictDefinitionJSON){
        this(UUID.randomUUID().toString(), srcLanguageForAPI,
                trgLanguageForAPI, srcLanguageForUser,
                trgLanguageForUser, srcMeaning,
                trgMeaning, isFavorite,
                dictDefinitionJSON);
    }

    public String getId() {
        return mId;
    }

    public void setId(final String id) {
        mId = id;
    }

    public String getSrcLanguageForAPI() {
        return mSrcLanguageForAPI;
    }

    public void setSrcLanguageForAPI(final String srcLanguageForAPI) {
        mSrcLanguageForAPI = srcLanguageForAPI;
    }

    public String getTrgLanguageForAPI() {
        return mTrgLanguageForAPI;
    }

    public void setTrgLanguageForAPI(final String trgLanguageForAPI) {
        mTrgLanguageForAPI = trgLanguageForAPI;
    }

    public String getSrcLanguageForUser() {
        return mSrcLanguageForUser;
    }

    public void setSrcLanguageForUser(final String srcLanguageForUser) {
        mSrcLanguageForUser = srcLanguageForUser;
    }

    public String getTrgLanguageForUser() {
        return mTrgLanguageForUser;
    }

    public void setTrgLanguageForUser(final String trgLanguageForUser) {
        mTrgLanguageForUser = trgLanguageForUser;
    }

    public String getSrcMeaning() {
        return mSrcMeaning;
    }

    public void setSrcMeaning(final String srcMeaning) {
        mSrcMeaning = srcMeaning;
    }

    public String getTrgMeaning() {
        return mTrgMeaning;
    }

    public void setTrgMeaning(final String trgMeaning) {
        mTrgMeaning = trgMeaning;
    }

    public String getIsFavorite() {
        return mIsFavorite;
    }

    public boolean isFavorite() {
        return mIsFavorite.equals("1");
    }

    public void isFavoriteUp(final boolean isFavoriteUp){
        if (isFavoriteUp){
            mIsFavorite = "1";
        } else {
            mIsFavorite = "0";
        }
    }

    public void setIsFavorite(final String isFavorite) {
        mIsFavorite = isFavorite;
    }


    public String getDictDefinitionJSON() {
        return mDictDefinitionJSON;
    }

    public void setDictDefinitionJSON(final String dictDefinitionJSON) {
        mDictDefinitionJSON = dictDefinitionJSON;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TranslatedItem) {
            final TranslatedItem item = (TranslatedItem) obj;
            if (mSrcMeaning.equals(item.mSrcMeaning) &&
                    mSrcLanguageForAPI.equals(item.mSrcLanguageForAPI) &&
                    mTrgLanguageForAPI.equals(item.mTrgLanguageForAPI)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
