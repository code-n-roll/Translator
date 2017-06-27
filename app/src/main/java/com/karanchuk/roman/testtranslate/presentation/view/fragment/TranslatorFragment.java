package com.karanchuk.roman.testtranslate.presentation.view.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.data.storage.TextDataStorage;
import com.karanchuk.roman.testtranslate.data.storage.TextDataStorageImpl;
import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;
import com.karanchuk.roman.testtranslate.presentation.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;
import com.karanchuk.roman.testtranslate.presentation.presenter.TranslatorPresenter;
import com.karanchuk.roman.testtranslate.presentation.presenter.impl.TranslatorPresenterImpl;
import com.karanchuk.roman.testtranslate.presentation.view.TranslatorView;
import com.karanchuk.roman.testtranslate.presentation.view.activity.FullscreenActivity;
import com.karanchuk.roman.testtranslate.presentation.view.activity.SourceLangActivity;
import com.karanchuk.roman.testtranslate.presentation.view.activity.TargetLangActivity;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.TranslatorRecyclerAdapter;
import com.karanchuk.roman.testtranslate.presentation.view.custom.CustomEditText;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CONT_ERROR_VISIBILITY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CONT_SUCCESS_VISIBILITY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_SELECTED_ITEM_SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_SELECTED_ITEM_TRG_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.presentation.Constants.IS_FAVORITE;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PROGRESS_BAR_VISIBILITY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.RECOGNIZING_REQUEST_PERMISSION_CODE;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRG_LANG;

/**
 * Created by roman on 8.4.17.
 */



public class TranslatorFragment extends Fragment implements TranslatorView {

    private final static int SRC_LANG_ACTIVITY_REQUEST_CODE = 1;
    private final static int TRG_LANG_ACTIVITY_REQUEST_CODE = 2;

    private ImageButton mButtonGetPhotoOrSourceVoice;
    private ImageButton mButtonGetAudioSpelling;
    private ImageButton mButtonGetTargetVoice;
    private ImageButton mButtonSetFavorite;
    private ImageButton mButtonShare;
    private ImageButton mButtonFullscreen;
    private ImageButton mClearEditText;
    private Button mButtonRetry;
    private LinearLayout mGeneralContainer;

    private ProgressBar mProgressDictionary;
    public ProgressBar mProgressTargetVoice;
    public ProgressBar mProgressSourceVoice;

    private ImageButton mButtonSwitchLang;
    private View mView;
    private ActionBar mActionBar;
    private BottomNavigationView mNavigation;
    public RecyclerView mTranslateRecyclerView;
    public CustomEditText mCustomEditText;
    public Button mButtonSrcLang;
    public Button mButtonTrgLang;
    public TextView mTranslatedResult;

    private RelativeLayout mContainerEdittext;
    private RelativeLayout mContainerSuccess;
    private RelativeLayout mContainerError;
    private FrameLayout mMainActivityContainer;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<TranslatedItem> mHistoryTranslatedItems;
    private ArrayList<Translation> mTranslations;
    private TranslatorRepository mRepository;

    private SharedPreferences mSettings;
//    private TranslationSaver mSaver;
    private int mBottomPadding;

    private AnimatorSet mAnimatorSet;
    private ImageView mCircleFirst;
    private ImageView mCircleSecond;
    private ImageView mCircleThird;
    private ImageView mCircleForth;

    private Animator mAnimatorSecond;
    private Animator mAnimatorSecondBack;
    private Animator mAnimatorThird;
    private Animator mAnimatorThirdBack;
    private Animator mAnimatorForth;
    private Animator mAnimatorForthBack;

    private TranslatorPresenter mPresenter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_translator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSettings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        mView = view;
//        mSaver = new TranslationSaver(getContext());

        TextDataStorage textDataStorage = new TextDataStorageImpl(this.getContext());
        setPresenter(new TranslatorPresenterImpl(this, textDataStorage));
        mPresenter.subscribe();

        initActionBar();
        findViewsOnFragment();
        findViewsOnActivity();
        findViewsOnActionBar();

