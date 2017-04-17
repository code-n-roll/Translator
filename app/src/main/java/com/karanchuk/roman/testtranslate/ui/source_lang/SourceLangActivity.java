package com.karanchuk.roman.testtranslate.ui.source_lang;

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
import com.google.gson.JsonParser;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by roman on 10.4.17.
 */

public class SourceLangActivity extends AppCompatActivity {
    private RecyclerView mSrcLangRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.ItemDecoration mDividerItemDecoration;
    private ArrayList<Language> mItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_lang);

        initToolbar();
        mLayoutManager = new LinearLayoutManager(this);
        mSrcLangRecycler = (RecyclerView) findViewById(R.id.recyclerview_src_lang);
        mSrcLangRecycler.setLayoutManager(mLayoutManager);

        mDividerItemDecoration = new DividerItemDecoration(this, RecyclerView.VERTICAL);
        mSrcLangRecycler.addItemDecoration(mDividerItemDecoration);


        mItems = new ArrayList<>();
        getLangsFromJson();
        Collections.sort(mItems);

        SourceLangRecyclerAdapter.OnItemClickListener itemClickListener = new
                SourceLangRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Language item) {
                        item.setSelected(true);
                        mItems.set(mItems.indexOf(item),item);
                        mSrcLangRecycler.getAdapter().notifyDataSetChanged();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result",item.getName());
                        setResult(AppCompatActivity.RESULT_OK,returnIntent);
                        Toast.makeText(getApplicationContext(),"selected "+item,Toast.LENGTH_SHORT).show();
                        finish();
                    }
                };

        mSrcLangRecycler.setAdapter(new SourceLangRecyclerAdapter(mItems, itemClickListener));

    }

    public void getLangsFromJson() {
        try {
            InputStream is = getAssets().open("langs.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String s = new String(buffer);
            JsonObject jo = (JsonObject)new JsonParser().parse(s);
            for (Map.Entry<String,JsonElement> o : jo.entrySet()){
                String lang = o.getKey();
                mItems.add(new Language(lang.substring(0,1).toUpperCase().concat(lang.substring(1)),false));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initToolbar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Source Language");
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


