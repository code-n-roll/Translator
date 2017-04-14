package com.karanchuk.roman.testtranslate.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.stored.StoredFragment;
import com.karanchuk.roman.testtranslate.ui.settings.SettingsFragment;
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment;
import com.karanchuk.roman.testtranslate.ui.view.ClearHistoryDialogFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ClearHistoryDialogFragment.ClearHistoryDialogListener,
        TranslatorRepository.TranslatedItemsRepositoryObserver{

    private String mCurFragment = "TRANSLATOR_FRAGMENT";
    public static String TRANSLATOR_FRAGMENT = "TRANSLATOR_FRAGMENT",
                        STORED_FRAGMENT = "STORED_FRAGMENT",
                        SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT";
    private List<TranslatedItem> mTranslatedItems;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;

    public void setCurFragment(String curFragment){
        this.mCurFragment = curFragment;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    if (!mCurFragment.equals(TRANSLATOR_FRAGMENT)) {
                        setCurFragment(TRANSLATOR_FRAGMENT);
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.main_activity_container,
                                        new TranslatorFragment(), TRANSLATOR_FRAGMENT).
                                commit();
                    }
                    return true;
                case R.id.navigation_favorites:
                    if (!mCurFragment.equals(STORED_FRAGMENT)){
                        setCurFragment(STORED_FRAGMENT);
                        getSupportFragmentManager().
                                beginTransaction().
                                replace(R.id.main_activity_container,
                                        new StoredFragment(), STORED_FRAGMENT).
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

        mMainHandler = new Handler(getMainLooper());

        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.main_activity_container,
                        new TranslatorFragment(), TRANSLATOR_FRAGMENT).
                commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(this);
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addContentObserver(this);
        mTranslatedItems = mRepository.getTranslatedItems();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mRepository.removeContentObserver(this);
    }


    @Override
    public void onDialogPositiveClick(ClearHistoryDialogFragment dialog) {
        Toast.makeText(this, "yes was clicked", Toast.LENGTH_SHORT).show();
        if (!mTranslatedItems.isEmpty())
            mRepository.deleteTranslatedItems();
        Fragment storedFragment = getSupportFragmentManager().findFragmentByTag(STORED_FRAGMENT);
        if (storedFragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(storedFragment)
                    .attach(storedFragment)
                    .commit();
        }
    }

    @Override
    public void onDialogNegativeClick(ClearHistoryDialogFragment dialog) {
        Toast.makeText(this,"cancel was clicked", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mTranslatedItems = mRepository.getTranslatedItems();
            }
        });
    }
}


//    if (getSupportActionBar() != null) {
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.actionbar_translator);
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