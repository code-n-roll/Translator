package com.karanchuk.roman.testtranslate.data;

import java.util.UUID;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatedItem {
    private String mId;
    private String mSrcLanguage;
    private String mTrgLanguage;
    private String mSrcMeaning;
    private String mTrgMeaning;
    private String mIsFavorite;
    private String mDictDefinition;

    public TranslatedItem(TranslatedItem translatedItem){
        mId = translatedItem.getId();
        mSrcLanguage = translatedItem.getSrcLanguage();
        mTrgLanguage = translatedItem.getTrgLanguage();
        mSrcMeaning = translatedItem.getSrcMeaning();
        mTrgMeaning = translatedItem.getTrgMeaning();
        mIsFavorite = translatedItem.getIsFavorite();
        mDictDefinition = translatedItem.getDictDefinition();
    }

    public TranslatedItem(String id, String srcLanguage,
                          String trgLanguage,
                          String srcMeaning,
                          String trgMeaning,
                          String isFavorite,
                          String dictDefinition) {
        mId = id;
        mSrcLanguage = srcLanguage;
        mTrgLanguage = trgLanguage;
        mSrcMeaning = srcMeaning;
        mTrgMeaning = trgMeaning;
        mIsFavorite = isFavorite;
        mDictDefinition = dictDefinition;
    }



    public TranslatedItem(String srcLanguage,
                          String trgLanguage,
                          String srcMeaning,
                          String trgMeaning,
                          String isFavorite,
                          String dictDefinition){
        this(UUID.randomUUID().toString(), srcLanguage,
                trgLanguage, srcMeaning, trgMeaning, isFavorite, dictDefinition);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getSrcLanguage() {
        return mSrcLanguage;
    }

    public void setSrcLanguage(String srcLanguage) {
        mSrcLanguage = srcLanguage;
    }

    public String getTrgLanguage() {
        return mTrgLanguage;
    }

    public void setTrgLanguage(String trgLanguage) {
        mTrgLanguage = trgLanguage;
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
                this.mSrcLanguage.equals(item.mSrcLanguage) &&
                this.mTrgLanguage.equals(item.mTrgLanguage)){
            return true;
        }
        return false;
    }
}
