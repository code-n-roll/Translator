package com.karanchuk.roman.testtranslate.presentation.ui.translator;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl;
import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition;
import com.karanchuk.roman.testtranslate.data.database.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.database.model.Translation;
import com.karanchuk.roman.testtranslate.common.view.CustomEditText;
import com.karanchuk.roman.testtranslate.common.view.EditTextLayout;
import com.karanchuk.roman.testtranslate.presentation.ui.fullscreen.FullscreenActivity;
import com.karanchuk.roman.testtranslate.presentation.ui.sourcelang.SourceLangActivity;
import com.karanchuk.roman.testtranslate.presentation.ui.targetlang.TargetLangActivity;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.karanchuk.roman.testtranslate.common.Constants.CONT_ERROR_VISIBILITY;
import static com.karanchuk.roman.testtranslate.common.Constants.CONT_SUCCESS_VISIBILITY;
import static com.karanchuk.roman.testtranslate.common.Constants.CUR_SELECTED_ITEM_SRC_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.CUR_SELECTED_ITEM_TRG_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.common.Constants.IS_FAVORITE;
import static com.karanchuk.roman.testtranslate.common.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.common.Constants.PROGRESS_BAR_VISIBILITY;
import static com.karanchuk.roman.testtranslate.common.Constants.RECOGNIZING_REQUEST_PERMISSION_CODE;
import static com.karanchuk.roman.testtranslate.common.Constants.RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSLATED_RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRG_LANG;

/**
 * Created by roman on 8.4.17.
 */


public class TranslatorFragment extends Fragment implements TranslatorContract.View {

    public final static int SRC_LANG_ACTIVITY_REQUEST_CODE = 1;
    public final static int TRG_LANG_ACTIVITY_REQUEST_CODE = 2;

    @BindView(R.id.get_source_voice) ImageButton mButtonGetPhotoOrSourceVoice;
    @BindView(R.id.get_audio_spelling) ImageButton mButtonGetAudioSpelling;
    @BindView(R.id.get_target_voice) ImageButton mButtonGetTargetVoice;
    @BindView(R.id.set_favorite) ImageButton mButtonSetFavorite;
    @BindView(R.id.share_translated_word) ImageButton mButtonShare;
    @BindView(R.id.fullscreen_translated_word) ImageButton mButtonFullscreen;
    @BindView(R.id.clear_edittext) ImageButton mClearEditText;
    @BindView(R.id.button_connection_error_retry) Button mButtonRetry;
    @BindView(R.id.general_container) LinearLayout mGeneralContainer;
    @BindView(R.id.fragment_translator_progressbar) ProgressBar mProgressDictionary;
    @BindView(R.id.get_target_voice_progress) ProgressBar mProgressTargetVoice;
    @BindView(R.id.get_source_voice_progress) ProgressBar mProgressSourceVoice;
    @BindView(R.id.container_dict_defin) RecyclerView mTranslateRecyclerView;
    @BindView(R.id.edittext) CustomEditText mCustomEditText;
    @BindView(R.id.textview_translate_result) TextView mTranslatedResult;
    @BindView(R.id.container_edittext) EditTextLayout mContainerEditText;
    @BindView(R.id.connection_successful_content) RelativeLayout mContainerSuccess;
    @BindView(R.id.connection_error_content) RelativeLayout mContainerError;
    @BindView(R.id.circle_first) ImageView mCircleFirst;
    @BindView(R.id.circle_second) ImageView mCircleSecond;
    @BindView(R.id.circle_third) ImageView mCircleThird;
    @BindView(R.id.circle_forth) ImageView mCircleForth;

    private ImageButton mButtonSwitchLang;
    private View mView;
    private ActionBar mActionBar;
    private BottomNavigationView mNavigation;
    public Button mButtonSrcLang;
    public Button mButtonTrgLang;
    private FrameLayout mMainActivityContainer;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private ArrayList<Translation> mTranslations;
    private TranslatorRepositoryImpl mRepository;
    private SharedPreferences mSettings;
    private int mBottomPadding;

