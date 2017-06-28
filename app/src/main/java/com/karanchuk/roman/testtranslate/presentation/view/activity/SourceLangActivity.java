package com.karanchuk.roman.testtranslate.presentation.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.presentation.model.Language;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.SourceLangRecyclerAdapter;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_SELECTED_ITEM_SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.LANGS_FILE_NAME;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PREFS_NAME;

/**
 * Created by roman on 10.4.17.
 */

public class SourceLangActivity extends AppCompatActivity {
    private RecyclerView mSrcLangRecycler;

    private List<Language> mItems;
    private JsonObject mLangsJson;
    private Language mCurSelectedItem;
    private SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_lang);
        initToolbar();

        mSrcLangRecycler = (RecyclerView) findViewById(R.id.recyclerview_src_lang);
        mSrcLangRecycler.setLayoutManager(new LinearLayoutManager(this));
        mSrcLangRecycler.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));

        mSettings = getSharedPreferences(PREFS_NAME, 0);
        mLangsJson = JsonUtils.getJsonObjectFromAssetsFile(this, LANGS_FILE_NAME);

        mItems = JsonUtils.getLangsFromJson(mLangsJson);
        Collections.sort(mItems);

        mSrcLangRecycler.setAdapter(new SourceLangRecyclerAdapter(mItems,
                this::clickOnSourceLangItemRecycler,
                getApplicationContext()));

    }

    private void clickOnSourceLangItemRecycler(final Language language){
        if (mCurSelectedItem == null){
            mCurSelectedItem = language;
        }
        if (!mCurSelectedItem.equals(language)){
            mCurSelectedItem.setSelected(false);
            mSrcLangRecycler.getAdapter().notifyItemChanged(mItems.indexOf(mCurSelectedItem));
            mCurSelectedItem = language;
        }
        if (!language.isSelected()) {
            language.setSelected(true);
            mSrcLangRecycler.getAdapter().notifyItemChanged(mItems.indexOf(language));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",language.getName());
            setResult(AppCompatActivity.RESULT_OK,returnIntent);
            UIUtils.showToast(getApplicationContext(),"selected "+language);
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        restoreFromSharedPreferences();
    }

    private void restoreCurSelectedItem(){
        String abbr = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG, "");
        String langName = null;
        for (Map.Entry<String, JsonElement> pair : mLangsJson.entrySet()) {
            if (abbr.equals(pair.getValue().getAsString())) {
                langName = pair.getKey();
                break;
            }
        }
        mCurSelectedItem = new Language(langName, abbr, true);
    }

    private void restoreFromSharedPreferences() {
        restoreCurSelectedItem();
        int id = mItems.indexOf(mCurSelectedItem);
        if (id != -1) {
            mCurSelectedItem = mItems.get(id);
            mCurSelectedItem.setSelected(true);
            mSrcLangRecycler.getAdapter().notifyItemChanged(id);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        saveToSharedPreferences();
    }

    private void saveToSharedPreferences(){
        SharedPreferences.Editor editor = mSettings.edit();
        if (mCurSelectedItem != null)
            editor.putString(CUR_SELECTED_ITEM_SRC_LANG, mCurSelectedItem.getAbbr());
        editor.apply();
    }



    private void initToolbar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.title_source_lang));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}


