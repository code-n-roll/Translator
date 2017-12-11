package com.karanchuk.roman.testtranslate.translator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.common.database.TablePersistenceContract;
import com.karanchuk.roman.testtranslate.common.database.repository.TranslatorLocalRepository;
import com.karanchuk.roman.testtranslate.common.database.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.common.database.repository.TranslatorRepositoryImpl;
import com.karanchuk.roman.testtranslate.common.model.DictDefinition;
import com.karanchuk.roman.testtranslate.common.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.common.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.common.model.Translation;
import com.karanchuk.roman.testtranslate.common.model.TranslationResponse;
import com.karanchuk.roman.testtranslate.common.storage.TextDataStorage;
import com.karanchuk.roman.testtranslate.common.storage.TextDataStorageImpl;
import com.karanchuk.roman.testtranslate.common.storage.TranslationSaver;
import com.karanchuk.roman.testtranslate.common.yandexapi.DictionaryService;
import com.karanchuk.roman.testtranslate.common.yandexapi.TranslatorService;
import com.karanchuk.roman.testtranslate.fullscreen.FullscreenActivity;
import com.karanchuk.roman.testtranslate.sourcelang.SourceLangActivity;
import com.karanchuk.roman.testtranslate.targetlang.TargetLangActivity;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.karanchuk.roman.testtranslate.common.Constants.CUR_SELECTED_ITEM_SRC_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.CUR_SELECTED_ITEM_TRG_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.CUR_TRANSLATED_ITEM;
import static com.karanchuk.roman.testtranslate.common.Constants.DICTIONARY_API_KEY;
import static com.karanchuk.roman.testtranslate.common.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.common.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.common.Constants.RECOGNIZING_REQUEST_PERMISSION_CODE;
import static com.karanchuk.roman.testtranslate.common.Constants.SPEECH_KIT_API_KEY;
import static com.karanchuk.roman.testtranslate.common.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSLATED_RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSLATOR_API_KEY;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRG_LANG;
import static com.karanchuk.roman.testtranslate.common.yandexapi.DictionaryService.API_BASE_URL_DICTIONARY;
import static com.karanchuk.roman.testtranslate.common.yandexapi.TranslatorService.API_BASE_URL_TRANSLATOR;
import static com.karanchuk.roman.testtranslate.translator.TranslatorFragment.SRC_LANG_ACTIVITY_REQUEST_CODE;
import static com.karanchuk.roman.testtranslate.translator.TranslatorFragment.TRG_LANG_ACTIVITY_REQUEST_CODE;

/**
 * Created by roman on 16.6.17.
 */