    private AnimatorSet mAnimatorSet;
    private Animator mAnimatorSecond;
    private Animator mAnimatorSecondBack;
    private Animator mAnimatorThird;
    private Animator mAnimatorThirdBack;
    private Animator mAnimatorForth;
    private Animator mAnimatorForthBack;

    private TranslatorContract.Presenter mPresenter;
    private GestureDetector mGestureDetector;
    private boolean isRecognizingSourceText;
    private Unbinder mUnbinder;

    @Override
    public void setTextButtonSrcLang(String text) {
        mButtonSrcLang.setText(text);
    }

    @Override
    public void setTextButtonTrgLang(String text) {
        mButtonTrgLang.setText(text);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translator, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSettings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        mView = view;

        setPresenter(new TranslatorPresenter(this, getContext()));
        mPresenter.attachView(getContext());

        initActionBar();
        findViewsOnActivity();
        findViewsOnActionBar();

        hideLoadingDictionary();
        hideLoadingTargetVoice();
        hideLoadingSourceVoice();
        hideRetry();

        TranslatorRepository localDataSource = TranslatorLocalRepository.getInstance(getContext());
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY);

        mTranslatedResult.setText(mSettings.getString(TRANSL_RESULT,""));
        if (Boolean.parseBoolean(mSettings.getString(IS_FAVORITE,""))) {
            mButtonSetFavorite.setImageResource(R.drawable.bookmark_black_shape_gold512);
        } else {
            mButtonSetFavorite.setImageResource(R.drawable.bookmark_black_shape_dark512);
        }
        if (mSettings.getString(TRANSL_CONTENT,"").isEmpty()) {
            hideSuccess();
        }

        if (isRecordAudioGranted()){
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_dark512);
        } else {
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_light512);
        }

        initTranslateRecyclerView();
        UIUtils.changeSoftInputModeWithOrientation(getActivity());
        initCustomEditText();
        initEventListenerKeyboardVisibility();
        initListeners();

        if (mCustomEditText.getText().toString().isEmpty()){
            mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.camera_dark512);
        } else {
            mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.volume_up_indicator_dark512);
        }

//        mGestureDetector = new GestureDetector(getContext(), mContainerEditText);
//        mCustomEditText.setOnTouchListener(mViewOnTouchListener);
    }

    @Override
    public boolean isEmptyTranslatedResultView() {
        return mTranslatedResult.getText().toString().isEmpty();
    }

    @Override
    public String getTextTranslatedResultView() {
        return mTranslatedResult.getText().toString();
    }

    @Override
    public void setTextCustomEditText(final String text) {
        mCustomEditText.setText(text);
    }

    @Override
    public boolean isEmptyCustomEditText() {
        return mCustomEditText.getText().toString().isEmpty();
    }

    @Override
    public void clearCustomEditText() {
        mCustomEditText.getText().clear();
    }

    @Override
    public boolean isRecognizingSourceText() {
        return isRecognizingSourceText;
    }

    public void setRecognizingSourceText(boolean recognizingSourceText) {
        isRecognizingSourceText = recognizingSourceText;
    }

