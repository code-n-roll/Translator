package com.karanchuk.roman.testtranslate.presentation.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karanchuk.roman.testtranslate.R;

/**
 * Created by roman on 8.4.17.
 */

public class SettingsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initActionBar();

        return view;
    }

    public void initActionBar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
            actionBar.setElevation(0);

            actionBar.setDisplayShowCustomEnabled(false);

            actionBar.setTitle("Settings");
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

            actionBar.show();
        }
    }
}
