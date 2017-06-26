package com.karanchuk.roman.testtranslate.presentation.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Created by roman on 23.6.17.
 */

public class GenClickableSpan extends ClickableSpan {
    private Context mContext;

    public GenClickableSpan(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(mContext, "span text clicked ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
        ds.setColor(ContextCompat.getColor(mContext, android.R.color.darker_gray));
        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
    }
}
