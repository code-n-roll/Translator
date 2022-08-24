package com.romankaranchuk.translator.ui.view

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.romankaranchuk.translator.R

/**
 * Created by roman on 23.6.17.
 */
class TextClickableSpan(
    private val mContext: Context,
    private val mOnItemClickListener: (View, String) -> Unit,
    private val mText: String
) : ClickableSpan() {
    override fun onClick(view: View) {
        mOnItemClickListener.invoke(view, mText)
        Toast.makeText(mContext, "span text clicked ", Toast.LENGTH_SHORT).show()
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
        ds.color = ContextCompat.getColor(mContext, R.color.colorTransl)
    }
}