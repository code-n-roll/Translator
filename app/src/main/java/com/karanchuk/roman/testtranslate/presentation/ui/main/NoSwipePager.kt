package com.karanchuk.roman.testtranslate.presentation.ui.main

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


class NoSwipePager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var isSwipingEnabled = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipingEnabled) {
            return super.onTouchEvent(ev)
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipingEnabled) {
            return super.onInterceptTouchEvent(ev)
        }
        return false
    }

    fun setSwipingEnabled(isEnabled: Boolean) {
        isSwipingEnabled = isEnabled
    }
}