        hideLoadingDictionary();
        hideLoadingTargetVoice();
        hideLoadingSourceVoice();
        hideRetry();

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.getInstance(getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
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

        initTranslateRecyclerView();
        UIUtils.changeSoftInputModeWithOrientation(getActivity());
        initCustomEditText();
        initEventListenerKeyboardVisibility();

        initListeners();
    }

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
        mButtonSetFavorite.setOnClickListener((ignored) -> clickOnSetFavoriteButton(mButtonSetFavorite));
        mButtonShare.setOnClickListener(this::clickOnShareButton);
    }

    private void findViewsOnFragment(){
        mCustomEditText = mView.findViewById(R.id.edittext);
        mButtonGetPhotoOrSourceVoice = mView.findViewById(R.id.get_source_voice);
        mButtonGetAudioSpelling = mView.findViewById(R.id.get_audio_spelling);
        mButtonGetTargetVoice = mView.findViewById(R.id.get_target_voice);
        mButtonSetFavorite = mView.findViewById(R.id.set_favorite);
        mButtonShare = mView.findViewById(R.id.share_translated_word);
        mButtonFullscreen = mView.findViewById(R.id.fullscreen_translated_word);
        mClearEditText = mView.findViewById(R.id.clear_edittext);
        mButtonRetry = mView.findViewById(R.id.button_connection_error_retry);
        mTranslatedResult = mView.findViewById(R.id.textview_translate_result);
        mContainerEdittext = mView.findViewById(R.id.container_edittext);
        mContainerSuccess = mView.findViewById(R.id.connection_succesful_content);
        mContainerError = mView.findViewById(R.id.connection_error_content);
        mTranslateRecyclerView = mView.findViewById(R.id.container_dict_defin);
        mGeneralContainer = mView.findViewById(R.id.general_container);
        mProgressDictionary = mView.findViewById(R.id.fragment_translator_progressbar);
        mProgressTargetVoice = mView.findViewById(R.id.get_target_voice_progress);
        mProgressSourceVoice = mView.findViewById(R.id.get_source_voice_progress);

        mCircleFirst = mView.findViewById(R.id.circle_first);
        mCircleSecond = mView.findViewById(R.id.circle_second);
        mCircleThird = mView.findViewById(R.id.circle_third);
        mCircleForth = mView.findViewById(R.id.circle_forth);
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
        mButtonSrcLang.setText(mSettings.getString(SRC_LANG, "Choose language"));
        mButtonTrgLang.setText(mSettings.getString(TRG_LANG, "Choose language"));
    }


    private boolean clickOnGeneralContainer(View view, MotionEvent event) {
        hideKeyboard();
//        UIUtils.showToast(getContext(), "clicked outside keyboard, keyboard hided");
        return true;
    }

    private void clickOnSrcLangButton(View view) {
        final Intent intent = new Intent(getActivity(), SourceLangActivity.class);
        startActivityForResult(intent,SRC_LANG_ACTIVITY_REQUEST_CODE);
    }

    private void clickOnSwitchLangButton(View view) {
        String oldSrcLang = mButtonSrcLang.getText().toString();
        String oldTrgLang = mButtonTrgLang.getText().toString();
        mButtonSrcLang.setText(oldTrgLang);
        mButtonTrgLang.setText(oldSrcLang);

        String srcLangAPI = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG,"");
        String trgLangAPI = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, trgLangAPI);
        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, srcLangAPI);
        editor.apply();

        if (!mCustomEditText.getText().toString().isEmpty()) {
            mCustomEditText.setText(mTranslatedResult.getText());
            showLoadingDictionary();
            hideSuccess();
            mPresenter.requestTranslatorAPI();
        }
    }

    private void clickOnTrgLangButton(View view) {
        final Intent intent = new Intent(getActivity(), TargetLangActivity.class);
        startActivityForResult(intent,TRG_LANG_ACTIVITY_REQUEST_CODE);
    }

    private void clickOnRetryButton(View view) {
        showLoadingDictionary();
        hideSuccess();
        mPresenter.requestTranslatorAPI();
        //        UIUtils.showToast(getContext(), "retry was clicked");
    }

    private void clickOnFullscreenButton(View view) {
        final Intent intent = new Intent(getActivity(), FullscreenActivity.class);
        intent.putExtra("translated_result", mTranslatedResult.getText().toString());
        startActivity(intent);
    }

    private void clickOnClearEditText(View view){
        mCustomEditText.getText().clear();
    }

    private void clickOnRecognizePhotoOrVocalizeSourceText(View view){
        showLoadingSourceVoice();
        hideIconSourceVoice();
        mPresenter.vocalizeSourceText();
    }

    private void clickOnRecognizeSourceText(View view){
        showAnimationMicroWaves();
        mPresenter.recognizeSourceText();
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
            mPresenter.recognizeSourceText();
        } else {
//            updateStatus("Record audio permission was not granted");
        }
    }


    private void clickOnVocalizeTargetText(View view){
        showLoadingTargetVoice();
        hideIconTargetVoice();
        mPresenter.vocalizeTargetText();
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
//        UIUtils.showToast(getContext(), "set favorite was clicked");
        UIUtils.showToast(getContext(), getResources().getString(R.string.set_favorite_message));
    }


    private void clickOnShareButton(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, mTranslatedResult.getText().toString());
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.chooser_title)));
    }


    public void initTranslateRecyclerView(){
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTranslateRecyclerView.setLayoutManager(mLayoutManager);

        final String dictDefString = mSettings.getString(TRANSL_CONTENT,"");


        mTranslations = new ArrayList<>();

        DictDefinition dictDefinition = null;
        if (!dictDefString.isEmpty()) {
            dictDefinition = new Gson().fromJson(dictDefString, DictDefinition.class);
            //        DictDefinition dictDefinition = JsonUtils.getDictDefinitionFromJson(
            //                JsonUtils.getJsonObjectFromFile(
            //                        getActivity().getAssets(),"translator_response.json"));
            if (dictDefinition != null) {
                for (PartOfSpeech POS : dictDefinition.getPartsOfSpeech()) {
                    mTranslations.addAll(POS.getTranslations());
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
        }
        if (dictDefinition != null){
            mTranslateRecyclerView.setAdapter(new TranslatorRecyclerAdapter(
                    mTranslations, dictDefinition.getPartsOfSpeech(), this::clickOnSynonymItem));
        } else {
            mTranslateRecyclerView.setAdapter(new TranslatorRecyclerAdapter(
                    mTranslations, null, this::clickOnSynonymItem));
        }
    }

    private void clickOnSynonymItem(View view, String text){
        if (!text.isEmpty()){
            mButtonSwitchLang.performClick();
            mCustomEditText.setText(text);
            showLoadingDictionary();
            hideSuccess();
            mPresenter.requestTranslatorAPI();
        }
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            restoreVisibility(savedInstanceState,mContainerError,CONT_ERROR_VISIBILITY);
            restoreVisibility(savedInstanceState, mContainerSuccess,CONT_SUCCESS_VISIBILITY);
            restoreVisibility(savedInstanceState, mProgressDictionary,PROGRESS_BAR_VISIBILITY);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mPresenter.unsubscribe();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    public void restoreVisibility(final Bundle savedInstanceState, final View view, final String key){
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
        outState.putString(CONT_SUCCESS_VISIBILITY, String.valueOf(mContainerSuccess.getVisibility()));
        outState.putString(PROGRESS_BAR_VISIBILITY, String.valueOf(mProgressDictionary.getVisibility()));
    }

    public void initActionBar(){
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


    public void initEventListenerKeyboardVisibility(){
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                isOpen -> {
                    // some code depending on keyboard visiblity status
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
//        hideClear();
//        hideKeyboard();
        hideIconSourceVoice();
        showActiveRecognizerInput();
        hideCursorInput();
        hideHintOnInput();
    }

    @Override
    public void desactivateVoiceRecognizer() {
//        showClear();
        showIconSourceVoice();
        showActiveBorderInput();
        showCursorInput();
        showHintOnInput();
    }

    private void showAnimationMicroWaves(){
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

    private void showHintOnInput(){
        mCustomEditText.setHint(getResources().getString(R.string.translate_hint));
    }

    private void hideHintOnInput(){
        mCustomEditText.setHint("");
    }

    private void showActiveBorderInput() {
        try {
            mContainerEdittext.setBackground(Drawable.createFromXml(getResources(),
                    getResources().getLayout(R.layout.edittext_border_active)));
        } catch (XmlPullParserException | IOException e){
            e.printStackTrace();
        }
    }

    private void hideActiveBorderInput() {
        try {
            mContainerEdittext.setBackground(Drawable.createFromXml(getResources(),
                    getResources().getLayout(R.layout.edittext_border)));
        } catch (XmlPullParserException | IOException e){
            e.printStackTrace();
        }
    }

    private void showActiveRecognizerInput() {
        try {
            mContainerEdittext.setBackground(Drawable.createFromXml(getResources(),
                    getResources().getLayout(R.layout.edittext_recognizer_active)));
        } catch (XmlPullParserException | IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void showKeyboard(){
        final InputMethodManager in = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void initCustomEditText(){
        mCustomEditText.setText(mSettings.getString(EDITTEXT_DATA, ""));
        if (!mCustomEditText.getText().toString().isEmpty()){
            showClear();
        } else {
            hideClear();
        }
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mCustomEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && mCustomEditText.getText().length() != 0) {
                showLoadingDictionary();
                hideSuccess();
                mPresenter.requestTranslatorAPI();
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

    private void handleRecognizerOnEdittext(View view){
        mPresenter.resetRecognizer();
    }

    public void clearContainerSuccess(){
        mTranslatedResult.setText("");
        mTranslations.clear();
        mPresenter.clearContainerSuccess();
    }


    @Override
    public void setPresenter(TranslatorPresenter presenter) {
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
        hideActiveBorderInput();
    }

    private void hideCursorInput(){
        mCustomEditText.setCursorVisible(false);
    }

    private void showCursorInput(){
        mCustomEditText.setCursorVisible(true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode){
            case SRC_LANG_ACTIVITY_REQUEST_CODE:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    String result = data.getStringExtra("result");

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
                    String result = data.getStringExtra("result");
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