package com.romankaranchuk.translator.data.database.repository;

import androidx.annotation.NonNull;

import com.romankaranchuk.translator.data.database.model.TranslatedItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public interface TranslatorRepository {
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

    void addHistoryContentObserver(@NotNull TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver observer);
    void removeHistoryContentObserver(@NotNull TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver observer);
}
