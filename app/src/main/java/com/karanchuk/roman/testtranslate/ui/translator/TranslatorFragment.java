package com.karanchuk.roman.testtranslate.ui.translator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.DictDefinition;
import com.karanchuk.roman.testtranslate.data.PartOfSpeech;
import com.karanchuk.roman.testtranslate.data.Translation;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.ui.fullscreen.FullscreenActivity;
import com.karanchuk.roman.testtranslate.ui.source_lang.SourceLangActivity;
import com.karanchuk.roman.testtranslate.ui.target_lang.TargetLangActivity;
import com.karanchuk.roman.testtranslate.ui.view.CustomEditText;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;
import com.karanchuk.roman.testtranslate.utils.TranslatorAPIUtils;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 8.4.17.
 */

public class TranslatorFragment extends Fragment implements
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        TranslatorAPIHolder.OnTranslatorAPIResultObserver
{
    private View mView, mViewActionBar;
    private CustomEditText mCustomEditText;
    private ImageButton mButtonGetPhotoOrSrcVoice,
            mButtonGetSourceVoice,
            mButtonGetTargetVoice,
            mButtonSetFavorite,
            mButtonShare,
            mButtonFullscreen,
            mClearEditText;
    private ActionBar mActionBar;
    private Button mButtonSrcLang, mButtonTrgLang;
    private ImageButton mButtonSwitchLang;
    private TextView mTranslatedResult, mTvLink;
    private RecyclerView mTranslateRecyclerView;
    private ArrayList<Translation> mTranslations;
    private RecyclerView.LayoutManager mLayoutManager;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private RelativeLayout mContainerEdittext,
                          mContainerSuccesful,
                            mContainerError;
    private LinearLayout mGeneralContainer;
    private FrameLayout mMainActivityContainer;
    private JsonObject mLanguagesMap;
    public static final String PREFS_NAME = "MyPrefsFile",
                               EDITTEXT_DATA = "EdittextData",
                                SRC_LANG = "ButtonSrcLangData",
                                TRG_LANG = "ButtonTrgLangData",
                                TRANSL_RESULT = "TextviewTranslResult",
                                TRANSL_CONTENT = "RecyclerViewTranslContent",
                                CONT_SUCCESS_VISIBILITY = "CONT_SUCCESS_VISIBILITY",
                                CONT_ERROR_VISIBILITY = "CONT_ERROR_VISIBILITY";
    private SharedPreferences mSettings;
    private TranslationSaver saver;
    private DictDefinition curDictDefinition;
    private BottomNavigationView mNavigation;
    private int mBottomPadding;
    private TranslatorAPIHolder mTranslatorAPIHolder;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_translator, container, false);

        mTranslatorAPIHolder = TranslatorAPIHolder.getInstance();
        mTranslatorAPIHolder.addOnTranslatorAPIResultObserver(this);


        mLanguagesMap  = JsonUtils.getJsonObjectFromFile(getActivity().getAssets(),"langs.json");
        mSettings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        mContainerSuccesful = (RelativeLayout) mView.findViewById(R.id.connection_succesful_content);
        mContainerError = (RelativeLayout) mView.findViewById(R.id.connection_error_content);
        mContainerError.setVisibility(View.INVISIBLE);

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());

        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addHistoryContentObserver(this);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);


        mMainHandler = new Handler(getContext().getMainLooper());
        saver = new TranslationSaver();


        mNavigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);


        UIUtils.changeSoftInputModeWithOrientation(getActivity());
        initToolbar();
        handleKeyboardVisibility();

        mViewActionBar = mActionBar.getCustomView();
        mButtonSwitchLang = (ImageButton) mViewActionBar.findViewById(R.id.center_actionbar_button);

        mButtonSrcLang = (Button) mViewActionBar.findViewById(R.id.left_actionbar_button);
        mButtonSrcLang.setText(mSettings.getString(SRC_LANG,"Choose language"));
        mButtonTrgLang = (Button) mViewActionBar.findViewById(R.id.right_actionbar_button);
        mButtonTrgLang.setText(mSettings.getString(TRG_LANG,"Choose language"));


        mCustomEditText = (CustomEditText) mView.findViewById(R.id.edittext);
        initCustomEditText();


        mClearEditText = (ImageButton) mView.findViewById(R.id.clearEditText);

        mClearEditText.setVisibility(View.INVISIBLE);

        mButtonGetPhotoOrSrcVoice = (ImageButton) mView.findViewById(R.id.getAudioSpelling);
        mButtonGetSourceVoice = (ImageButton) mView.findViewById(R.id.get_source_voice);

        mTranslatedResult = (TextView) mView.findViewById(R.id.textview_translate_result);
        mTranslatedResult.setText(mSettings.getString(TRANSL_RESULT,""));

        mButtonFullscreen = (ImageButton) mView.findViewById(R.id.fullscreen_translated_word);

        mContainerEdittext = (RelativeLayout) mView.findViewById(R.id.container_edittext);
        mGeneralContainer = (LinearLayout) mView.findViewById(R.id.general_container);
        mMainActivityContainer = (FrameLayout) getActivity().findViewById(R.id.main_activity_container);

        mButtonGetSourceVoice = (ImageButton) mView.findViewById(R.id.get_source_voice);
        mButtonGetTargetVoice = (ImageButton) mView.findViewById(R.id.get_target_voice);
        mButtonSetFavorite = (ImageButton) mView.findViewById(R.id.set_favorite);
        mButtonShare = (ImageButton) mView.findViewById(R.id.share_translated_word);


        mTranslateRecyclerView = (RecyclerView) mView.findViewById(R.id.container_dict_defin);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTranslateRecyclerView.setLayoutManager(mLayoutManager);

        String dictDefString = mSettings.getString(TRANSL_CONTENT,"");


        mTranslations = new ArrayList<>();

        if (!dictDefString.isEmpty()) {
            DictDefinition dictDefinition = JsonUtils.getDictDefinitionFromJson(
                    new JsonParser().parse(dictDefString).getAsJsonObject()
            );
            curDictDefinition = dictDefinition;
    //        DictDefinition dictDefinition = JsonUtils.getDictDefinitionFromJson(
    //                JsonUtils.getJsonObjectFromFile(
    //                        getActivity().getAssets(),"translator_response.json"));
            for (PartOfSpeech POS : dictDefinition.getPartsOfSpeech()){
                for (Translation transl : POS.getTranslations()){
                    mTranslations.add(transl);
                }
            }
        }

