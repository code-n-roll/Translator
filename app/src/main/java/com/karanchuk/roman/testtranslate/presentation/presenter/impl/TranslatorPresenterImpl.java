package com.karanchuk.roman.testtranslate.presentation.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.data.network.DictionaryYandexAPI;
import com.karanchuk.roman.testtranslate.data.network.TranslatorYandexAPI;
import com.karanchuk.roman.testtranslate.data.storage.TextDataStorage;
import com.karanchuk.roman.testtranslate.data.storage.TranslationSaver;
import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;
import com.karanchuk.roman.testtranslate.presentation.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;
import com.karanchuk.roman.testtranslate.presentation.model.TranslationResponse;
import com.karanchuk.roman.testtranslate.presentation.presenter.TranslatorPresenter;
import com.karanchuk.roman.testtranslate.presentation.view.TranslatorView;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.TranslatorRecyclerAdapter;
import com.karanchuk.roman.testtranslate.presentation.view.fragment.TranslatorFragment;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;

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
import static com.karanchuk.roman.testtranslate.data.network.DictionaryYandexAPI.API_BASE_URL_DICTIONARY;
import static com.karanchuk.roman.testtranslate.data.network.TranslatorYandexAPI.API_BASE_URL_TRANSLATOR;
import static com.karanchuk.roman.testtranslate.presentation.Constants.DICTIONARY_API_KEY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.presentation.Constants.RECOGNIZING_REQUEST_PERMISSION_CODE;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SPEECH_KIT_API_KEY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSLATOR_API_KEY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRG_LANG;

/**
 * Created by roman on 16.6.17.
 */

public class TranslatorPresenterImpl implements TranslatorPresenter,
        TranslatorRepository.HistoryTranslatedItemsRepositoryObserver,
        VocalizerListener, RecognizerListener {
    private SharedPreferences mSettings;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private TranslatorFragment mView;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private DictDefinition mCurDictDefinition;
    private TranslationSaver mSaver;

    private CompositeDisposable mCompositeDisposable;
    private TranslatorYandexAPI mTranslatorAPI;
    private DictionaryYandexAPI mDictionaryAPI;
    private String mRequestedText;
    private String mTranslationDirection;
    private Gson mGson;
    private Vocalizer mVocalizer;
    private Recognizer mRecognizer;
    private TextDataStorage mTextDataStorage;

    public TranslatorPresenterImpl(TranslatorView view, TextDataStorage textDataStorage) {
        mView = (TranslatorFragment) view;
        mTextDataStorage = textDataStorage;

        SpeechKit.getInstance().configure(mView.getContext(), SPEECH_KIT_API_KEY);

        mSaver = new TranslationSaver(mView.getContext());
        mGson = new Gson();
        mSettings = mView.getActivity().getSharedPreferences(PREFS_NAME, 0);

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.
                getInstance(mView.getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(
                TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);

        mMainHandler = new Handler(mView.getContext().getMainLooper());


        final String dictDefString = mSettings.getString(TRANSL_CONTENT,"");
        if (!dictDefString.equals("null")){
            mCurDictDefinition = mGson.fromJson(dictDefString, DictDefinition.class);
        }

        initTranslatorYandexAPI();
        initDictionaryYandexAPI();
    }

    @Override
    public void subscribe() {
        mCompositeDisposable = new CompositeDisposable();
        mRepository.addHistoryContentObserver(this);
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable = null;
        mRepository.removeHistoryContentObserver(this);
        saveToSharedPreferences();
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {

    }

    @Override
    public void requestTranslatorAPI() {
        JsonObject langs = JsonUtils.getJsonObjectFromAssetsFile(mView.getContext(), "langs.json");

        String srcLang = mView.mButtonSrcLang.getText().toString().toLowerCase();
        String trgLang = mView.mButtonTrgLang.getText().toString().toLowerCase();

        String srcLangAPI = langs.get(srcLang).getAsString();
        String trgLangAPI = langs.get(trgLang).getAsString();
        mTranslationDirection = srcLangAPI.concat("-").concat(trgLangAPI);

        mRequestedText = mView.mCustomEditText.getText().toString();

        mCompositeDisposable.add(mTranslatorAPI.fetchTranslation(TRANSLATOR_API_KEY,
                mRequestedText, mTranslationDirection)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleTranslatingResponse,this::handleTranslatingError));
    }

    @Override
    public void requestDictionaryAPI() {
        mCompositeDisposable.add(mDictionaryAPI.getDictDefinition(DICTIONARY_API_KEY,
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
                .build().create(TranslatorYandexAPI.class);
    }

    private void initDictionaryYandexAPI(){
        mDictionaryAPI = new Retrofit.Builder()
                .baseUrl(API_BASE_URL_DICTIONARY)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DictionaryYandexAPI.class);
    }

    private void handleDictionaryResponse(DictDefinition dictDefinition){
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

        saveToRepository(dictDefinition);

        mView.hideLoadingDictionary();
        mView.hideRetry();
        mView.showSuccess();
//        TranslatorStateHolder.getInstance().notifyTranslatorAPIResult(true);
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
        mView.hideLoadingDictionary();
        mView.hideRetry();
        mView.hideSuccess();
        if (isOnline()){

        } else {

        }
    }

    private void handleTranslatingResponse(TranslationResponse translation){
        Log.d("myLogs", translation.getText().toString());
        mView.mTranslatedResult.setText(translation.getText().get(0));

        requestDictionaryAPI();
    }

    private void handleTranslatingError(Throwable error){
        error.printStackTrace();
        mView.showRetry();
        mView.hideSuccess();
        mView.hideLoadingDictionary();
        if (isOnline()){

        } else {

        }
    }

    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)
                mView.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void saveToSharedPreferences(){
        HashMap<String, Object> data = new HashMap<>();

        data.put(EDITTEXT_DATA, mView.mCustomEditText.getText().toString());
        data.put(SRC_LANG, mView.mButtonSrcLang.getText().toString());
        data.put(TRG_LANG, mView.mButtonTrgLang.getText().toString());
        data.put(TRANSL_RESULT, mView.mTranslatedResult.getText().toString());
        data.put(TRANSL_CONTENT, mCurDictDefinition);

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
            mVocalizer.setListener(TranslatorPresenterImpl.this);
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
            mVocalizer.setListener(TranslatorPresenterImpl.this);
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
                    TranslatorPresenterImpl.this);
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
                TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
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
        mView.hideLoadingTargetVoice();
        mView.hideLoadingSourceVoice();
        mView.showIconTargetVoice();
        mView.showIconSourceVoice();
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
        mView.desactivateVoiceRecognizer();
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
        mView.mCustomEditText.setText(recognition.getBestResultText());
        mView.stopAnimationMicroWaves();
    }

    @Override
    public void onError(Recognizer recognizer, Error error) {
        Log.d("myLogs", " onError");
        mView.hideLoadingTargetVoice();
        mView.hideLoadingSourceVoice();
        mView.showIconTargetVoice();
        mView.showIconSourceVoice();
        mView.stopAnimationMicroWaves();
    }
}