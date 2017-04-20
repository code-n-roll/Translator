package com.karanchuk.roman.testtranslate.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.local.TablesPersistenceContract.TranslatedItemEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class TranslatorLocalDataSource implements TranslatorDataSource{
    private static TranslatorLocalDataSource INSTANCE;
    private static String LOG_TAG = "MY_DB_LOG";

    private TablesDbHelper mDbHelper;

    private TranslatorLocalDataSource(@NonNull Context context){
        mDbHelper = new TablesDbHelper(context);
    }

    public static TranslatorLocalDataSource getInstance(@NonNull Context context){
        if (INSTANCE == null){
            INSTANCE = new TranslatorLocalDataSource(context);
        }
        return INSTANCE;
    }

    private boolean isEntryExist(SQLiteDatabase db, String tableName, String fieldName, String entryId){
        Cursor c = null;
        try{
            String query = "SELECT COUNT(*) FROM " + tableName + " WHERE " + fieldName + " = ?";
            c = db.rawQuery(query, new String[] {entryId});
            return c.moveToFirst() && c.getInt(0) != 0;
        } finally {
            if (c != null){
                c.close();
            }
        }
    }

    @Override
    public boolean saveTranslatedItem(@NonNull String tableName, @NonNull TranslatedItem translatedItem) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (isEntryExist(db, tableName, TranslatedItemEntry.COLUMN_NAME_ENTRY_ID,
                translatedItem.getId())){
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(TranslatedItemEntry.COLUMN_NAME_ENTRY_ID, translatedItem.getId());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_LANG, translatedItem.getSrcLanguage());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_LANG, translatedItem.getTrgLanguage());
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


    public void deleteTranslatedItem(@NonNull String tableName, @NonNull TranslatedItem translatedItem) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + " LIKE ? AND " +
                            TranslatedItemEntry.COLUMN_NAME_SRC_LANG + " LIKE ? AND " +
                            TranslatedItemEntry.COLUMN_NAME_TRG_LANG + " LIKE ?";
        String[] selectionArgs = {
                translatedItem.getSrcMeaning(),
                translatedItem.getSrcLanguage(),
                translatedItem.getTrgLanguage()};
        db.delete(tableName, selection, selectionArgs);
        db.close();

        Log.d(LOG_TAG, "delete item DB "+tableName);
        printAllTranslatedItems(tableName);
    }

    public void deleteTranslatedItems(@NonNull String tableName){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String clearTable = "DELETE FROM " + tableName;
        db.execSQL(clearTable);

        Log.d(LOG_TAG, "delete items DB " + tableName);
        printAllTranslatedItems(tableName);

        db.close();
    }

    public void updateIsFavoriteTranslatedItems(@NonNull String tableName, @NonNull boolean isFavorite){
        List<TranslatedItem> list = getTranslatedItems(tableName);
        for (TranslatedItem item : list){
            item.isFavoriteUp(isFavorite);
            updateTranslatedItem(tableName, item);
        }
    }

    public void updateTranslatedItem(@NonNull String tableName, @NonNull TranslatedItem item) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TranslatedItemEntry.COLUMN_NAME_ENTRY_ID, item.getId());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_LANG, item.getSrcLanguage());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_LANG, item.getTrgLanguage());
        values.put(TranslatedItemEntry.COLUMN_NAME_SRC_MEAN, item.getSrcMeaning());
        values.put(TranslatedItemEntry.COLUMN_NAME_TRG_MEAN, item.getTrgMeaning());
        values.put(TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE, item.isFavorite());
        values.put(TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION, item.getDictDefinition());

        String whereClause = TranslatedItemEntry.COLUMN_NAME_ENTRY_ID + " = ? ";
        String[] whereArgs = {item.getId()};
        db.update(tableName, values, whereClause, whereArgs);
        db.close();

        Log.d(LOG_TAG, "update item DB " + tableName);
        printAllTranslatedItems(tableName);
    }

    @NonNull
    @Override
    public List<TranslatedItem> getTranslatedItems(@NonNull String tableName) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        List<TranslatedItem> items = new ArrayList<>();
        String[] projection = {
                TranslatedItemEntry.COLUMN_NAME_ENTRY_ID,
                TranslatedItemEntry.COLUMN_NAME_SRC_LANG,
                TranslatedItemEntry.COLUMN_NAME_TRG_LANG,
                TranslatedItemEntry.COLUMN_NAME_SRC_MEAN,
                TranslatedItemEntry.COLUMN_NAME_TRG_MEAN,
                TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE,
                TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION
        };

        Cursor c = db.query(tableName, projection, null, null, null, null, null);
        if (c != null && c.getCount() > 0){
            while(c.moveToNext()){
                String itemId =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_ENTRY_ID));
                String itemSrcLang =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_SRC_LANG));
                String itemTrgLang =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_TRG_LANG));
                String itemSrcMean =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_SRC_MEAN));
                String itemTrgMean =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_TRG_MEAN));
                String itemIsFavorite =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_IS_FAVORITE));
                String itemDictDef =
                        c.getString(c.getColumnIndexOrThrow(TranslatedItemEntry.COLUMN_NAME_DICT_DEFINITION));

                TranslatedItem translatedItem = new TranslatedItem(itemId, itemSrcLang, itemTrgLang,
                        itemSrcMean, itemTrgMean, itemIsFavorite, itemDictDef);
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
    public void printAllTranslatedItems(@NonNull String tableName) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor c = db.query(tableName, null, null, null, null, null, null);
        logCursor(c);

        if (c!= null){
            c.close();
        }
        db.close();

    }


    private void logCursor(Cursor c){
        if (c != null){
            if (c.moveToFirst()){
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()){
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
