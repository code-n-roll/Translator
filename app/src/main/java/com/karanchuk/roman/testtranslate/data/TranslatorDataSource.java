package com.karanchuk.roman.testtranslate.data;

import android.support.annotation.NonNull;

import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;

import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public interface TranslatorDataSource {
    boolean saveTranslatedItem(@NonNull final String tableName,
                               @NonNull final TranslatedItem translatedItem);

    void deleteTranslatedItem(@NonNull final String tableName,
                              @NonNull final TranslatedItem translatedItem);

    @NonNull
    List<TranslatedItem> getTranslatedItems(@NonNull final String tableName);

    void deleteTranslatedItems(@NonNull final String tableName);

    void updateTranslatedItem(@NonNull final String tableName,
                              @NonNull final TranslatedItem translatedItem);

    void updateIsFavoriteTranslatedItems(@NonNull final String tableName,
                                         @NonNull final boolean isFavorite);

    void printAllTranslatedItems(@NonNull final String tableName);
}
