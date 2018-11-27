package com.karanchuk.roman.testtranslate.data.database.model;

import android.support.annotation.NonNull;

/**
 * Created by roman on 18.4.17.
 */

public class Language implements Comparable<Language>{
    private String mName;
    private String mAbbr;
    private boolean mSelected;

    public Language(final String name,
                    final String abbr,
                    final boolean selected) {
        mName = name;
        mAbbr = abbr;
        mSelected = selected;
    }

    public String getName() {
        return mName;
    }

    public void setName(final String name) {
        mName = name;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(final boolean selected) {
        mSelected = selected;
    }

    @Override
    public int compareTo(@NonNull final Language o) {
        return mName.compareTo(o.mName);
    }

    @Override
    public String toString() {
        return mName;
    }

    public String getAbbr() {
        return mAbbr;
    }

    public void setAbbr(final String abbr) {
        mAbbr = abbr;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Language) {
            Language item = (Language) obj;
            if (mAbbr.equals(item.mAbbr)) {
                return true;
            }
        }
        return false;
    }
}
