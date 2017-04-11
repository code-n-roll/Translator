package com.karanchuk.roman.testtranslate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.karanchuk.roman.testtranslate.favorites.FavoritesFragment;
import com.karanchuk.roman.testtranslate.translate.TranslateFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class MainActivity extends AppCompatActivity {

    private String mCurFragment = "TRANSLATE_FRAGMENT";
    public static String TRANSLATE_FRAGMENT = "TRANSLATE_FRAGMENT",
                        FAVORITES_FRAGMENT = "FAVORITES_FRAGMENT",
                        SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT";

    public void setCurFragment(String curFragment){
        this.mCurFragment = curFragment;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    if (!mCurFragment.equals(TRANSLATE_FRAGMENT)) {
                        setCurFragment(TRANSLATE_FRAGMENT);
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.main_activity_container,
                                        new TranslateFragment(), TRANSLATE_FRAGMENT).
                                commit();
                    }
                    return true;
                case R.id.navigation_favorites:
                    if (!mCurFragment.equals(FAVORITES_FRAGMENT)){
                        setCurFragment(FAVORITES_FRAGMENT);
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.main_activity_container,
                                        new FavoritesFragment(), FAVORITES_FRAGMENT).
                                commit();
                    }
                    return true;
                case R.id.navigation_settings:
                    if (!mCurFragment.equals(SETTINGS_FRAGMENT)) {
                        setCurFragment(SETTINGS_FRAGMENT);
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.main_activity_container,
                                        new SettingsFragment(), SETTINGS_FRAGMENT).
                                commit();
                    }
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_activity_container,
                        new TranslateFragment(), TRANSLATE_FRAGMENT).
                commit();

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (navigation.isShown() && isOpen){
                            navigation.setVisibility(View.INVISIBLE);
                        } else if (!navigation.isShown() && !isOpen){
                            navigation.setVisibility(View.VISIBLE);
                        }
                    }
                });



    }







//    if (getSupportActionBar() != null) {
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.actionbar_translate);
//    }
//    View actionbar = getSupportActionBar().getCustomView();
//    Button leftLang = (Button) actionbar.findViewById(R.id.left_actionbar_button),
//            rightLang = (Button) actionbar.findViewById(R.id.right_actionbar_button),
//            centerSeparator = (Button) actionbar.findViewById(R.id.center_actionbar_button);
//        leftLang.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//        }
//    });
//
//        rightLang.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//        }
//    });
//
//        centerSeparator.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//        }
//    });

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.choose_from_to_lang, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                 User chose the "Settings" item, show the app settings UI...
//                return true;
//
//            case R.id.action_favorite:
//                 User chose the "Favorite" action, mark the current item
//                 as a favorite...
//                return true;
//
//            default:
//                 If we got here, the user's action was not recognized.
//                 Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

}
