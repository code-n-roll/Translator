package com.karanchuk.roman.testtranslate.data.source;

import android.support.annotation.NonNull;

import com.karanchuk.roman.testtranslate.data.TranslatedItem;

import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public interface TranslatorDataSource {
    boolean saveTranslatedItem(@NonNull String tableName, @NonNull TranslatedItem translatedItem);

    void deleteTranslatedItem(@NonNull String tableName, @NonNull TranslatedItem translatedItem);

    @NonNull
    List<TranslatedItem> getTranslatedItems(@NonNull String tableName);

    void deleteTranslatedItems(@NonNull String tableName);

    void updateTranslatedItem(@NonNull String tableName, @NonNull TranslatedItem translatedItem);

    void updateIsFavoriteTranslatedItems(@NonNull String tableName, @NonNull boolean isFavorite);

    void printAllTranslatedItems(@NonNull String tableName);
}