//    private View.OnTouchListener mViewOnTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            mGestureDetector.onTouchEvent(motionEvent);
//            return false;
//        }
//    };

    private void initListeners(){
        mGeneralContainer.setOnTouchListener(this::clickOnGeneralContainer);
        mButtonSrcLang.setOnClickListener(this::clickOnSrcLangButton);
        mButtonSwitchLang.setOnClickListener(this::clickOnSwitchLangButton);
        mButtonTrgLang.setOnClickListener(this::clickOnTrgLangButton);
        mButtonRetry.setOnClickListener(this::clickOnRetryButton);
        mButtonFullscreen.setOnClickListener(this::clickOnFullscreenButton);
        mClearEditText.setOnClickListener(this::clickOnClearEditText);
        mButtonGetPhotoOrSourceVoice.setOnClickListener(this::clickOnRecognizePhotoOrVocalizeSourceText);
        mButtonGetAudioSpelling.setOnClickListener(this::clickOnRecognizeSourceText);
        mButtonGetTargetVoice.setOnClickListener(this::clickOnVocalizeTargetText);
        mButtonSetFavorite.setOnClickListener((empty) -> clickOnSetFavoriteButton(mButtonSetFavorite));
        mButtonShare.setOnClickListener(this::clickOnShareButton);
    }

    private void findViewsOnActivity(){
        mNavigation = getActivity().findViewById(R.id.navigation);
        mMainActivityContainer = getActivity().findViewById(R.id.main_activity_container);
    }

    private void findViewsOnActionBar(){
        View mActionBarView = mActionBar.getCustomView();
        mButtonSwitchLang = mActionBarView.findViewById(R.id.center_actionbar_button);
        mButtonSrcLang = mActionBarView.findViewById(R.id.left_actionbar_button);
        mButtonTrgLang = mActionBarView.findViewById(R.id.right_actionbar_button);
        String title = getResources().getString(R.string.title_choose_lang);
        mButtonSrcLang.setText(mSettings.getString(SRC_LANG, title));
        mButtonTrgLang.setText(mSettings.getString(TRG_LANG, title));
    }

    @Override
    public boolean isRecordAudioGranted() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestRecordAudioPermissions() {
        requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO},
                RECOGNIZING_REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RECOGNIZING_REQUEST_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
