package com.karanchuk.roman.testtranslate.common.view;


import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by roman on 9.4.17.
 */

public class CustomEditText extends AppCompatEditText {
    public CustomEditText(Context context){
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            this.setCursorVisible(false);
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
