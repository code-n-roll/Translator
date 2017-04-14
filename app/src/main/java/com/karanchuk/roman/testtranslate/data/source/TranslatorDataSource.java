package com.karanchuk.roman.testtranslate.data.source;

import android.support.annotation.NonNull;

import com.karanchuk.roman.testtranslate.data.TranslatedItem;

import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public interface TranslatorDataSource {
    boolean saveTranslatedItem(@NonNull TranslatedItem translatedItem);

    void deleteTranslatedItem(@NonNull TranslatedItem translatedItem);

    @NonNull
    List<TranslatedItem> getTranslatedItems();

    void deleteTranslatedItems();
}
