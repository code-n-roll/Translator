package com.romankaranchuk.translator.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;


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
