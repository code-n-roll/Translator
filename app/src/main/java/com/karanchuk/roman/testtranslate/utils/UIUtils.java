package com.karanchuk.roman.testtranslate.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.karanchuk.roman.testtranslate.R;

import java.util.List;

/**
 * Created by roman on 17.4.17.
 */

public class UIUtils {
    public static void updateUIFragment(FragmentManager fm, String fragmentTag){
        Fragment storedFragment = fm.findFragmentByTag(fragmentTag);
        if (storedFragment != null) {
                    fm.beginTransaction()
                    .detach(storedFragment)
                    .attach(storedFragment)
                    .commit();
        }
    }

    public static void updateUIRecyclerView(FragmentManager fm, int viewId){
        Fragment favorites = fm.getFragments().get(1);
        if (favorites != null){
            View view = favorites.getView();
            if (view != null) {
                RecyclerView rc = (RecyclerView) view.findViewById(viewId);
                rc.getAdapter().notifyDataSetChanged();
            }
        }
    }
}
