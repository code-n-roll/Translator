package com.karanchuk.roman.testtranslate.data;

import java.util.UUID;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatedItem {
    private String mId;
    private String mSrcLanguage;
    private String mDstLanguage;
    private String mSrcMeaning;
    private String mDstMeaning;
    private boolean mIsFavorite;

    public TranslatedItem(String id, String srcLanguage,
                          String dstLanguage,
                          String srcMeaning,
                          String dstMeaning,
                          boolean isFavorite) {
        mId = id;
        mSrcLanguage = srcLanguage;
        mDstLanguage = dstLanguage;
        mSrcMeaning = srcMeaning;
        mDstMeaning = dstMeaning;
        mIsFavorite = isFavorite;
    }

    public TranslatedItem(String srcLanguage,
                          String dstLanguage,
                          String srcMeaning,
                          String dstMeaning,
                          boolean isFavorite){
        this(UUID.randomUUID().toString(), srcLanguage,
                dstLanguage, srcMeaning, dstMeaning, isFavorite);
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

    public String getDstLanguage() {
        return mDstLanguage;
    }

    public void setDstLanguage(String dstLanguage) {
        mDstLanguage = dstLanguage;
    }

    public String getSrcMeaning() {
        return mSrcMeaning;
    }

    public void setSrcMeaning(String srcMeaning) {
        mSrcMeaning = srcMeaning;
    }

    public String getDstMeaning() {
        return mDstMeaning;
    }

    public void setDstMeaning(String dstMeaning) {
        mDstMeaning = dstMeaning;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }


}
