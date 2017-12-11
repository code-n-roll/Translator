package com.karanchuk.roman.testtranslate.common.database.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.karanchuk.roman.testtranslate.common.database.TablePersistenceContract;
import com.karanchuk.roman.testtranslate.common.database.TranslatorDatabaseHelper;
import com.karanchuk.roman.testtranslate.common.model.TranslatedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatorLocalRepository implements TranslatorRepository {

    private static TranslatorLocalRepository INSTANCE;

    private static final String LOG_TAG = "MY_DB_LOG";

    private final TranslatorDatabaseHelper mDatabaseHelper;

    private TranslatorLocalRepository(@NonNull final Context context){
        mDatabaseHelper = new TranslatorDatabaseHelper(context);
    }

    public static TranslatorLocalRepository getInstance(@NonNull final Context context){
        if (INSTANCE == null){
            INSTANCE = new TranslatorLocalRepository(context);
        }
        return INSTANCE;
    }

    private boolean isEntryExist(final SQLiteDatabase db,
                                 final String tableName,
                                 final String fieldName,
                                 final String entryId){
        Cursor c = null;
        try{
            final String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + fieldName + " = ?";
            c = db.rawQuery(query, new String[] {entryId});
            return c.moveToFirst() && c.getInt(0) != 0;
        } finally {
            if (c != null){
                c.close();
            }
        }
    }

    @Override
    public boolean saveTranslatedItem(@NonNull final String tableName,
                                      @NonNull final TranslatedItem translatedItem) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        if (isEntryExist(db, tableName, TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID,
                translatedItem.getId())){
            return false;
        }

        final ContentValues values = new ContentValues();
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID, translatedItem.getId());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API, translatedItem.getSrcLanguageForAPI());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API, translatedItem.getTrgLanguageForAPI());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER, translatedItem.getSrcLanguageForUser());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER, translatedItem.getTrgLanguageForUser());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_MEAN, translatedItem.getSrcMeaning());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_MEAN, translatedItem.getTrgMeaning());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE, translatedItem.isFavorite());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION, translatedItem.getDictDefinitionJSON());

        db.insert(tableName,null, values);
        db.close();

        Log.d(LOG_TAG, "save item DB "+tableName);
        printAllTranslatedItems(tableName);

        return true;
    }


    public void deleteTranslatedItem(@NonNull final String tableName,
                                     @NonNull final TranslatedItem translatedItem) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        final String selection = TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + " LIKE ? AND " +
                            TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API + " LIKE ? AND " +
                            TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API + " LIKE ?";
        final String[] selectionArgs = {
                translatedItem.getSrcMeaning(),
                translatedItem.getSrcLanguageForAPI(),
                translatedItem.getTrgLanguageForAPI()};
        db.delete(tableName, selection, selectionArgs);
        db.close();

        Log.d(LOG_TAG, "delete item DB "+tableName);
        printAllTranslatedItems(tableName);
    }

    public void deleteTranslatedItems(@NonNull final String tableName){
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        final String clearTable = "DELETE FROM " + tableName;
        db.execSQL(clearTable);

        Log.d(LOG_TAG, "delete items DB " + tableName);
        printAllTranslatedItems(tableName);

        db.close();
    }

    public void updateIsFavoriteTranslatedItems(@NonNull final String tableName,
                                                @NonNull final boolean isFavorite){
        final List<TranslatedItem> list = getTranslatedItems(tableName);
        for (TranslatedItem item : list){
            item.isFavoriteUp(isFavorite);
            updateTranslatedItem(tableName, item);
        }
    }

    public void updateTranslatedItem(@NonNull final String tableName,
                                     @NonNull final TranslatedItem item) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID, item.getId());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API, item.getSrcLanguageForAPI());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API, item.getTrgLanguageForAPI());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER, item.getSrcLanguageForUser());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER, item.getTrgLanguageForUser());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_MEAN, item.getSrcMeaning());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_MEAN, item.getTrgMeaning());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE, item.isFavorite());
        values.put(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION, item.getDictDefinitionJSON());

        final String whereClause = TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID + " = ? ";
        final String[] whereArgs = {item.getId()};
        db.update(tableName, values, whereClause, whereArgs);
        db.close();

        Log.d(LOG_TAG, "update item DB " + tableName);
        printAllTranslatedItems(tableName);
    }

    @NonNull
    @Override
    public List<TranslatedItem> getTranslatedItems(@NonNull final String tableName) {
        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        final List<TranslatedItem> items = new ArrayList<>();
        final String[] projection = {
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_MEAN,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_MEAN,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE,
                TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION
        };

        final Cursor c = db.query(tableName, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0){
            while(c.moveToNext()){
                final String itemId =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_ENTRY_ID));
                final String itemSrcLangAPI =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API));
                final String itemTrgLangAPI =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API));
                final String itemSrcLangUser =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER));
                final String itemTrgLangUser =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER));
                final String itemSrcMean =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_SRC_MEAN));
                final String itemTrgMean =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_TRG_MEAN));
                final String itemIsFavorite =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE));
                final String itemDictDef =
                        c.getString(c.getColumnIndexOrThrow(TablePersistenceContract.TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION));

                TranslatedItem translatedItem = new TranslatedItem(
                        itemId,
                        itemSrcLangAPI,
                        itemTrgLangAPI,
                        itemSrcLangUser,
                        itemTrgLangUser,
                        itemSrcMean,
                        itemTrgMean,
                        itemIsFavorite,
                        itemDictDef);
                items.add(translatedItem);
            }
        }
        if (c != null){
            c.close();
        }
        db.close();

        Log.d(LOG_TAG, "get items DB " + tableName);
        printAllTranslatedItems(tableName);
        return items;
    }

    @Override
    public void printAllTranslatedItems(@NonNull final String tableName) {
        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        final Cursor c = db.query(tableName, null, null, null, null, null, null);

        logCursor(c);
        if (c != null){
            c.close();
        }

        db.close();
    }


    private void logCursor(final Cursor c){
        if (c != null){
            if (c.moveToFirst()){
                String str;
                do {
                    str = "";
                    for (final String cn : c.getColumnNames()){
                        str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                } while (c.moveToNext());
            }
        } else {
            Log.d(LOG_TAG, "Cursor is null");
        }
    }
}
