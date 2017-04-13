package com.karanchuk.roman.testtranslate.translate;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.CustomEditText;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.TargetLangActivity;
import com.karanchuk.roman.testtranslate.data.DictDefinition;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.source.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.source.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.source.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.source_lang.SourceLangActivity;
import com.karanchuk.roman.testtranslate.utils.TranslateAPIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 8.4.17.
 */

public class TranslateFragment extends Fragment implements TranslatorRepository.TranslatedItemsRepositoryObserver{
    private View mView, mViewActionBar;
    private CustomEditText mCustomEditText;
    private ImageButton mGetPhoto, mGetVoiceRequest, mClearEditText;
    private RelativeLayout mTranslateResultContainer;
    private ActionBar mActionBar;
    private Button mButtonSrcLang, mButtonTrgLang;
    private ImageButton mButtonSwitchLang;
    private TextView mTranslateResult, mTvLink;
    private RecyclerView mTranslateRecyclerView;
    private ArrayList<DictDefinition> mDictDefinitions;
    private RecyclerView.LayoutManager mLayoutManager;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private List<TranslatedItem> mTranslatedItems;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_translate, container, false);

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());

        mRepository = TranslatorRepository.getInstance(localDataSource);
        mRepository.addContentObserver(this);

        if (mTranslatedItems == null)
            mTranslatedItems = mRepository.getTranslatedItems();


        mMainHandler = new Handler(getContext().getMainLooper());

        initToolbar();
        handleKeyboardVisibility();

        mLayoutManager = new LinearLayoutManager(mView.getContext());
        mViewActionBar = mActionBar.getCustomView();
        mButtonSrcLang = (Button) mViewActionBar.findViewById(R.id.left_actionbar_button);
        mButtonSwitchLang = (ImageButton) mViewActionBar.findViewById(R.id.center_actionbar_button);
        mButtonTrgLang = (Button) mViewActionBar.findViewById(R.id.right_actionbar_button);

        mCustomEditText = (CustomEditText) mView.findViewById(R.id.edittext);
        initCustomEditText();

        mClearEditText = (ImageButton) mView.findViewById(R.id.clearEditText);
        mTranslateResultContainer = (RelativeLayout) mView.findViewById(R.id.translate_result_container);

        mClearEditText.setVisibility(View.INVISIBLE);

        mGetPhoto = (ImageButton) mView.findViewById(R.id.getAudioSpelling);
        mGetVoiceRequest = (ImageButton) mView.findViewById(R.id.getVoiceRequest);

        mTranslateResult = (TextView) mView.findViewById(R.id.textview_translate_result);

        mTranslateRecyclerView = (RecyclerView) mView.findViewById(R.id.container_dict_defin);
        mTranslateRecyclerView.setLayoutManager(mLayoutManager);

        mDictDefinitions = new ArrayList<>();
        for (int i = 1; i <= 10; i++){
            mDictDefinitions.add(new DictDefinition(String.valueOf(i),
                    "время ср, раз м, момент м, срок м, пора ж, период м",
                    "(period, time, moment, pore)",
                    "dayling saving time \u2014 летнее время\ntake some time \u2014 занять некоторое время"));
        }
        mTranslateRecyclerView.setAdapter(new TranslateRecyclerAdapter(mDictDefinitions));



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
                startActivity(intent);
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
                startActivity(intent);
            }
        });



//        mTvLink = (TextView) mView.findViewById(R.id.tv_link);
//        mTvLink.setMovementMethod(LinkMovementMethod.getInstance());

        return mView;
    }


    public void initToolbar(){
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if ( mActionBar != null) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(R.layout.actionbar_translate);

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
                                mCustomEditText.setBackgroundDrawable(
                                        Drawable.createFromXml(
                                                getResources(),
                                                getResources().getLayout(R.layout.edittext_border_active)));
                                mCustomEditText.setCursorVisible(true);
                                mCustomEditText.requestFocus();
                            } catch (XmlPullParserException | IOException e) {
                                e.printStackTrace();
                            }
                        } else if (!isOpen && isAdded()){
                            try {
                                mCustomEditText.setBackgroundDrawable(
                                        Drawable.createFromXml(
                                                getResources(),
                                                getResources().getLayout(R.layout.edittext_border)));
                                mCustomEditText.setCursorVisible(false);
                                addToHistory();
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
        mCustomEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mCustomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && mCustomEditText.getText().length() != 0) {
                    try {
                        TranslateAPIUtils.getTranslate(mCustomEditText.getText().toString(), "ru-en", mTranslateResult);
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
                    mGetPhoto.setImageResource(R.drawable.audio_speaker_on);
                } else if (mCustomEditText.getText().length() == 0 && mClearEditText.isShown()){
                    mClearEditText.setVisibility(View.INVISIBLE);
                    mGetPhoto.setImageResource(R.drawable.photocamera);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mCustomEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });
//        showKeyboard();
    }

    public void addToHistory(){
        if (mCustomEditText.getText().length() != 0){
            TranslationSaver saver = new TranslationSaver();
            new Thread(saver).start();
        }
    }

    private class TranslationSaver implements Runnable{
        @Override
        public void run() {
            TranslatedItem item = new TranslatedItem(
                    mButtonSrcLang.getText().toString(),
                    mButtonTrgLang.getText().toString(),
                    mCustomEditText.getText().toString(),
                    mTranslateResult.getText().toString(),
                    "false",
                    mDictDefinitions.toString());
            mRepository.saveTranslatedItem(item);
        }
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



