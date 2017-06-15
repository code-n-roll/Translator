package com.karanchuk.roman.testtranslate.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatorLocalDataSource implements TranslatorDataSource{
    private static TranslatorLocalDataSource INSTANCE;
    private static final String LOG_TAG = "MY_DB_LOG";

    private final TablesDbHelper mDbHelper;

    private TranslatorLocalDataSource(@NonNull final Context context){
        mDbHelper = new TablesDbHelper(context);
    }

    public static TranslatorLocalDataSource getInstance(@NonNull final Context context){
        if (INSTANCE == null){
            INSTANCE = new TranslatorLocalDataSource(context);
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
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (isEntryExist(db, tableName, TranslatedItemEntry.COLUMN_NAME_ENTRY_ID,
                translatedItem.getId())){
            return false;
        }

        final ContentValues values = new ContentValues();
        values.put(TranslatedItemEntry.COLUMN_NAME_ENTRY_ID, translatedItem.getId());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API, translatedItem.getSrcLanguageForAPI());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API, translatedItem.getTrgLanguageForAPI());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER, translatedItem.getSrcLanguageForUser());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER, translatedItem.getTrgLanguageForUser());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_MEAN, translatedItem.getSrcMeaning());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_MEAN, translatedItem.getTrgMeaning());
        values.put(TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE, translatedItem.isFavorite());
        values.put(TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION, translatedItem.getDictDefinition());

        db.insert(tableName,null, values);
        db.close();

        Log.d(LOG_TAG, "save item DB "+tableName);
        printAllTranslatedItems(tableName);

        return true;
    }


    public void deleteTranslatedItem(@NonNull final String tableName,
                                     @NonNull final TranslatedItem translatedItem) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final String selection = TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + " LIKE ? AND " +
                            TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API + " LIKE ? AND " +
                            TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API + " LIKE ?";
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
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

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
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final ContentValues values = new ContentValues();
        values.put(TranslatedItemEntry.COLUMN_NAME_ENTRY_ID, item.getId());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API, item.getSrcLanguageForAPI());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API, item.getTrgLanguageForAPI());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER, item.getSrcLanguageForUser());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER, item.getTrgLanguageForUser());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_MEAN, item.getSrcMeaning());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_MEAN, item.getTrgMeaning());
        values.put(TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE, item.isFavorite());
        values.put(TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION, item.getDictDefinition());

        final String whereClause = TranslatedItemEntry.COLUMN_NAME_ENTRY_ID + " = ? ";
        final String[] whereArgs = {item.getId()};
        db.update(tableName, values, whereClause, whereArgs);
        db.close();

        Log.d(LOG_TAG, "update item DB " + tableName);
        printAllTranslatedItems(tableName);
    }

    @NonNull
    @Override
    public List<TranslatedItem> getTranslatedItems(@NonNull final String tableName) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final List<TranslatedItem> items = new ArrayList<>();
        final String[] projection = {
                TranslatedItemEntry.COLUMN_NAME_ENTRY_ID,
                TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API,
                TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API,
                TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER,
                TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER,
                TranslatedItemEntry.COLUMN_NAME_SRC_MEAN,
                TranslatedItemEntry.COLUMN_NAME_TRG_MEAN,
                TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE,
                TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION
        };

        final Cursor c = db.query(tableName, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0){
            while(c.moveToNext()){
                final String itemId =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_ENTRY_ID));
                final String itemSrcLangAPI =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_SRC_LANG_API));
                final String itemTrgLangAPI =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_TRG_LANG_API));
                final String itemSrcLangUser =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_SRC_LANG_USER));
                final String itemTrgLangUser =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_TRG_LANG_USER));
                final String itemSrcMean =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_SRC_MEAN));
                final String itemTrgMean =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_TRG_MEAN));
                final String itemIsFavorite =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE));
                final String itemDictDef =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION));

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
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        final Cursor c = db.query(tableName, null, null, null, null, null, null);
        logCursor(c);

        if (c!= null){
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
