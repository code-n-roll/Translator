package com.karanchuk.roman.testtranslate.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

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
}
