package com.karanchuk.roman.testtranslate.ui.fullscreen;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;

import static com.karanchuk.roman.testtranslate.common.Constants.TRANSLATED_RESULT;

/**
 * Created by roman on 13.4.17.
 */

public class FullscreenActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        TextView fullscreenText = findViewById(R.id.textview_fullscreen_text);
        ImageButton closeFullscreen = findViewById(R.id.button_close_fullscreen);

        if (getIntent() != null) {
            fullscreenText.setText(getIntent().getStringExtra(TRANSLATED_RESULT));
        }

        closeFullscreen.setOnClickListener(v -> finish());
    }
}
