package com.karanchuk.roman.testtranslate.data.source.local;

import android.provider.BaseColumns;

/**
 * Created by roman on 9.4.17.
 */

final class TablesPersistenceContract {
    private TablesPersistenceContract(){}

    static abstract class TranslatedItemEntry implements BaseColumns{
        static final String TABLE_NAME = "translated_items";
        static final String COLUMN_NAME_ENTRY_ID = "translated_item_id";
        static final String COLUMN_NAME_SRC_LANG = "source_language";
        static final String COLUMN_NAME_TRG_LANG = "target_language";
        static final String COLUMN_NAME_SRC_MEAN = "source_meaning";
        static final String COLUMN_NAME_TRG_MEAN = "target_meaning";
        static final String COLUMN_NAME_IS_FAVORITE = "is_favorite";
        static final String COLUMN_NAME_DICT_DEFINITION = "dict_definition";
    }
}
