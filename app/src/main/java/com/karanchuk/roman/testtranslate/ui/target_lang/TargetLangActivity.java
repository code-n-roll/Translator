package com.karanchuk.roman.testtranslate.ui.target_lang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.Language;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment.PREFS_NAME;

/**
 * Created by roman on 10.4.17.
 */

public class TargetLangActivity extends AppCompatActivity {
    private RecyclerView mTrgLangRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private List<Language> mItems;
    private JsonObject mLangs;
    private Language mCurSelectedItem;
    private SharedPreferences mSettings;
    public static String CUR_SELECTED_ITEM_TRG_LANG = "CUR_SELECTED_ITEM_TRG_LANG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_lang);

        mSettings = getSharedPreferences(PREFS_NAME, 0);


        initToolbar();
        mLangs = JsonUtils.getJsonObjectFromFile(getAssets(),"langs.json");

        mLayoutManager = new LinearLayoutManager(this);
        mTrgLangRecycler = (RecyclerView) findViewById(R.id.recyclerview_trg_lang);
        mTrgLangRecycler.setLayoutManager(mLayoutManager);

        mDividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        mTrgLangRecycler.addItemDecoration(mDividerItemDecoration);


        mItems = getLangsFromJson();
        Collections.sort(mItems);


        mTrgLangRecycler.setAdapter(new TargetLangRecyclerAdapter(
                mItems,
                (language)->clickOnTargetLangRecyclerItem(language)
        ));
    }

    private void clickOnTargetLangRecyclerItem(Language language){
        if (mCurSelectedItem == null){
            mCurSelectedItem = language;
        }
        if (!mCurSelectedItem.equals(language)){
            mCurSelectedItem.setSelected(false);
            mTrgLangRecycler.getAdapter().notifyItemChanged(mItems.indexOf(mCurSelectedItem));
            mCurSelectedItem = language;
        }
        if (!language.isSelected()) {
            language.setSelected(true);
            mTrgLangRecycler.getAdapter().notifyItemChanged(mItems.indexOf(language));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result",language.getName());
            setResult(AppCompatActivity.RESULT_OK,returnIntent);
            Toast.makeText(getApplicationContext(),"selected "+language,Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        restoreFromSharedPreferences();
    }

    public void restoreFromSharedPreferences(){
        String abbr = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");
        String langName = null;
        for (Map.Entry<String,JsonElement> pair : mLangs.entrySet()){
            if (abbr.equals(pair.getValue().getAsString())){
                langName = pair.getKey();
                break;
            }
        }
        mCurSelectedItem = new Language(langName, abbr, true);

        int id = mItems.indexOf(mCurSelectedItem);
        if (id != -1) {
            mCurSelectedItem = mItems.get(id);
            mCurSelectedItem.setSelected(true);
            mTrgLangRecycler.getAdapter().notifyItemChanged(id);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveToSharedPreferences();
    }

    public void saveToSharedPreferences(){
        SharedPreferences.Editor editor = mSettings.edit();
        if (mCurSelectedItem != null)
            editor.putString(CUR_SELECTED_ITEM_TRG_LANG, mCurSelectedItem.getAbbr());
        editor.apply();
    }

    public List<Language> getLangsFromJson() {
        List<Language> items = new ArrayList<>();
        for (Map.Entry<String,JsonElement> o : mLangs.entrySet()){
            String lang = o.getKey();
            String abbr = o.getValue().getAsString();
            String firstCapitalize = lang.substring(0,1).toUpperCase().concat(lang.substring(1));
            items.add(new Language(firstCapitalize,abbr,false));
        }
        return items;
    }

    public void initToolbar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Target Language");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
