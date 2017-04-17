package com.karanchuk.roman.testtranslate.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.karanchuk.roman.testtranslate.data.source.local.TablesPersistenceContract.TranslatedItemEntry;
/**
 * Created by roman on 9.4.17.
 */

public class TablesDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "translator.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_HISTORY_TRANSLATED_ITEMS =
            "CREATE TABLE " + TranslatedItemEntry.TABLE_NAME_HISTORY + " (" +
                    TranslatedItemEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_SRC_LANG + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_TRG_LANG + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_TRG_MEAN + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION + TEXT_TYPE +
            " );";
    private static final String SQL_CREATE_TABLE_FAVORITES_TRANSLATED_ITEMS =
            "CREATE TABLE " + TranslatedItemEntry.TABLE_NAME_FAVORITES + " (" +
                    TranslatedItemEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_SRC_LANG + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_TRG_LANG + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_TRG_MEAN + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE + TEXT_TYPE + COMMA_SEP +
                    TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION + TEXT_TYPE +
                    " );";

    TablesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_HISTORY_TRANSLATED_ITEMS);
        db.execSQL(SQL_CREATE_TABLE_FAVORITES_TRANSLATED_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TranslatedItemEntry.TABLE_NAME_HISTORY);
        db.execSQL(SQL_CREATE_TABLE_HISTORY_TRANSLATED_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TranslatedItemEntry.TABLE_NAME_FAVORITES);
        db.execSQL(SQL_CREATE_TABLE_FAVORITES_TRANSLATED_ITEMS);
    }
}
