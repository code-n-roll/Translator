package com.karanchuk.roman.testtranslate.presentation.view.fragment;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract.TranslatedItemEntry;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;
import com.karanchuk.roman.testtranslate.presentation.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;
import com.karanchuk.roman.testtranslate.presentation.presenter.translator.TranslatorPresenter;
import com.karanchuk.roman.testtranslate.presentation.view.TranslatorView;
import com.karanchuk.roman.testtranslate.presentation.view.activity.FullscreenActivity;
import com.karanchuk.roman.testtranslate.presentation.view.activity.SourceLangActivity;
import com.karanchuk.roman.testtranslate.presentation.view.activity.TargetLangActivity;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.TranslatorRecyclerAdapter;
import com.karanchuk.roman.testtranslate.presentation.view.custom.CustomEditText;
import com.karanchuk.roman.testtranslate.presentation.view.state_holder.TranslatorStateHolder;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;
import com.karanchuk.roman.testtranslate.utils.TranslatorAPIUtils;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.karanchuk.roman.testtranslate.presentation.Constants.CONT_ERROR_VISIBILITY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CONT_SUCCESS_VISIBILITY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_SELECTED_ITEM_SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.CUR_SELECTED_ITEM_TRG_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.presentation.Constants.IS_FAVORITE;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PROGRESS_BAR_VISIBILITY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRG_LANG;

/**
 * Created by roman on 8.4.17.
 */



public class TranslatorFragment extends Fragment implements
    TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
    TranslatorStateHolder.OnTranslatorStateObserver,
    TranslatorView {

    private final static int SRC_LANG_ACTIVITY_REQUEST_CODE = 1;
    private final static int TRG_LANG_ACTIVITY_REQUEST_CODE = 2;

    private CustomEditText mCustomEditText;
    private ImageButton mButtonGetPhotoOrSrcVoice;
    private ImageButton mButtonGetSourceVoice;
    private ImageButton mButtonGetTargetVoice;
    private ImageButton mButtonSetFavorite;
    private ImageButton mButtonShare;
    private ImageButton mButtonFullscreen;
    private ImageButton mClearEditText;
    private Button mButtonRetry;
    private TextView mTranslatedResult;
    private RecyclerView mTranslateRecyclerView;
    private LinearLayout mGeneralContainer;
    private ProgressBar mProgressBar;
    private ImageButton mButtonSwitchLang;
    private Button mButtonSrcLang;
    private Button mButtonTrgLang;
    private View mView;
    private ActionBar mActionBar;
    private BottomNavigationView mNavigation;

    private RelativeLayout mContainerEdittext;
    private RelativeLayout mContainerSuccesful;
    private RelativeLayout mContainerError;
    private FrameLayout mMainActivityContainer;

    private RecyclerView.LayoutManager mLayoutManager;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private ArrayList<Translation> mTranslations;
    private JsonObject mLanguagesMap;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;

    private SharedPreferences mSettings;
    private TranslationSaver mSaver;
    private DictDefinition mCurDictDefinition;
    private int mBottomPadding;
    private TranslatorStateHolder mTranslatorStateHolder;

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

        initActionBar();
        findViewsOnFragment();
        findViewsOnActivity();
        findViewsOnActionBar();

        mProgressBar.setVisibility(View.INVISIBLE);
        mContainerError.setVisibility(View.INVISIBLE);

        mTranslatorStateHolder = TranslatorStateHolder.getInstance();
        mLanguagesMap = JsonUtils.getJsonObjectFromFile(getActivity().getAssets(), "langs.json");
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
            mContainerSuccesful.setVisibility(View.INVISIBLE);
        }

        initTranslateRecyclerView();
        UIUtils.changeSoftInputModeWithOrientation(getActivity());
        initCustomEditText();
        initEventListenerKeyboardVisibility();


        mMainHandler = new Handler(getContext().getMainLooper());
        mSaver = new TranslationSaver();

        initListeners();


