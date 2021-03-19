package com.karanchuk.roman.testtranslate.common.view;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.presentation.ui.edittextnavigator.EditTextLayoutPresenterImpl;

/**
 * Created by roman on 28.6.17.
 */

public class EditTextLayout extends RelativeLayout implements GestureDetector.OnGestureListener{
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    private EditTextLayoutPresenterImpl mPresenter;
    private Context mContext;
    private Animation mMoveUp;
    private LayoutParams mSavedLayoutParams;

    public EditTextLayout(Context context) {
        super(context);
        mContext = context;
        mPresenter = new EditTextLayoutPresenterImpl();

    }

    public EditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPresenter = new EditTextLayoutPresenterImpl();
        mMoveUp = AnimationUtils.loadAnimation(context, R.anim.move_up_edittext_layout);
        mMoveUp.setDuration(500);
    }

    public EditTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPresenter = new EditTextLayoutPresenterImpl();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditTextLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        mPresenter = new EditTextLayoutPresenterImpl();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent1,
                            MotionEvent motionEvent2,
                            float velocityX,
                            float velocityY) {

        offsetLeftAndRight(Math.min((int)(motionEvent2.getX() - motionEvent1.getX()),
                                    (int)(motionEvent2.getX() % 1000)));
        Log.d("myLogs onScroll ", String.valueOf("event1 x = " + motionEvent1.getX()) + " " +
                String.valueOf("event2 x = " + motionEvent2.getX()) +
                " getLeft() = " + String.valueOf(getLeft()) +
                " getWidth() = " + String.valueOf(getWidth()) +
                " alpha = " + getAlpha());

//        setAlpha(1 - ((float) Math.abs(getLeft()) / getWidth()) * 1.5f);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent1,
                           MotionEvent motionEvent2,
                           float velocityX,
                           float velocityY) {
        try {
            if (motionEvent1.getX() - motionEvent2.getX() > 0
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                startAnimation(mMoveUp);
//                setAlpha(1f);
                mPresenter.getPreviousItemFromHistory();
                Toast.makeText(mContext, "right to left", Toast.LENGTH_SHORT).show();
            } else if (motionEvent2.getX() - motionEvent1.getX() > 0
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                startAnimation(mMoveUp);

                mPresenter.getNextItemFromHistory();
                Toast.makeText(mContext, "left to right", Toast.LENGTH_SHORT).show();
            }

            Log.d("myLogs on Fling", String.valueOf("event1 x = " + motionEvent1.getX()) + " " +
                                     String.valueOf("event2 x = " + motionEvent2.getX()) + " " +
                                     String.valueOf("velocityX = " + velocityX));

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

}
