package com.karanchuk.roman.testtranslate.ui.fullscreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.karanchuk.roman.testtranslate.R;

/**
 * Created by roman on 13.4.17.
 */

public class FullscreenActivity extends AppCompatActivity{
    private ImageButton mCloseFullscreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mCloseFullscreen = (ImageButton) findViewById(R.id.button_close_fullscreen);
        mCloseFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
