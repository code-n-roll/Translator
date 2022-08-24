package com.romankaranchuk.translator.data.database;

import android.provider.BaseColumns;

/**
 * Created by roman on 9.4.17.
 */

public final class TablePersistenceContract {

    private TablePersistenceContract(){
        // do not create instances
    }

    public static abstract class TranslatedItemEntry implements BaseColumns {

        public static final String TABLE_NAME_HISTORY = "history_translated_items";
        public static final String TABLE_NAME_FAVORITES = "favorites_translated_items";

        public static final String COLUMN_NAME_ENTRY_ID = "translated_item_id";
        public static final String COLUMN_NAME_SRC_LANG_API = "source_language_api";
        public static final String COLUMN_NAME_TRG_LANG_API = "target_language_api";
        public static final String COLUMN_NAME_SRC_LANG_USER = "source_language_user";
        public static final String COLUMN_NAME_TRG_LANG_USER = "target_language_user";
        public static final String COLUMN_NAME_SRC_MEAN = "source_meaning";
        public static final String COLUMN_NAME_TRG_MEAN = "target_meaning";
        public static final String COLUMN_NAME_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_NAME_DICT_DEFINITION = "dict_definition";
    }
}
