package com.romankaranchuk.translator.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class UIUtils {
    public static void updateUIFragment(final FragmentManager fm,
                                        final String fragmentTag){
        final Fragment fragment = fm.findFragmentByTag(fragmentTag);
        if (fragment != null) {
                    fm.beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
        }
    }

    public static void changeSoftInputModeWithOrientation(final Activity curActivity){
        if (curActivity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT){
            curActivity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } else {
            curActivity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

//    public static int hideBottomNavViewGetBottomPadding(
//            final Activity curActivity,
//            final View container,
//            final AHBottomNavigation navView
//    ){
//        hideBottomNavView(navView);
//        int bottomPadding = 0;
//        if (curActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            bottomPadding = container.getPaddingBottom();
//            container.setPadding(0,0,0,0);
//        }
//        return bottomPadding;
//    }

//    public static void showBottomNavViewSetBottomPadding(
//            final Activity curActivity,
//            final View container,
//            final AHBottomNavigation navView,
//            final int bottomPadding){
//        showBottomNavView(navView);
//        if (curActivity.getResources().getConfiguration().orientation ==
//                Configuration.ORIENTATION_LANDSCAPE) {
//            container.setPadding(0,0,0,bottomPadding);
//        }
//    }

//    private static void showBottomNavView(final AHBottomNavigation navView){
//        navView.setVisibility(View.VISIBLE);
//    }

//    private static void hideBottomNavView(final AHBottomNavigation navView){
//        Animation anim = new TranslateAnimation(0,0,0,200);
//        anim.setDuration(500);
//        navView.startAnimation(anim);
//        navView.setVisibility(View.INVISIBLE);
//    }

    public static void showToast(final Context context,
                                 final String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
