package com.karanchuk.roman.testtranslate.data.source.local;

import android.provider.BaseColumns;

/**
 * Created by roman on 9.4.17.
 */

public final class TablesPersistenceContract {
    private TablesPersistenceContract(){}

    public static abstract class TranslatedItemEntry implements BaseColumns{
        public static final String TABLE_NAME_HISTORY = "history_translated_items";
        public static final String TABLE_NAME_FAVORITES = "favorites_translated_items";
        public static final String COLUMN_NAME_ENTRY_ID = "translated_item_id";
        public static final String COLUMN_NAME_SRC_LANG = "source_language";
        public static final String COLUMN_NAME_TRG_LANG = "target_language";
        public static final String COLUMN_NAME_SRC_MEAN = "source_meaning";
        public static final String COLUMN_NAME_TRG_MEAN = "target_meaning";
        public static final String COLUMN_NAME_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_NAME_DICT_DEFINITION = "dict_definition";
    }
}
