package com.karanchuk.roman.testtranslate.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

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
    public boolean saveTranslatedItem(@NonNull TranslatedItem translatedItem) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (isEntryExist(db, TranslatedItemEntry.TABLE_NAME, TranslatedItemEntry.COLUMN_NAME_ENTRY_ID,
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

        db.insert(TranslatedItemEntry.TABLE_NAME,null, values);
        db.close();

        return true;
    }


    @Override
    public void deleteTranslatedItem(@NonNull TranslatedItem translatedItem) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TranslatedItemEntry.COLUMN_NAME_SRC_MEAN + " LIKE ? AND " +
                            TranslatedItemEntry.COLUMN_NAME_SRC_LANG + " LIKE ? AND " +
                            TranslatedItemEntry.COLUMN_NAME_TRG_LANG + " LIKE ?";
        String[] selectionArgs = {translatedItem.getSrcMeaning(),
                translatedItem.getSrcLanguage(),
                translatedItem.getTrgLanguage()};
        db.delete(TranslatedItemEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    @Override
    public void deleteTranslatedItems(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String clearTable = "DELETE FROM " + TranslatedItemEntry.TABLE_NAME;
        db.execSQL(clearTable);
        db.close();
    }

    @NonNull
    @Override
    public List<TranslatedItem> getTranslatedItems() {
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

        Cursor c = db.query(TranslatedItemEntry.TABLE_NAME, projection, null, null, null, null, null);
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

        return items;
    }


}