//        mTvLink = (TextView) mView.findViewById(R.id.tv_link);
//        mTvLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initListeners(){
        mGeneralContainer.setOnTouchListener((ignored, event) -> clickOnGeneralContainer(ignored, event));
        mButtonSrcLang.setOnClickListener(this::clickOnSrcLangButton);
        mButtonSwitchLang.setOnClickListener(this::clickOnSwitchLangButton);
        mButtonTrgLang.setOnClickListener(this::clickOnTrgLangButton);
        mButtonRetry.setOnClickListener(this::clickOnRetryButton);
        mButtonFullscreen.setOnClickListener(this::clickOnFullscreenButton);
        mClearEditText.setOnClickListener(this::clickOnClearEditText);
        mButtonGetPhotoOrSrcVoice.setOnClickListener(this::clickOnGetPhotoOrSrcVoiceButton);
        mButtonGetSourceVoice.setOnClickListener(this::clickOnGetSourceVoiceButton);
        mButtonGetTargetVoice.setOnClickListener(this::clickOnGetTargetVoiceButton);
        mButtonSetFavorite.setOnClickListener((ignored) -> clickOnSetFavoriteButton(mButtonSetFavorite));
        mButtonShare.setOnClickListener(this::clickOnShareButton);
    }

    private void findViewsOnFragment(){
        mCustomEditText = mView.findViewById(R.id.edittext);
        mButtonGetPhotoOrSrcVoice = mView.findViewById(R.id.get_audio_spelling);
        mButtonGetSourceVoice = mView.findViewById(R.id.get_source_voice);
        mButtonGetTargetVoice = mView.findViewById(R.id.get_target_voice);
        mButtonSetFavorite = mView.findViewById(R.id.set_favorite);
        mButtonShare = mView.findViewById(R.id.share_translated_word);
        mButtonFullscreen = mView.findViewById(R.id.fullscreen_translated_word);
        mClearEditText = mView.findViewById(R.id.clear_edittext);
        mButtonRetry = mView.findViewById(R.id.button_connection_error_retry);
        mTranslatedResult = mView.findViewById(R.id.textview_translate_result);
        mContainerEdittext = mView.findViewById(R.id.container_edittext);
        mContainerSuccesful = mView.findViewById(R.id.connection_succesful_content);
        mContainerError = mView.findViewById(R.id.connection_error_content);
        mTranslateRecyclerView = mView.findViewById(R.id.container_dict_defin);
        mGeneralContainer = mView.findViewById(R.id.general_container);
        mProgressBar = mView.findViewById(R.id.fragment_translator_progressbar);
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
        final String srcText = mButtonSrcLang.getText().toString(),
                trgText = mButtonTrgLang.getText().toString();
        mButtonSrcLang.setText(trgText);
        mButtonTrgLang.setText(srcText);

        final String srcLangAPI = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG,"");
        final String trgLangAPI = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, trgLangAPI);
        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, srcLangAPI);
        editor.apply();
    }

    private void clickOnTrgLangButton(View view) {
        final Intent intent = new Intent(getActivity(), TargetLangActivity.class);
        startActivityForResult(intent,TRG_LANG_ACTIVITY_REQUEST_CODE);
    }

    private void clickOnRetryButton(View view) {
        requestTranslatorAPI();
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

    private void clickOnGetPhotoOrSrcVoiceButton(View view){
//        UIUtils.showToast(getContext(), "get photo or source audio was clicked");
        UIUtils.showToast(getContext(), getResources().getString(R.string.next_release_message));
    }

    private void clickOnGetSourceVoiceButton(View view){
//        UIUtils.showToast(getContext(), "get source voice was clicked");
        UIUtils.showToast(getContext(), getResources().getString(R.string.next_release_message));
    }


    private void clickOnGetTargetVoiceButton(View view){
//        UIUtils.showToast(getContext(), "get target audio was clicked");
        UIUtils.showToast(getContext(), getResources().getString(R.string.next_release_message));
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
//        UIUtils.showToast(getContext(), "share was clicked");
        UIUtils.showToast(getContext(), getResources().getString(R.string.next_release_message));
    }


    public void initTranslateRecyclerView(){
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTranslateRecyclerView.setLayoutManager(mLayoutManager);

        final String dictDefString = mSettings.getString(TRANSL_CONTENT,"");


        mTranslations = new ArrayList<>();

        if (!dictDefString.isEmpty()) {
            final DictDefinition dictDefinition = JsonUtils.getDictDefinitionFromJson(
                    new JsonParser().parse(dictDefString).getAsJsonObject()
            );
            mCurDictDefinition = dictDefinition;
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
    }


    public void requestTranslatorAPI(){
        try {
            mContainerSuccesful.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            TranslatorAPIUtils.getTranslate(mCustomEditText.getText().toString(),
                    getActivity().getAssets(),
                    mButtonSrcLang.getText().toString().toLowerCase(),
                    mButtonTrgLang.getText().toString().toLowerCase(),
                    mTranslatedResult,
                    mTranslateRecyclerView,
                    mSaver,
                    mHistoryTranslatedItems,
                    mSettings
            );
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            restoreVisibility(savedInstanceState,mContainerError,CONT_ERROR_VISIBILITY);
            restoreVisibility(savedInstanceState,mContainerSuccesful,CONT_SUCCESS_VISIBILITY);
            restoreVisibility(savedInstanceState,mProgressBar,PROGRESS_BAR_VISIBILITY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mTranslatorStateHolder.addOnTranslatorAPIResultObserver(this);
        mRepository.addHistoryContentObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRepository.removeHistoryContentObserver(this);
        mTranslatorStateHolder.removeOnTranslatorAPIResultObserver(this);

        saveToSharedPreferences();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        outState.putString(CONT_SUCCESS_VISIBILITY, String.valueOf(mContainerSuccesful.getVisibility()));
        outState.putString(PROGRESS_BAR_VISIBILITY, String.valueOf(mProgressBar.getVisibility()));
    }

    public void saveToSharedPreferences(){
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(EDITTEXT_DATA,mCustomEditText.getText().toString());
        editor.putString(SRC_LANG, mButtonSrcLang.getText().toString());
        editor.putString(TRG_LANG,mButtonTrgLang.getText().toString());
        editor.putString(TRANSL_RESULT, mTranslatedResult.getText().toString());

        if (mSaver.getCurTranslatedItem()!= null && mSaver.getCurTranslatedItem().getIsFavorite()!=null) {
            editor.putString(IS_FAVORITE, mSaver.getCurTranslatedItem().getIsFavorite());
        } else {
            editor.putString(IS_FAVORITE, String.valueOf(false));
        }
        if (mSaver.getDictDefinition() != null) {
            editor.putString(TRANSL_CONTENT, mSaver.getDictDefinition().getJsonToStringRepr());
        } else if (mCurDictDefinition != null){
            editor.putString(TRANSL_CONTENT, mCurDictDefinition.getJsonToStringRepr());
        } else {
            editor.putString(TRANSL_CONTENT, "");
        }

        editor.apply();
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
                        try {
                            mContainerEdittext.setBackground(
                                    Drawable.createFromXml(
                                            getResources(),
                                            getResources().getLayout(R.layout.edittext_border_active)));
                            mCustomEditText.setCursorVisible(true);
                            mBottomPadding =  UIUtils.hideBottomNavViewGetBottomPadding(
                                    getActivity(),mMainActivityContainer,mNavigation);
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
                            UIUtils.showBottomNavViewSetBottomPadding(getActivity(),
                                    mMainActivityContainer,mNavigation,mBottomPadding);

                            if (!mCustomEditText.getText().toString().isEmpty() &&
                                    mSaver != null &&
                                    mSaver.getCurTranslatedItem() != null &&
                                    !mSaver.getCurTranslatedItem().getSrcMeaning().
                                            equals(mCustomEditText.getText().toString()))
                                requestTranslatorAPI();
                        } catch (XmlPullParserException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void hideKeyboard(){
        final InputMethodManager in = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(mView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showKeyboard(){
        final InputMethodManager in = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void initCustomEditText(){
        mCustomEditText.setText(mSettings.getString(EDITTEXT_DATA, ""));
        if (!mCustomEditText.getText().toString().isEmpty()){
            mClearEditText.setVisibility(View.VISIBLE);
        } else {
            mClearEditText.setVisibility(View.INVISIBLE);
        }
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mCustomEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && mCustomEditText.getText().length() != 0) {
                requestTranslatorAPI();
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
                    mContainerError.setVisibility(View.INVISIBLE);
                if (mCustomEditText.getText().length() != 0 && !mClearEditText.isShown()){
                    mClearEditText.setVisibility(View.VISIBLE);
                    mButtonGetPhotoOrSrcVoice.setImageResource(R.drawable.volume_up_indicator_dark512);
                } else if (mCustomEditText.getText().length() == 0 && mClearEditText.isShown()){
                    mClearEditText.setVisibility(View.INVISIBLE);
                    mButtonGetPhotoOrSrcVoice.setImageResource(R.drawable.camera_dark512);
                    mContainerSuccesful.setVisibility(View.INVISIBLE);
                    clearInfoContainerSuccesful();
                    saveToSharedPreferences();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    public void clearInfoContainerSuccesful(){
        mTranslatedResult.setText("");
        mTranslations.clear();
        mTranslateRecyclerView.getAdapter().notifyDataSetChanged();
        mSaver.setDictDefinition(null);
        mCurDictDefinition = null;
    }


    @Override
    public void onTranslatorAPIResult(final boolean success) {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (success){
            mContainerSuccesful.setVisibility(View.VISIBLE);
            mContainerError.setVisibility(View.INVISIBLE);
        } else {
            mContainerSuccesful.setVisibility(View.INVISIBLE);
            mContainerError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShowSelectedItem() {
        mContainerSuccesful.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(TranslatorPresenter presenter) {
        mPresenter = presenter;
    }


    public class TranslationSaver implements Runnable{
        private DictDefinition mDictDefinition;
        private TranslatedItem mCurTranslatedItem;

        public TranslatedItem getCurTranslatedItem() {
            return mCurTranslatedItem;
        }

        public void setCurTranslatedItem(final TranslatedItem curTranslatedItem) {
            mCurTranslatedItem = curTranslatedItem;
        }

        public DictDefinition getDictDefinition() {
            return mDictDefinition;
        }

        public void setDictDefinition(final DictDefinition dictDefinition) {
            mDictDefinition = dictDefinition;
        }

        @Override
        public void run() {

            mCurTranslatedItem = new TranslatedItem(
                mLanguagesMap.get(mButtonSrcLang.getText().toString().toLowerCase()).getAsString(),
                mLanguagesMap.get(mButtonTrgLang.getText().toString().toLowerCase()).getAsString(),
                mButtonSrcLang.getText().toString(),
                mButtonTrgLang.getText().toString(),
                mCustomEditText.getText().toString(),
                mTranslatedResult.getText().toString(),
                "false",
                mDictDefinition.getJsonToStringRepr()
                );
            if (!mHistoryTranslatedItems.contains(mCurTranslatedItem)) {
                mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
            } else {
                final int index = mHistoryTranslatedItems.indexOf(mCurTranslatedItem);
                mCurTranslatedItem.setIsFavorite(mHistoryTranslatedItems.get(index).getIsFavorite());
                mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
                if (mCurTranslatedItem.isFavorite()){
                    mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, mHistoryTranslatedItems.get(index));
                    mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, mCurTranslatedItem);
                }
            }
    }   }




    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> mHistoryTranslatedItems = mRepository.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch(requestCode){
            case SRC_LANG_ACTIVITY_REQUEST_CODE:
                if (resultCode == AppCompatActivity.RESULT_OK){
                    String result = data.getStringExtra("result");
                    mButtonSrcLang.setText(result);
                }
                break;
            case TRG_LANG_ACTIVITY_REQUEST_CODE:
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