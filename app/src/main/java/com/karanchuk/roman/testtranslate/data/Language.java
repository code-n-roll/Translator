package com.karanchuk.roman.testtranslate.data;

import android.support.annotation.NonNull;

/**
 * Created by roman on 18.4.17.
 */

public class Language implements Comparable<Language>{
    private String mName;
    private String mAbbr;
    private boolean mSelected;

    public Language(String name, String abbr, boolean selected) {
        mName = name;
        mAbbr = abbr;
        mSelected = selected;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    @Override
    public int compareTo(@NonNull Language o) {
        return mName.compareTo(o.mName);
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getAbbr() {
        return mAbbr;
    }

    public void setAbbr(String abbr) {
        mAbbr = abbr;
    }
}
