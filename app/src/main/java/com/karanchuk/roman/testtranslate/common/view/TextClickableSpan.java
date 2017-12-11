package com.karanchuk.roman.testtranslate.common.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.translator.TranslatorRecyclerAdapter;

/**
 * Created by roman on 23.6.17.
 */

public class TextClickableSpan extends ClickableSpan {
    private Context mContext;
    private TranslatorRecyclerAdapter.OnItemClickListener mOnItemClickListener;
    private String mText;

    public TextClickableSpan(Context context,
                             TranslatorRecyclerAdapter.OnItemClickListener onItemClickListener,
                             String text) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
        mText = text;
    }

    @Override
    public void onClick(View view) {
        mOnItemClickListener.onSynonymItemClick(view, mText);
        Toast.makeText(mContext, "span text clicked ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
        ds.setColor(ContextCompat.getColor(mContext, R.color.colorTransl));
    }
}
