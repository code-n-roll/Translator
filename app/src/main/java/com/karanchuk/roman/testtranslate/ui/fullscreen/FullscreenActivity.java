package com.karanchuk.roman.testtranslate.ui.fullscreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;

import org.w3c.dom.Text;

/**
 * Created by roman on 13.4.17.
 */

public class FullscreenActivity extends AppCompatActivity{
    private ImageButton mCloseFullscreen;
    private TextView mFullscreenText;
    public static String TRANSLATED_RESULT = "translated_result";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mFullscreenText = (TextView) findViewById(R.id.textview_fullscreen_text);

        if (getIntent() != null)
            mFullscreenText.setText(getIntent().getStringExtra(TRANSLATED_RESULT));

        mCloseFullscreen = (ImageButton) findViewById(R.id.button_close_fullscreen);
        mCloseFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
