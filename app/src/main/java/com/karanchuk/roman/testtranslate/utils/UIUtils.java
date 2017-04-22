package com.karanchuk.roman.testtranslate.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by roman on 17.4.17.
 */

public class UIUtils {
    public static void updateUIFragment(FragmentManager fm, String fragmentTag){
        Fragment fragment = fm.findFragmentByTag(fragmentTag);
        if (fragment != null) {
                    fm.beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
        }
    }

    public static void changeSoftInputModeWithOrientation(Activity curActivity){
        if (curActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            curActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } else {
            curActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
        }
    }

    public static int hideBottomNavViewGetBottomPadding(Activity curActivity,
                                                        View container,
                                                        BottomNavigationView navView){
        hideBottomNavView(navView);
        int bottomPadding = 0;
        if (curActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomPadding = container.getPaddingBottom();
            container.setPadding(0,0,0,0);
        }
        return bottomPadding;
    }

    public static void showBottomNavViewSetBottomPadding(Activity curActivity,
                                                        View container,
                                                        BottomNavigationView navView,
                                                        int bottomPadding){
        showBottomNavView(navView);
        if (curActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container.setPadding(0,0,0,bottomPadding);
        }
    }

    private static void showBottomNavView(BottomNavigationView navView){
        navView.setVisibility(View.VISIBLE);
    }

    private static void hideBottomNavView(BottomNavigationView navView){
        Animation anim = new TranslateAnimation(0,0,0,200);
        anim.setDuration(500);
        navView.startAnimation(anim);
        navView.setVisibility(View.INVISIBLE);
    }
}