//        List<Synonym> synonyms = new ArrayList<>();
//        synonyms.add(new Synonym("время","ср"));
//        synonyms.add(new Synonym("раз","м"));
//        synonyms.add(new Synonym("момент","м"));
//        synonyms.add(new Synonym("срок","м"));
//        synonyms.add(new Synonym("пора","ж"));
//        synonyms.add(new Synonym("период","м"));
//
//        for (int i = 1; i <= 10; i++){
//            mTranslations.add(new Translation(String.valueOf(i),
//                    synonyms,"(period, time, moment, pore)",
//                    "dayling saving time \u2014 летнее время\ntake some time \u2014 занять некоторое время",
//                    synonyms.toString()));
//        }

        mTranslateRecyclerView.setAdapter(new TranslatorRecyclerAdapter(mTranslations));

        mButtonFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullscreenActivity.class);
                intent.putExtra("translated_result", mTranslatedResult.getText().toString());
                startActivity(intent);
            }
        });

        mClearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomEditText.getText().clear();
            }
        });


        mButtonSrcLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SourceLangActivity.class);
                startActivityForResult(intent,1);
            }
        });
        mButtonSwitchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String srcText = mButtonSrcLang.getText().toString(),
                        trgText = mButtonTrgLang.getText().toString();
                mButtonSrcLang.setText(trgText);
                mButtonTrgLang.setText(srcText);
            }
        });
        mButtonTrgLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TargetLangActivity.class);
                startActivityForResult(intent,2);
            }
        });

        mButtonGetPhotoOrSrcVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "get photo or source audio was clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mButtonGetSourceVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "get source voice was clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mButtonGetTargetVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "get target audio was clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mButtonSetFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "set favorite was clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "share was clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mGeneralContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                Toast.makeText(getContext(), "clicked outside keyboard, keyboard hided",Toast.LENGTH_SHORT).show();

                return false;
            }
        });


