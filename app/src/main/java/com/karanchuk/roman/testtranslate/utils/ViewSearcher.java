package com.karanchuk.roman.testtranslate.utils;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by roman on 24.4.17.
 */

public final class ViewSearcher {
    private final View mView;

    public ViewSearcher(@NonNull final View view){
        Assertion.nonNull(view);
        mView = view;
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(@IntRange(from=1)final int id) {
        return (T) mView.findViewById(id);
    }
}
