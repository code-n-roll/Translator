package com.karanchuk.roman.testtranslate.presentation.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;

import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSLATED_RESULT;

/**
 * Created by roman on 13.4.17.
 */

public class FullscreenActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        TextView fullscreenText = (TextView) findViewById(R.id.textview_fullscreen_text);
        ImageButton closeFullscreen = (ImageButton) findViewById(R.id.button_close_fullscreen);

        if (getIntent() != null) {
            fullscreenText.setText(getIntent().getStringExtra(TRANSLATED_RESULT));
        }

        closeFullscreen.setOnClickListener(v -> finish());
    }
}