//        mTvLink = (TextView) mView.findViewById(R.id.tv_link);
//        mTvLink.setMovementMethod(LinkMovementMethod.getInstance());

        return mView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            restoreVisibility(savedInstanceState,mContainerError,CONT_ERROR_VISIBILITY);
            restoreVisibility(savedInstanceState,mContainerSuccesful,CONT_SUCCESS_VISIBILITY);
        }
    }

    public void restoreVisibility(Bundle savedInstanceState, View view, String key){
        switch (Integer.parseInt(savedInstanceState.getString(key))){
            case View.GONE:
                view.setVisibility(View.GONE);
                break;
            case View.INVISIBLE:
                view.setVisibility(View.INVISIBLE);
                break;
            case View.VISIBLE:
                view.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putString(CONT_ERROR_VISIBILITY, String.valueOf(mContainerError.getVisibility()));
        outState.putString(CONT_SUCCESS_VISIBILITY, String.valueOf(mContainerSuccesful.getVisibility()));
    }

    @Override
    public void onStop() {
        super.onStop();
        mRepository.removeHistoryContentObserver(this);
        mTranslatorAPIHolder.removeOnTranslatorAPIResultObserver(this);

        saveToSharedPreferences();
    }

    public void saveToSharedPreferences(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(EDITTEXT_DATA,mCustomEditText.getText().toString());
        editor.putString(SRC_LANG,mButtonSrcLang.getText().toString());
        editor.putString(TRG_LANG,mButtonTrgLang.getText().toString());
        editor.putString(TRANSL_RESULT, mTranslatedResult.getText().toString());

        if (saver.getDictDefinition() != null) {
            editor.putString(TRANSL_CONTENT, saver.getDictDefinition().getJsonToStringRepr());
        } else if (curDictDefinition != null){
            editor.putString(TRANSL_CONTENT, curDictDefinition.getJsonToStringRepr());
        }
        editor.apply();
    }


    public void initToolbar(){
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if ( mActionBar != null) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(R.layout.actionbar_translator);

            mActionBar.setShowHideAnimationEnabled(false);
            mActionBar.setElevation(0);
            mActionBar.setTitle("");
            mActionBar.show();
        }
    }


    public void handleKeyboardVisibility(){
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if (isOpen && isAdded()){
                            try {
                                mContainerEdittext.setBackground(
                                        Drawable.createFromXml(
                                                getResources(),
                                                getResources().getLayout(R.layout.edittext_border_active)));
                                mCustomEditText.setCursorVisible(true);


                                mBottomPadding =  UIUtils.hideBottomNavViewGetBottomPadding(getActivity(),mMainActivityContainer,mNavigation);
                            } catch (XmlPullParserException | IOException e) {
                                e.printStackTrace();
                            }
                        } else if (!isOpen && isAdded()){
                            try {
                                mContainerEdittext.setBackground(
                                        Drawable.createFromXml(
                                                getResources(),
                                                getResources().getLayout(R.layout.edittext_border)));
                                mCustomEditText.setCursorVisible(false);

                                UIUtils.showBottomNavViewSetBottomPadding(getActivity(),mMainActivityContainer,mNavigation,mBottomPadding);
                            } catch (XmlPullParserException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void hideKeyboard(){
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(mView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(){
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void initCustomEditText(){
        mCustomEditText.setText(mSettings.getString(EDITTEXT_DATA, ""));
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mCustomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && mCustomEditText.getText().length() != 0) {
                    try {
                        TranslatorAPIUtils.getTranslate(mCustomEditText.getText().toString(),
                                getActivity().getAssets(),
                                mButtonSrcLang.getText().toString(),
                                mButtonTrgLang.getText().toString(),
                                mTranslatedResult,
                                mTranslateRecyclerView,
                                saver,
                                mHistoryTranslatedItems,
                                mSettings
                                );
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    Log.d("keyboard state", "ACTION_DONE & customEditText is not empty");
                }
                return false;
            }
        });

        mCustomEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCustomEditText.getText().length() != 0 && !mClearEditText.isShown()){
                    mClearEditText.setVisibility(View.VISIBLE);
                    mButtonGetPhotoOrSrcVoice.setImageResource(R.drawable.volume_up_indicator_dark512);
                } else if (mCustomEditText.getText().length() == 0 && mClearEditText.isShown()){
                    mClearEditText.setVisibility(View.INVISIBLE);
                    mButtonGetPhotoOrSrcVoice.setImageResource(R.drawable.camera_dark512);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void addToHistory(){
//        if (mCustomEditText.getText().length() != 0){
//        }
    }

    public void clearCurTranslatorResultView(){
        mContainerError.setVisibility(View.INVISIBLE);
        mContainerSuccesful.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTranslatorAPIResult(boolean success) {
        if (success){
            mContainerSuccesful.setVisibility(View.VISIBLE);
            mContainerError.setVisibility(View.INVISIBLE);
        } else {
            mContainerSuccesful.setVisibility(View.INVISIBLE);
            mContainerError.setVisibility(View.VISIBLE);
        }
    }


    public class TranslationSaver implements Runnable{
        private DictDefinition mDictDefinition;

        public DictDefinition getDictDefinition() {
            return mDictDefinition;
        }

        public void setDictDefinition(DictDefinition dictDefinition) {
            mDictDefinition = dictDefinition;
        }

        @Override
        public void run() {

            TranslatedItem item = new TranslatedItem(
                    mLanguagesMap.get(mButtonSrcLang.getText().toString().toLowerCase()).getAsString().toUpperCase(),
                    mLanguagesMap.get(mButtonTrgLang.getText().toString().toLowerCase()).getAsString().toUpperCase(),
                    mButtonSrcLang.getText().toString(),
                    mButtonTrgLang.getText().toString(),
                    mCustomEditText.getText().toString(),
                    mTranslatedResult.getText().toString(),
                    "false",
                    mDictDefinition.getJsonToStringRepr()
                    );
            if (!mHistoryTranslatedItems.contains(item)) {
                mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
            } else {
                int index = mHistoryTranslatedItems.indexOf(item);
                item.setIsFavorite(mHistoryTranslatedItems.get(index).getIsFavorite());
                mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
            }
        }
    }




    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode){
            case 1:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    String result = data.getStringExtra("result");
                    mButtonSrcLang.setText(result);
                }
                break;
            case 2:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    String result = data.getStringExtra("result");
                    mButtonTrgLang.setText(result);
                }
                break;
            default:
                break;
        }
    }
}


//        View.OnClickListener editTextClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == mCustomEditText.getId()){
//                    mCustomEditText.setCursorVisible(true);
//                }
//            }
//        };
//        mCustomEditText.setOnClickListener(editTextClickListener);


//        View.OnClickListener lostFocusEditTextClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == mTranslateResultContainer.getId()){
//                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (in.isAcceptingText()) {
//                        in.hideSoftInputFromWindow(mCustomEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                        mCustomEditText.setCursorVisible(false);
////                        Log.d("keyboard state", "lostFocusEditText");
//                    }
//                }
//            }
//        };
//        mTranslateResultContainer.setOnClickListener(lostFocusEditTextClickListener);