public class TranslatorPresenter implements TranslatorContract.Presenter,
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        VocalizerListener, RecognizerListener {
    private SharedPreferences mSettings;
    private TranslatorRepositoryImpl mRepository;
    private Handler mMainHandler;
    private TranslatorFragment mView;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private DictDefinition mCurDictDefinition;
    private TranslationSaver mSaver;

    private CompositeDisposable mCompositeDisposable;
    private TranslatorService mTranslatorAPI;
    private DictionaryService mDictionaryAPI;
    private String mRequestedText;
    private String mTranslationDirection;
    private Gson mGson;
    private Vocalizer mVocalizer;
    private Recognizer mRecognizer;
    private TextDataStorage mTextDataStorage;

    private Context mContext;

    public TranslatorPresenter(TranslatorContract.View view, Context context) {
        mView = (TranslatorFragment) view;
        SpeechKit.getInstance().configure(mView.getContext(), SPEECH_KIT_API_KEY);

        mTextDataStorage = new TextDataStorageImpl(context);
        mSaver = new TranslationSaver(mView.getContext());
        mGson = new Gson();
        mMainHandler = new Handler(mView.getContext().getMainLooper());

        TranslatorRepository localDataSource =
                TranslatorLocalRepository.getInstance(mView.getContext());
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(
                TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);


        mSettings = mView.getActivity().getSharedPreferences(PREFS_NAME, 0);
        String dictDefString = mSettings.getString(TRANSL_CONTENT,"");
        if (!dictDefString.equals("null")){
            mCurDictDefinition = mGson.fromJson(dictDefString, DictDefinition.class);
        }

        initTranslatorYandexAPI();
        initDictionaryYandexAPI();
    }

    @Override
    public void attachView(Context context) {
        mCompositeDisposable = new CompositeDisposable();
        mRepository.addHistoryContentObserver(this);
        mContext = context;
    }

    @Override
    public void detachView() {
        resetRecognizer();
        saveToSharedPreferences();

        mCompositeDisposable = null;
        mRepository.removeHistoryContentObserver(this);
        mRepository = null;

        mGson = null;
        mSaver = null;
        mMainHandler = null;
        mTextDataStorage = null;
        mHistoryTranslatedItems = null;
        mSettings = null;
        mCurDictDefinition = null;
    }

    public Recognizer getRecognizer() {
        return mRecognizer;
    }

    @Override
    public boolean requestTranslatorAPI() {
        JsonObject langs = JsonUtils.getJsonObjectFromAssetsFile(mView.getContext(), "langs.json");

        String srcLang = mView.mButtonSrcLang.getText().toString().toLowerCase();
        String trgLang = mView.mButtonTrgLang.getText().toString().toLowerCase();

        String srcLangAPI = langs.get(srcLang).getAsString();
        String trgLangAPI = langs.get(trgLang).getAsString();
        mTranslationDirection = srcLangAPI.concat("-").concat(trgLangAPI);

        mRequestedText = mView.mCustomEditText.getText().toString();

        if ((mSaver.getCurTranslatedItem() != null &&
                !mSaver.getCurTranslatedItem().getSrcMeaning().equals(mRequestedText)) ||
                mSaver.getCurTranslatedItem() == null) {
            mCompositeDisposable.add(mTranslatorAPI.fetchTranslation(TRANSLATOR_API_KEY,
                    mRequestedText, mTranslationDirection)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleTranslatingResponse, this::handleTranslatingError));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void requestDictionaryAPI() {
        mCompositeDisposable.add(mDictionaryAPI.fetchDictDefinition(DICTIONARY_API_KEY,
                mRequestedText, mTranslationDirection)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleDictionaryResponse, this::handleDictionaryError));
    }

    private void initTranslatorYandexAPI(){
        mTranslatorAPI = new Retrofit.Builder()
                .baseUrl(API_BASE_URL_TRANSLATOR)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(TranslatorService.class);
    }

    private void initDictionaryYandexAPI(){
        mDictionaryAPI = new Retrofit.Builder()
                .baseUrl(API_BASE_URL_DICTIONARY)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DictionaryService.class);
    }

    @Override
    public void handleDictionaryResponse(DictDefinition dictDefinition) {
        Log.d("myLogs", dictDefinition.toString());

        List<Translation> translations = new ArrayList<>();
        int index;
        for (PartOfSpeech POS : dictDefinition.getPartsOfSpeech()){
            translations.addAll(POS.getTranslations());
            index = 1;
            for (Translation translation : POS.getTranslations()){
                translation.setNumber(String.valueOf(index++));
            }
        }

        TranslatorRecyclerAdapter adapter = (TranslatorRecyclerAdapter)
                mView.mTranslateRecyclerView.getAdapter();
        adapter.updateData(translations, dictDefinition.getPartsOfSpeech());

        String curEditTextContent = mView.mCustomEditText.getText().toString().trim();
        String srcLangAPI = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG,"");
        String trgLangAPI = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");

        TranslatedItem maybeExistedItem =
                new TranslatedItem(srcLangAPI, trgLangAPI, null, null,
                        curEditTextContent, null, null, null);
        if (!mHistoryTranslatedItems.contains(maybeExistedItem)) {
            saveToRepository(dictDefinition);
        }

        if (mView != null) {
            mView.hideLoadingDictionary();
            mView.hideRetry();
            mView.showSuccess();
        }
    }

    private void saveToRepository(DictDefinition dictDefinition){
        mCurDictDefinition = dictDefinition;
        Map<String, Object> savedData = new HashMap<>();
        savedData.put(SRC_LANG, mView.mButtonSrcLang.getText().toString());
        savedData.put(TRG_LANG, mView.mButtonTrgLang.getText().toString());
        savedData.put(EDITTEXT_DATA, mView.mCustomEditText.getText().toString());
        savedData.put(TRANSL_RESULT, mView.mTranslatedResult.getText().toString());
        savedData.put(TRANSL_CONTENT, dictDefinition);
        mSaver.setSavedData(savedData);
        new Thread(mSaver).start();
    }

    private void handleDictionaryError(Throwable error){
        error.printStackTrace();
        if (mView != null) {
            mView.hideLoadingDictionary();
            mView.hideRetry();
            mView.hideSuccess();
        }
        if (isOnline()){

        } else {

        }
    }

    private void handleTranslatingResponse(TranslationResponse translation){
        Log.d("myLogs", translation.getText().toString());
        if (mView != null) {
            mView.mTranslatedResult.setText(translation.getText().get(0));
        }
        requestDictionaryAPI();
    }

    private void handleTranslatingError(Throwable error){
        error.printStackTrace();
        if (mView != null) {
            mView.showRetry();
            mView.hideSuccess();
            mView.hideLoadingDictionary();
        }
        if (isOnline()){

        } else {

        }
    }

    private boolean isOnline(){
        NetworkInfo networkInfo = null;
        if (mView != null && mView.getActivity() != null) {
            ConnectivityManager cm = (ConnectivityManager)
                    mView.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void saveToSharedPreferences(){
        HashMap<String, Object> data = new HashMap<>();

        data.put(EDITTEXT_DATA, mView.mCustomEditText.getText().toString());
        data.put(SRC_LANG, mView.mButtonSrcLang.getText().toString());
        data.put(TRG_LANG, mView.mButtonTrgLang.getText().toString());
        data.put(TRANSL_RESULT, mView.mTranslatedResult.getText().toString());
        data.put(TRANSL_CONTENT, mCurDictDefinition);
        if (mSaver.getCurTranslatedItem() != null) {
            data.put(CUR_TRANSLATED_ITEM, mSaver.getCurTranslatedItem().toString());
        }
        mTextDataStorage.saveToSharedPreferences(data);
    }

    @Override
    public void clearContainerSuccess() {
        mView.mTranslateRecyclerView.getAdapter().notifyDataSetChanged();
        mCurDictDefinition = null;
    }

    @Override
    public void vocalizeSourceText() {
        String text = mView.mCustomEditText.getText().toString();
        if (text.isEmpty()){

        } else {
            resetVocalizer();
            mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true,
                    Vocalizer.Voice.OMAZH);
            mVocalizer.setListener(TranslatorPresenter.this);
            mVocalizer.start();
        }
    }

    @Override
    public void vocalizeTargetText() {
        String text = mView.mTranslatedResult.getText().toString();
        if (text.isEmpty()){

        } else {
            resetVocalizer();
            mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true,
                    Vocalizer.Voice.OMAZH);
            mVocalizer.setListener(TranslatorPresenter.this);
            mVocalizer.start();
        }
    }

    @Override
    public void recognizeSourceText() {
        createAndStartRecognizer();
        mView.activateVoiceRecognizer();
    }

    private void createAndStartRecognizer() {
        final Context context = mView.getContext();
        if (context == null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(context, RECORD_AUDIO) != PERMISSION_GRANTED) {
            mView.requestPermissions(new String[]{RECORD_AUDIO}, RECOGNIZING_REQUEST_PERMISSION_CODE);
        } else {
            // Reset the current recognizer.
            resetRecognizer();
            // To create a new recognizer, specify the language,
            // the model - a scope of recognition to get the most appropriate results,
            // set the listener to handle the recognition events.
            mRecognizer = Recognizer.create(Recognizer.Language.RUSSIAN, Recognizer.Model.NOTES,
                    TranslatorPresenter.this);
            // Don't forget to call start on the created object.
            mRecognizer.start();
        }
    }

    private void resetVocalizer(){
        if (mVocalizer != null){
            mVocalizer.cancel();
            mVocalizer = null;
        }
    }

    @Override
    public void resetRecognizer() {
        if (mRecognizer != null) {
            mRecognizer.cancel();
            mRecognizer = null;
        }
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> mHistoryTranslatedItems = mRepository.getTranslatedItems(
                TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
    }

    @Override
    public void onSynthesisBegin(Vocalizer vocalizer) {
        Log.d("myLogs", " onSynthesisBegin");
    }

    @Override
    public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {
        Log.d("myLogs", " onSynthesisDone");
    }

    @Override
    public void onPlayingBegin(Vocalizer vocalizer) {
        Log.d("myLogs", " onPlayingBegin");
    }

    @Override
    public void onPlayingDone(Vocalizer vocalizer) {
        Log.d("myLogs", " onPlayingDone");
        if (mView != null) {
            mView.hideLoadingTargetVoice();
            mView.hideLoadingSourceVoice();
            mView.showIconTargetVoice();
            mView.showIconSourceVoice();
        }
    }

    @Override
    public void onVocalizerError(Vocalizer vocalizer, Error error) {
        resetVocalizer();
        Log.d("myLogs", error.getString());
        Log.d("myLogs", " onVocalizerError");
    }

    @Override
    public void onRecordingBegin(Recognizer recognizer) {
        Log.d("myLogs", " onRecordingBegin");
    }

    @Override
    public void onSpeechDetected(Recognizer recognizer) {
        Log.d("myLogs", " onSpeechDetected");
    }

    @Override
    public void onSpeechEnds(Recognizer recognizer) {
        Log.d("myLogs", " onSpeechEnds");
    }

    @Override
    public void onRecordingDone(Recognizer recognizer) {
        Log.d("myLogs", " onRecordingDone");
        if (mView != null && mView.isAdded()) {
            mView.desactivateVoiceRecognizer();
        }
    }

    @Override
    public void onSoundDataRecorded(Recognizer recognizer, byte[] bytes) {
        Log.d("myLogs", " onSoundDataRecorded");
    }

    @Override
    public void onPowerUpdated(Recognizer recognizer, float v) {
        Log.d("myLogs", " onPowerUpdated");
    }

    @Override
    public void onPartialResults(Recognizer recognizer, Recognition recognition, boolean b) {
        Log.d("myLogs", " onPartialResults");
    }

    @Override
    public void onRecognitionDone(Recognizer recognizer, Recognition recognition) {
        if (mView != null) {
            mView.mCustomEditText.setText(recognition.getBestResultText());
            mView.stopAnimationMicroWaves();
        }
    }

    @Override
    public void onError(Recognizer recognizer, Error error) {
        Log.d("myLogs", " onError");
        if (mView != null) {
            mView.hideLoadingTargetVoice();
            mView.hideLoadingSourceVoice();
            mView.showIconTargetVoice();
            mView.showIconSourceVoice();
            mView.stopAnimationMicroWaves();
            mView.setHintOnInput();
            mView.showActiveInput();
            mView.setRecognizingSourceText(false);
            UIUtils.showToast(mView.getContext(),
                    mView.getContext().getResources().getString(R.string.connection_error_content));
        }
    }

    @Override
    public boolean clickOnGeneralContainer(View view, MotionEvent event) {
        mView.hideKeyboard();
//        UIUtils.showToast(getContext(), "clicked outside keyboard, keyboard hided");
        return true;
    }

    @Override
    public void clickOnSrcLangButton(View view) {
        final Intent intent = new Intent(mContext, SourceLangActivity.class);
        mView.startActivityForResult(intent, SRC_LANG_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void clickOnSwitchLangButton(View view) {
        String oldSrcLang = mView.getTextButtonSrcLang();
        String oldTrgLang = mView.getTextButtonTrgLang();
        mView.setTextButtonSrcLang(oldTrgLang);
        mView.setTextButtonTrgLang(oldSrcLang);

        String srcLangAPI = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG,"");
        String trgLangAPI = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, trgLangAPI);
        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, srcLangAPI);
        editor.apply();

        mView.setTextCustomEditText(mView.getTextTranslatedResultView());
        if (!mView.isEmptyCustomEditText() && requestTranslatorAPI()) {
            mView.showLoadingDictionary();
            mView.hideSuccess();
        } else {
            mView.getTranslatedItemFromCache(mView.createPredictedTranslatedItem());
        }
    }

    @Override
    public void clickOnTrgLangButton(View view) {
        final Intent intent = new Intent(mContext, TargetLangActivity.class);
        mView.startActivityForResult(intent, TRG_LANG_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void clickOnRetryButton(View view) {
        mView.showLoadingDictionary();
        mView.hideSuccess();
        requestTranslatorAPI();
    }

    @Override
    public void clickOnFullscreenButton(View view) {
        final Intent intent = new Intent(mContext, FullscreenActivity.class);
        intent.putExtra(TRANSLATED_RESULT, mView.getTextTranslatedResultView());
        mContext.startActivity(intent);
    }

    @Override
    public void clickOnClearEditText(View view){
        mView.clearCustomEditText();
    }

    @Override
    public void clickOnRecognizePhotoOrVocalizeSourceText(View view){
        if (!mView.isEmptyCustomEditText()) {
            mView.showLoadingSourceVoice();
            mView.hideIconSourceVoice();
            vocalizeSourceText();
        } else {
            UIUtils.showToast(mContext, mContext.getResources()
                                                .getString(R.string.try_to_get_photo));
        }
    }

    @Override
    public void clickOnRecognizeSourceText(View view){
        if (!mView.isRecordAudioGranted()){
            mView.requestRecordAudioPermissions();
        }
        if (!mView.isRecognizingSourceText() && mView.isRecordAudioGranted()) {
            mView.setRecognizingSourceText(true);
            mView.showAnimationMicroWaves();
            recognizeSourceText();
        } else {
            mView.setRecognizingSourceText(false);
            resetRecognizer();
        }
    }

    @Override
    public void clickOnSetFavoriteButton(final ImageButton view) {
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
        UIUtils.showToast(mContext, mContext.getResources().getString(R.string.set_favorite_message));
    }


    @Override
    public void clickOnShareButton(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, mView.getTextTranslatedResultView());
        mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.chooser_title)));
    }

    @Override
    public void clickOnSynonymItem(View view, String text){
        if (!text.isEmpty()) {
            mView.clickOnButtonSwitchLang();
            mView.setTextCustomEditText(text);
            mView.showLoadingDictionary();
            mView.hideSuccess();
            requestTranslatorAPI();
        }
    }

    @Override
    public void clickOnVocalizeTargetText(View view) {
        if (!mView.isEmptyTranslatedResultView()) {
            mView.showLoadingTargetVoice();
            mView.hideIconTargetVoice();
            vocalizeTargetText();
        } else {
            UIUtils.showToast(mContext, mContext.getResources()
                                                .getString(R.string.try_vocalize_empty_result));
        }
    }
}