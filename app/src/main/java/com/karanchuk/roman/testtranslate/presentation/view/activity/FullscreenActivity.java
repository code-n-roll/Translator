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
    private ImageButton mCloseFullscreen;
    private TextView mFullscreenText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mFullscreenText = (TextView) findViewById(R.id.textview_fullscreen_text);
        mCloseFullscreen = (ImageButton) findViewById(R.id.button_close_fullscreen);

        if (getIntent() != null) {
            mFullscreenText.setText(getIntent().getStringExtra(TRANSLATED_RESULT));
        }

        mCloseFullscreen.setOnClickListener(v -> finish());
    }
}
