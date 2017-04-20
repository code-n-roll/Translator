package com.karanchuk.roman.testtranslate.ui.target_lang;

import android.content.Intent;
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
import java.util.Map;

/**
 * Created by roman on 10.4.17.
 */

public class TargetLangActivity extends AppCompatActivity {
    private RecyclerView mTrgLangRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private ArrayList<Language> mItems;
    private JsonObject mLangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_lang);

        initToolbar();
        mLangs = JsonUtils.getJsonObjectFromFile(getAssets(),"langs.json");

        mLayoutManager = new LinearLayoutManager(this);
        mTrgLangRecycler = (RecyclerView) findViewById(R.id.recyclerview_trg_lang);
        mTrgLangRecycler.setLayoutManager(mLayoutManager);

        mDividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        mTrgLangRecycler.addItemDecoration(mDividerItemDecoration);


        mItems = new ArrayList<>();
        getLangsFromJson();
        Collections.sort(mItems);

        TargetLangRecyclerAdapter.OnItemClickListener itemClickListener = new
                TargetLangRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Language item) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result",item.getName());
                        setResult(AppCompatActivity.RESULT_OK,returnIntent);
                        Toast.makeText(getApplicationContext(),"selected "+item,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                };

        mTrgLangRecycler.setAdapter(new TargetLangRecyclerAdapter(mItems, itemClickListener));

    }

    public void getLangsFromJson() {
        for (Map.Entry<String,JsonElement> o : mLangs.entrySet()){
            String lang = o.getKey();
            String abbr = o.getValue().getAsString();
            String firstCapitalize = lang.substring(0,1).toUpperCase().concat(lang.substring(1));
            mItems.add(new Language(firstCapitalize,abbr,false));
        }
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
