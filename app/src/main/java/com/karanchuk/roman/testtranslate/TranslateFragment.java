package com.karanchuk.roman.testtranslate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by roman on 8.4.17.
 */

public class TranslateFragment extends Fragment{
    private View mView;
    private CustomEditText mCustomEditText;
    private ImageButton mGetPhoto, mGetVoiceRequest, mClearEditText;
    private RelativeLayout mTranslateResult;



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_translate, container, false);
        initToolbar();

        mCustomEditText = (CustomEditText) mView.findViewById(R.id.edittext);
        mClearEditText = (ImageButton) mView.findViewById(R.id.clearEditText);
        mTranslateResult = (RelativeLayout) mView.findViewById(R.id.translate_result_container);

        mClearEditText.setVisibility(View.INVISIBLE);

        mGetPhoto = (ImageButton) mView.findViewById(R.id.getAudioSpelling);
        mGetVoiceRequest = (ImageButton) mView.findViewById(R.id.getVoiceRequest);

        View.OnClickListener editTextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == mCustomEditText.getId()){
                    mCustomEditText.setCursorVisible(true);
                }
            }
        };
        mCustomEditText.setOnClickListener(editTextClickListener);
        mCustomEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        View.OnClickListener lostFocusEditTextClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == mTranslateResult.getId()){
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (in.isAcceptingText()) {
                        in.hideSoftInputFromWindow(mCustomEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        mCustomEditText.setCursorVisible(false);
                    }
                }
            }
        };
        mTranslateResult.setOnClickListener(lostFocusEditTextClickListener);

        mCustomEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mCustomEditText.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(mCustomEditText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
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
                Log.d("loggind edittext", "onTextChanged");
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

        mClearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomEditText.getText().clear();
            }
        });


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
                                                getResources().getXml(R.layout.edittext_border_active)));
                            } catch (XmlPullParserException | IOException e) {
                                e.printStackTrace();
                            }
                        } else if (!isOpen && isAdded()){
                            try {
                                mCustomEditText.setBackgroundDrawable(
                                        Drawable.createFromXml(
                                                getResources(),
                                                getResources().getXml(R.layout.edittext_border)));
                            } catch (XmlPullParserException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        return mView;
    }




    public void initToolbar(){
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if ( actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
            actionBar.show();
            actionBar.setElevation(0);
            actionBar.setTitle("");
        }
    }


}
