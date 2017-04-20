package com.karanchuk.roman.testtranslate.data;

import java.util.UUID;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatedItem {
    private String mId;
    private String mSrcLanguageForAPI;
    private String mTrgLanguageForAPI;
    private String mSrcLanguageForUser;
    private String mTrgLanguageForUser;
    private String mSrcMeaning;
    private String mTrgMeaning;
    private String mIsFavorite;
    private String mDictDefinition;

    public TranslatedItem(TranslatedItem translatedItem){
        mId = translatedItem.getId();
        mSrcLanguageForAPI = translatedItem.getSrcLanguageForAPI();
        mTrgLanguageForAPI = translatedItem.getTrgLanguageForAPI();
        mSrcLanguageForUser = translatedItem.getSrcLanguageForAPI();
        mTrgLanguageForUser = translatedItem.getTrgLanguageForAPI();
        mSrcMeaning = translatedItem.getSrcMeaning();
        mTrgMeaning = translatedItem.getTrgMeaning();
        mIsFavorite = translatedItem.getIsFavorite();
        mDictDefinition = translatedItem.getDictDefinition();

    }

    public TranslatedItem(String id, String srcLanguageForAPI,
                          String trgLanguageForAPI,
                          String srcLanguageForUser,
                          String trgLanguageForUser,
                          String srcMeaning,
                          String trgMeaning,
                          String isFavorite,
                          String dictDefinition) {
        mId = id;
        mSrcLanguageForAPI = srcLanguageForAPI;
        mTrgLanguageForAPI = trgLanguageForAPI;
        mSrcLanguageForUser = srcLanguageForUser;
        mTrgLanguageForUser = trgLanguageForUser;
        mSrcMeaning = srcMeaning;
        mTrgMeaning = trgMeaning;
        mIsFavorite = isFavorite;
        mDictDefinition = dictDefinition;
    }



    public TranslatedItem(String srcLanguageForAPI,
                          String trgLanguageForAPI,
                          String srcLanguageForUser,
                          String trgLanguageForUser,
                          String srcMeaning,
                          String trgMeaning,
                          String isFavorite,
                          String dictDefinition){
        this(UUID.randomUUID().toString(), srcLanguageForAPI,
                trgLanguageForAPI, srcLanguageForUser,
                trgLanguageForUser, srcMeaning,
                trgMeaning, isFavorite,
                dictDefinition);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getSrcLanguageForAPI() {
        return mSrcLanguageForAPI;
    }

    public void setSrcLanguageForAPI(String srcLanguageForAPI) {
        mSrcLanguageForAPI = srcLanguageForAPI;
    }

    public String getTrgLanguageForAPI() {
        return mTrgLanguageForAPI;
    }

    public void setTrgLanguageForAPI(String trgLanguageForAPI) {
        mTrgLanguageForAPI = trgLanguageForAPI;
    }

    public String getSrcLanguageForUser() {
        return mSrcLanguageForUser;
    }

    public void setSrcLanguageForUser(String srcLanguageForUser) {
        mSrcLanguageForUser = srcLanguageForUser;
    }

    public String getTrgLanguageForUser() {
        return mTrgLanguageForUser;
    }

    public void setTrgLanguageForUser(String trgLanguageForUser) {
        mTrgLanguageForUser = trgLanguageForUser;
    }

    public String getSrcMeaning() {
        return mSrcMeaning;
    }

    public void setSrcMeaning(String srcMeaning) {
        mSrcMeaning = srcMeaning;
    }

    public String getTrgMeaning() {
        return mTrgMeaning;
    }

    public void setTrgMeaning(String trgMeaning) {
        mTrgMeaning = trgMeaning;
    }

    public String getIsFavorite() {
        return mIsFavorite;
    }

    public boolean isFavorite() {
        if (mIsFavorite.equals("1")){
            return true;
        } else {
            return false;
        }
    }

    public void isFavoriteUp(boolean isFavoriteUp){
        if (isFavoriteUp){
            mIsFavorite = "1";
        } else {
            mIsFavorite = "0";
        }
    }

    public void setIsFavorite(String isFavorite) {
        mIsFavorite = isFavorite;
    }


    public String getDictDefinition() {
        return mDictDefinition;
    }

    public void setDictDefinition(String dictDefinition) {
        mDictDefinition = dictDefinition;
    }

    @Override
    public boolean equals(Object obj) {
        TranslatedItem item = (TranslatedItem) obj;
        if (this.mSrcMeaning.equals(item.mSrcMeaning) &&
                this.mTrgMeaning.equals(item.mTrgMeaning) &&
                this.mSrcLanguageForAPI.equals(item.mSrcLanguageForAPI) &&
                this.mTrgLanguageForAPI.equals(item.mTrgLanguageForAPI)){
            return true;
        }
        return false;
    }
}
