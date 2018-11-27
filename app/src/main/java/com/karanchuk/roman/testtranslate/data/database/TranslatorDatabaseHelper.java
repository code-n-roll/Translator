package com.karanchuk.roman.testtranslate.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatorDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "translator.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE_HISTORY_TRANSLATED_ITEMS =
            "CREATE TABLE " + TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY + " (" +
                    TablePersistenceContract.TranslatedItemEntry._ID + INTEGER_TYPE +
                    " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_MEAN + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION + TEXT_TYPE +
            " );";
    private static final String SQL_CREATE_TABLE_FAVORITES_TRANSLATED_ITEMS =
            "CREATE TABLE " + TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES + " (" +
                    TablePersistenceContract.TranslatedItemEntry._ID + INTEGER_TYPE +
                    " PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_MEAN + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE + TEXT_TYPE + COMMA_SEP +
                    TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION + TEXT_TYPE +
                    " );";

    public TranslatorDatabaseHelper(final Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_HISTORY_TRANSLATED_ITEMS);
        db.execSQL(SQL_CREATE_TABLE_FAVORITES_TRANSLATED_ITEMS);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db,
                          final int oldVersion,
                          final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);
        db.execSQL(SQL_CREATE_TABLE_HISTORY_TRANSLATED_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES);
        db.execSQL(SQL_CREATE_TABLE_FAVORITES_TRANSLATED_ITEMS);
    }
}
