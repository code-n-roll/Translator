package com.romankaranchuk.translator.ui.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.romankaranchuk.translator.R;


public class CommaClickableSpan extends ClickableSpan {
    private Context mContext;

    public CommaClickableSpan(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
        ds.setColor(ContextCompat.getColor(mContext, R.color.colorTransl));
    }
}