//            mPresenter.recognizeSourceText();
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_dark512);
        } else {
            UIUtils.showToast(getContext(),
                    getResources().getString(R.string.record_audio_not_granted));
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_light512);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            restoreVisibility(savedInstanceState, mContainerError,CONT_ERROR_VISIBILITY);
            restoreVisibility(savedInstanceState, mContainerSuccess,CONT_SUCCESS_VISIBILITY);
            restoreVisibility(savedInstanceState, mProgressDictionary,PROGRESS_BAR_VISIBILITY);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mPresenter.detachView();
        mUnbinder.unbind();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(CONT_ERROR_VISIBILITY, String.valueOf(mContainerError.getVisibility()));
        outState.putString(CONT_SUCCESS_VISIBILITY, String.valueOf(mContainerSuccess.getVisibility()));
        outState.putString(PROGRESS_BAR_VISIBILITY, String.valueOf(mProgressDictionary.getVisibility()));
    }

    @Override
    public void hideKeyboard(){
        final InputMethodManager in = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(mView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void showClear() {
        mClearEditText.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideClear() {
        mClearEditText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoadingTargetVoice() {
        mProgressTargetVoice.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingTargetVoice() {
        mProgressTargetVoice.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showIconTargetVoice() {
        mButtonGetTargetVoice.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideIconTargetVoice() {
        mButtonGetTargetVoice.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoadingSourceVoice() {
        mProgressSourceVoice.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingSourceVoice() {
        mProgressSourceVoice.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showIconSourceVoice() {
        mButtonGetPhotoOrSourceVoice.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideIconSourceVoice() {
        mButtonGetPhotoOrSourceVoice.setVisibility(View.INVISIBLE);
    }

    @Override
    public void activateVoiceRecognizer() {
        hideClear();
        hideKeyboard();
        hideIconSourceVoice();
        showActiveRecognizerInput();
        hideCursorInput();
        setHintRecognizer();
    }

    @Override
    public void desactivateVoiceRecognizer() {
        if (!mCustomEditText.getText().toString().isEmpty()) {
            showClear();
        } else {
            hideClear();
        }
        showIconSourceVoice();
        showActiveBorderInput();
        showCursorInput();
        setHintOnInput();
        isRecognizingSourceText = false;
    }

    @Override
    public void showAnimationMicroWaves(){
        mCircleFirst.setAlpha(1f);

        mAnimatorSecond = AnimatorInflater.loadAnimator(getContext(),
                R.animator.micro_waves_second);
        mAnimatorSecondBack = AnimatorInflater.loadAnimator(getContext(),
                R.animator.micro_waves_second_back);

        mAnimatorThird = AnimatorInflater.loadAnimator(getContext(),
                R.animator.micro_waves_third);
        mAnimatorThirdBack = AnimatorInflater.loadAnimator(getContext(),
                R.animator.micro_waves_third_back);

        mAnimatorForth = AnimatorInflater.loadAnimator(getContext(),
                R.animator.micro_waves_forth);
        mAnimatorForthBack = AnimatorInflater.loadAnimator(getContext(),
                R.animator.micro_waves_forth_back);

        mAnimatorSecond.setTarget(mCircleSecond);
        mAnimatorSecondBack.setTarget(mCircleSecond);
        mAnimatorThird.setTarget(mCircleThird);
        mAnimatorThirdBack.setTarget(mCircleThird);
        mAnimatorForth.setTarget(mCircleForth);
        mAnimatorForthBack.setTarget(mCircleForth);

        mAnimatorSet = new AnimatorSet();

        mAnimatorSet.play(mAnimatorSecond).before(mAnimatorSecondBack);
        mAnimatorSet.play(mAnimatorSecondBack)
                .after(getResources().getInteger(R.integer.dur_second_to_back));

        mAnimatorSet.play(mAnimatorThird).after(mAnimatorSecond);
        mAnimatorSet.play(mAnimatorThird).before(mAnimatorThirdBack);
        mAnimatorSet.play(mAnimatorThirdBack)
                .after(getResources().getInteger(R.integer.dur_third_to_back));

        mAnimatorSet.play(mAnimatorForth).after(mAnimatorThird);
        mAnimatorSet.play(mAnimatorForth).before(mAnimatorForthBack);
        mAnimatorSet.play(mAnimatorForthBack)
                .after(getResources().getInteger(R.integer.dur_forth_to_back));
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.start();
    }

    @Override
    public void stopAnimationMicroWaves(){
        mAnimatorSet.removeAllListeners();

        mAnimatorSet.end();
        mAnimatorSecond.end();
        mAnimatorSecondBack.end();
        mAnimatorThird.end();
        mAnimatorThirdBack.end();
        mAnimatorForth.end();
        mAnimatorForthBack.end();

        mCircleFirst.setAlpha(0f);
        mCircleSecond.setAlpha(0f);
        mCircleThird.setAlpha(0f);
        mCircleForth.setAlpha(0f);
    }

    @Override
    public void showKeyboard(){
        final InputMethodManager in = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public String getTextButtonSrcLang() {
        return mButtonSrcLang.getText().toString();
    }

    @Override
    public String getTextButtonTrgLang() {
        return mButtonTrgLang.getText().toString();
    }

    @Override
    public void clickOnButtonSwitchLang() {
        mButtonSwitchLang.performClick();
    }

    @Override
    public TranslatedItem createPredictedTranslatedItem() {
        String curEditTextContent = mCustomEditText.getText().toString().trim();
        String srcLangAPI = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG,"");
        String trgLangAPI = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");

        return new TranslatedItem(srcLangAPI, trgLangAPI, null, null,
                        curEditTextContent, null, null, null);
    }

    @Override
    public void getTranslatedItemFromCache(TranslatedItem maybeExistedItem){
        int id = mHistoryTranslatedItems.indexOf(maybeExistedItem);
        if (id != -1) {
            String dictDefinitionJSON =
                    mHistoryTranslatedItems.get(id).getDictDefinitionJSON();
            DictDefinition existedItem =
                    new Gson().fromJson(dictDefinitionJSON, DictDefinition.class);
            mPresenter.handleDictionaryResponse(existedItem);
            mTranslatedResult.setText(mHistoryTranslatedItems.get(id).getTrgMeaning());
            Log.d("myLogs", mHistoryTranslatedItems.get(id).getTrgMeaning());
        }
    }

    @Override
    public void setPresenter(TranslatorContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoadingDictionary() {
        mProgressDictionary.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingDictionary() {
        mProgressDictionary.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showRetry() {
        mContainerError.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetry() {
        mContainerError.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSuccess() {
        mContainerSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSuccess() {
        mContainerSuccess.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showActiveInput() {
        showCursorInput();
        mBottomPadding =  UIUtils.hideBottomNavViewGetBottomPadding(getActivity(),
                mMainActivityContainer, mNavigation);
        showActiveBorderInput();
    }

    @Override
    public void hideActiveInput() {
        hideCursorInput();
        UIUtils.showBottomNavViewSetBottomPadding(getActivity(), mMainActivityContainer,
                mNavigation, mBottomPadding);
        if (isRecognizingSourceText){
            showActiveRecognizerInput();
        } else {
            hideActiveBorderInput();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode){
            case SRC_LANG_ACTIVITY_REQUEST_CODE:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    String result = data.getStringExtra(RESULT);

//                    if (!mButtonSrcLang.getText().equals(result) &&
//                            !mTranslatedResult.getText().toString().isEmpty()) {
//                        mButtonSrcLang.setText(mButtonTrgLang.getText());
//                        mButtonTrgLang.setText(result);
//                        mCustomEditText.setText(mTranslatedResult.getText());

//                        JsonObject languagesMap = JsonUtils.getJsonObjectFromAssetsFile(getContext(), "langs.json");
//
//                        String srcLangAPI = languagesMap.get(mButtonSrcLang.getText().toString().toLowerCase()).getAsString();
//                        String trgLangAPI = languagesMap.get(mButtonTrgLang.getText().toString().toLowerCase()).getAsString();
//
//                        SharedPreferences.Editor editor = mSettings.edit();
//                        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, srcLangAPI);
//                        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, trgLangAPI);
//                        editor.apply();

//                    } else if (mButtonSrcLang.getText().equals(result)){
//                        mButtonSwitchLang.performClick();
//                    } else {
                        mButtonSrcLang.setText(result);
//                    }
                    if (!mCustomEditText.getText().toString().isEmpty()) {
                        showLoadingDictionary();
                        hideSuccess();
                        mPresenter.requestTranslatorAPI();
                    }
                }
                break;
            case TRG_LANG_ACTIVITY_REQUEST_CODE:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    String result = data.getStringExtra(RESULT);
//                    if (mButtonTrgLang.getText().equals(result)) {
//                        mButtonSrcLang.setText(mButtonTrgLang.getText());
//                        mCustomEditText.setText(mTranslatedResult.getText());
//                    }
                    mButtonTrgLang.setText(result);
                    if (!mCustomEditText.getText().toString().isEmpty()) {
                        showLoadingDictionary();
                        hideSuccess();
                        mPresenter.requestTranslatorAPI();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setHintOnInput(){
        mCustomEditText.setHint(getResources().getString(R.string.translate_hint));
    }

    @Override
    public void showError() {
        hideLoadingTargetVoice();
        hideLoadingSourceVoice();
        showIconTargetVoice();
        showIconSourceVoice();
        stopAnimationMicroWaves();
        setHintOnInput();
        showActiveInput();
        setRecognizingSourceText(false);
    }

    private void initCustomEditText(){
        mCustomEditText.setText(mSettings.getString(EDITTEXT_DATA, ""));
        if (!mCustomEditText.getText().toString().isEmpty()){
            showClear();
        } else {
            hideClear();
        }
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mCustomEditText.setOnEditorActionListener((v, actionId, event) -> {
            String curEditTextContent = mCustomEditText.getText().toString().trim();
            TranslatedItem maybeExistedItem = createPredictedTranslatedItem();

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!curEditTextContent.isEmpty() &&
                        !mHistoryTranslatedItems.contains(maybeExistedItem)){
                    if (mPresenter.requestTranslatorAPI()) {
                        showLoadingDictionary();
                        hideSuccess();
                    }
                } else {
                    getTranslatedItemFromCache(maybeExistedItem);
                }
                Log.d("keyboard state", "ACTION_DONE & customEditText is not empty");
            }
            return false;
        });

        mCustomEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mContainerError.getVisibility() == View.VISIBLE)
                    hideRetry();
                if (mCustomEditText.getText().length() != 0 && !mClearEditText.isShown()){
                    showClear();
                    mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.volume_up_indicator_dark512);
                } else if (mCustomEditText.getText().length() == 0 && mClearEditText.isShown()){
                    hideClear();
                    hideSuccess();
                    clearContainerSuccess();
                    mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.camera_dark512);
                    mPresenter.saveToSharedPreferences();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mCustomEditText.setOnClickListener(this::handleRecognizerOnEdittext);
    }

    private void setHintRecognizer(){
        mCustomEditText.setHint(getResources().getString(R.string.recognizer_hint));
    }

    private void hideHintOnInput(){
        mCustomEditText.setHint("");
    }

    private void showActiveBorderInput() {
        try {
            mContainerEditText.setBackground(Drawable.createFromXml(getResources(),
                    getResources().getLayout(R.layout.edittext_border_active)));
        } catch (XmlPullParserException | IOException e){
            e.printStackTrace();
        }
    }

    private void hideActiveBorderInput() {
        try {
            mContainerEditText.setBackground(Drawable.createFromXml(getResources(),
                    getResources().getLayout(R.layout.edittext_border)));
        } catch (XmlPullParserException | IOException e){
            e.printStackTrace();
        }
    }

    private void showActiveRecognizerInput() {
        try {
            mContainerEditText.setBackground(Drawable.createFromXml(getResources(),
                    getResources().getLayout(R.layout.edittext_recognizer_active)));
        } catch (XmlPullParserException | IOException e){
            e.printStackTrace();
        }
    }

    private void hideCursorInput(){
        mCustomEditText.setCursorVisible(false);
    }

    private void showCursorInput(){
        mCustomEditText.setCursorVisible(true);
    }

    private void handleRecognizerOnEdittext(View view){
        mPresenter.resetRecognizer();
    }

    public void clearContainerSuccess(){
        mTranslatedResult.setText("");
        mTranslations.clear();
        mPresenter.clearContainerSuccess();
    }

    private void initActionBar(){
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setCustomView(R.layout.actionbar_translator);

            mActionBar.setShowHideAnimationEnabled(false);
            mActionBar.setElevation(0);
            mActionBar.setTitle("");
            mActionBar.show();
        }
    }

    private void initEventListenerKeyboardVisibility(){
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                isOpen -> {
                    if (isOpen && isAdded()){
                        showActiveInput();
                    } else if (!isOpen && isAdded()){
                        hideActiveInput();
//                        if (!mCustomEditText.getText().toString().isEmpty() &&
//                                mSaver != null &&
//                                mSaver.getCurTranslatedItem() != null &&
//                                !mSaver.getCurTranslatedItem()
//                                        .getSrcMeaning()
//                                        .equals(mCustomEditText.getText().toString())) {
//                            showLoadingDictionary();
//                            mPresenter.requestTranslatorAPI();
//                        }
                    }
                });
    }


    private void restoreVisibility(final Bundle savedInstanceState,
                                   final View view,
                                   final String key){
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

    private void initTranslateRecyclerView(){
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTranslateRecyclerView.setLayoutManager(mLayoutManager);

        final String dictDefString = mSettings.getString(TRANSL_CONTENT,"");


        mTranslations = new ArrayList<>();

        DictDefinition dictDefinition = null;
        if (!dictDefString.isEmpty()) {
            dictDefinition = new Gson().fromJson(dictDefString, DictDefinition.class);
            if (dictDefinition != null) {
                for (PartOfSpeech POS : dictDefinition.getPartsOfSpeech()) {
                    mTranslations.addAll(POS.getTranslations());
                }
            }
        }
        if (dictDefinition != null){
            mTranslateRecyclerView.setAdapter(new TranslatorRecyclerAdapter(
                    mTranslations,
                    dictDefinition.getPartsOfSpeech(),
                    this::clickOnSynonymItem));
        } else {
            mTranslateRecyclerView.setAdapter(new TranslatorRecyclerAdapter(
                    mTranslations,
                    null,
                    this::clickOnSynonymItem));
        }
    }

    private boolean clickOnGeneralContainer(View view, MotionEvent event) {
        hideKeyboard();
//        UIUtils.showToast(getContext(), "clicked outside keyboard, keyboard hided");
        return true;
    }

    private void clickOnSrcLangButton(View view) {
        final Intent intent = new Intent(getContext(), SourceLangActivity.class);
        startActivityForResult(intent, SRC_LANG_ACTIVITY_REQUEST_CODE);
    }

    private void clickOnSwitchLangButton(View view) {
        String oldSrcLang = getTextButtonSrcLang();
        String oldTrgLang = getTextButtonTrgLang();
        setTextButtonSrcLang(oldTrgLang);
        setTextButtonTrgLang(oldSrcLang);

        String srcLangAPI = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG,"");
        String trgLangAPI = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, trgLangAPI);
        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, srcLangAPI);
        editor.apply();

        setTextCustomEditText(getTextTranslatedResultView());
        if (!isEmptyCustomEditText() && mPresenter.requestTranslatorAPI()) {
            showLoadingDictionary();
            hideSuccess();
        } else {
            getTranslatedItemFromCache(createPredictedTranslatedItem());
        }
    }

    private void clickOnTrgLangButton(View view) {
        final Intent intent = new Intent(getContext(), TargetLangActivity.class);
        startActivityForResult(intent, TRG_LANG_ACTIVITY_REQUEST_CODE);
    }

    private void clickOnRetryButton(View view) {
        showLoadingDictionary();
        hideSuccess();
        mPresenter.requestTranslatorAPI();
    }

    private void clickOnFullscreenButton(View view) {
        final Intent intent = new Intent(getContext(), FullscreenActivity.class);
        intent.putExtra(TRANSLATED_RESULT, getTextTranslatedResultView());
        startActivity(intent);
    }

    private void clickOnClearEditText(View view){
        clearCustomEditText();
    }

    private void clickOnRecognizePhotoOrVocalizeSourceText(View view){
        if (!isEmptyCustomEditText()) {
            showLoadingSourceVoice();
            hideIconSourceVoice();
            mPresenter.vocalizeSourceText();
        } else {
            UIUtils.showToast(getContext(), getContext().getResources()
                    .getString(R.string.try_to_get_photo));
        }
    }

    private void clickOnRecognizeSourceText(View view){
        if (!isRecordAudioGranted()){
            requestRecordAudioPermissions();
        }
        if (!isRecognizingSourceText() && isRecordAudioGranted()) {
            setRecognizingSourceText(true);
            showAnimationMicroWaves();
            mPresenter.recognizeSourceText();
        } else {
            setRecognizingSourceText(false);
            mPresenter.resetRecognizer();
        }
    }

    private void clickOnSetFavoriteButton(final ImageButton view) {
//         final TranslatedItem item = mSaver.getCurTranslatedItem();
//         if (!item.isFavorite()) {
//             item.isFavoriteUp(true);
//             mSaver.setCurTranslatedItem(item);
//             mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
//             view.setImageResource(R.drawable.bookmark_black_shape_gold512);
//         } else {
//             item.isFavoriteUp(false);
//             mSaver.setCurTranslatedItem(item);
//             mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
//             view.setImageResource(R.drawable.bookmark_black_shape_dark512);
//         }
//        mRepository.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
//        UIUtils.showToast(mContext, "set favorite was clicked");
        UIUtils.showToast(getContext(), getContext().getResources().getString(R.string.set_favorite_message));
    }


    private void clickOnShareButton(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getContext().getResources().getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getTextTranslatedResultView());
        startActivity(Intent.createChooser(intent, getContext().getResources().getString(R.string.chooser_title)));
    }

    private void clickOnSynonymItem(View view, String text){
        if (!text.isEmpty()) {
            showLoadingDictionary();
            hideSuccess();
            mPresenter.requestTranslatorAPI();
        }
    }

    private void clickOnVocalizeTargetText(View view) {
        if (!isEmptyTranslatedResultView()) {
            showLoadingTargetVoice();
            hideIconTargetVoice();
            mPresenter.vocalizeTargetText();
        } else {
            UIUtils.showToast(getContext(),
                    getContext().getResources().getString(R.string.try_vocalize_empty_result));
        }
    }
}