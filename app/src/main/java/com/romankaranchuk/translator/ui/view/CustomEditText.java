package com.romankaranchuk.translator.ui.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;


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
