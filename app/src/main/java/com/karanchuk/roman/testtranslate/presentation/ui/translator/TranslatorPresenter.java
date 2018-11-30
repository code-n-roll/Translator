package com.karanchuk.roman.testtranslate.presentation.ui.translator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract;
import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition;
import com.karanchuk.roman.testtranslate.data.database.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.data.database.model.Translation;
import com.karanchuk.roman.testtranslate.data.database.model.TranslationResponse;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl;
import com.karanchuk.roman.testtranslate.data.database.storage.TextDataStorage;
import com.karanchuk.roman.testtranslate.data.database.storage.TextDataStorageImpl;
import com.karanchuk.roman.testtranslate.data.database.storage.TranslationSaver;
import com.karanchuk.roman.testtranslate.presentation.TestTranslatorApp;
import com.karanchuk.roman.testtranslate.repository.YandexDictionaryRepository;
import com.karanchuk.roman.testtranslate.repository.YandexTranslateRepository;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;
import com.karanchuk.roman.testtranslate.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
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
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSLATOR_API_KEY;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.common.Constants.TRG_LANG;

/**
 * Created by roman on 16.6.17.
 */

public class TranslatorPresenter implements TranslatorContract.Presenter,
        TranslatorRepositoryImpl.HistoryTranslatedItemsRepositoryObserver,
        VocalizerListener,
        RecognizerListener {

    private static final String MAIN_HANDLER_THREAD = TranslatorPresenter.class.getName() + ".MAIN_HANDLER_THREAD";

    private SharedPreferences mSettings;
    private TranslatorRepositoryImpl mRepository;
    private Handler mMainHandler;
    private HandlerThread mMainHandlerThread;
    private TranslatorFragment mView;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private DictDefinition mCurDictDefinition;
    private TranslationSaver mSaver;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private String mRequestedText;
    private String mTranslationDirection;

    private Vocalizer mVocalizer;
    private Recognizer mRecognizer;
    private TextDataStorage mTextDataStorage;

    @Inject
    Gson mGson;

    @Inject
    YandexTranslateRepository mYandexTranslateRepository;

    @Inject
    YandexDictionaryRepository mYandexDictionaryRepository;

    public TranslatorPresenter(TranslatorContract.View view) {
        TestTranslatorApp.appComponent.inject(this);

        mView = (TranslatorFragment) view;
        SpeechKit.getInstance().configure(mView.getContext(), SPEECH_KIT_API_KEY);

        mTextDataStorage = new TextDataStorageImpl(mView.getContext(), mGson);
        mSaver = new TranslationSaver(mView.getContext(), mGson);

        TranslatorRepository localDataSource = TranslatorLocalRepository.getInstance(mView.getContext());
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource);
    }

    @Override
    public void attachView(Context context) {
        mRepository.addHistoryContentObserver(this);

        mMainHandlerThread = new HandlerThread(MAIN_HANDLER_THREAD);
        mMainHandlerThread.start();
        mMainHandler = new Handler(mMainHandlerThread.getLooper());
        mMainHandler.post(() -> {
            mHistoryTranslatedItems = mRepository.getTranslatedItems(
                    TablePersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);

            mSettings = mView.getActivity().getSharedPreferences(PREFS_NAME, 0);
            String dictDefString = mSettings.getString(TRANSL_CONTENT,"");
            if (!dictDefString.equals("null")){
                mCurDictDefinition = mGson.fromJson(dictDefString, DictDefinition.class);
            }
        });
    }

    @Override
    public void detachView() {
        resetRecognizer();
        saveToSharedPreferences();

        mCompositeDisposable = null;
        mRepository.removeHistoryContentObserver(this);
        mRepository = null;

        mSaver = null;
        mTextDataStorage = null;
        mHistoryTranslatedItems = null;
        mSettings = null;
        mCurDictDefinition = null;

        mMainHandler = null;
        mMainHandlerThread.quit();
        mMainHandlerThread = null;
    }

    @Override
    public boolean requestTranslatorAPI() {
        Single<JsonObject> getTranslDirectFromJson = Single.create(emitter -> {
            JsonObject langs = JsonUtils.getJsonObjectFromAssetsFile(mView.getContext(), mGson,"langs.json");

            emitter.onSuccess(langs);
        });

        String srcLang = mView.getMButtonSrcLang().getText().toString().toLowerCase();
        String trgLang = mView.getMButtonTrgLang().getText().toString().toLowerCase();
        mRequestedText = mView.getMCustomEditText().getText().toString();

        if ((mSaver.getCurTranslatedItem() != null &&
                !mSaver.getCurTranslatedItem().getSrcMeaning().equals(mRequestedText)) ||
                mSaver.getCurTranslatedItem() == null) {
            mCompositeDisposable.add(
                    getTranslDirectFromJson
                            .flatMap(langs -> {
                                String srcLangAPI = langs.get(srcLang).getAsString();
                                String trgLangAPI = langs.get(trgLang).getAsString();
                                mTranslationDirection = srcLangAPI.concat("-").concat(trgLangAPI);

                                return mYandexTranslateRepository.getTranslation(
                                        TRANSLATOR_API_KEY,
                                        mRequestedText,
                                        mTranslationDirection);
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::handleTranslatingResponse, this::handleTranslatingError));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void requestDictionaryAPI() {
        mCompositeDisposable.add(mYandexDictionaryRepository.getValueFromDictionary(
                DICTIONARY_API_KEY,
                mRequestedText,
                mTranslationDirection)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleDictionaryResponse, this::handleDictionaryError));
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

        TranslatorRecyclerAdapter adapter = (TranslatorRecyclerAdapter) mView.getMTranslateRecyclerView().getAdapter();
        adapter.updateData(translations, dictDefinition.getPartsOfSpeech());

        String curEditTextContent = mView.getMCustomEditText().getText().toString().trim();
        String srcLangAPI = mSettings.getString(CUR_SELECTED_ITEM_SRC_LANG,"");
        String trgLangAPI = mSettings.getString(CUR_SELECTED_ITEM_TRG_LANG,"");

        TranslatedItem maybeExistedItem = new TranslatedItem(srcLangAPI,
                trgLangAPI,
                null,
                null,
                        curEditTextContent,
                null,
                null,
                null);
        if (!mHistoryTranslatedItems.contains(maybeExistedItem)) {
            saveToRepository(dictDefinition);
        }

        if (mView != null) {
            mView.hideLoadingDictionary();
            mView.hideRetry();
            mView.showSuccess();
        }
    }

    public List<TranslatedItem> getHistoryTranslatedItems() {
        return mHistoryTranslatedItems;
    }

    private void saveToRepository(DictDefinition dictDefinition){
        mCurDictDefinition = dictDefinition;
        Map<String, Object> savedData = new HashMap<>();
        savedData.put(SRC_LANG, mView.getMButtonSrcLang().getText().toString());
        savedData.put(TRG_LANG, mView.getMButtonTrgLang().getText().toString());
        savedData.put(EDITTEXT_DATA, mView.getMCustomEditText().getText().toString());
        savedData.put(TRANSL_RESULT, mView.getMTranslatedResult().getText().toString());
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
            mView.getMTranslatedResult().setText(translation.getText().get(0));
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

        data.put(EDITTEXT_DATA, mView.getMCustomEditText().getText().toString());
        data.put(SRC_LANG, mView.getMButtonSrcLang().getText().toString());
        data.put(TRG_LANG, mView.getMButtonTrgLang().getText().toString());
        data.put(TRANSL_RESULT, mView.getMTranslatedResult().getText().toString());
        data.put(TRANSL_CONTENT, mCurDictDefinition);
        if (mSaver.getCurTranslatedItem() != null) {
            data.put(CUR_TRANSLATED_ITEM, mSaver.getCurTranslatedItem().toString());
        }
        mTextDataStorage.saveToSharedPreferences(data);
    }

    @Override
    public void clearContainerSuccess() {
        mView.getMTranslateRecyclerView().getAdapter().notifyDataSetChanged();
        mCurDictDefinition = null;
    }

    @Override
    public void vocalizeSourceText() {
        String text = mView.getMCustomEditText().getText().toString();
        if (!text.isEmpty()){
            resetVocalizer();
            mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.ENGLISH, text, true, Vocalizer.Voice.OMAZH);
            mVocalizer.setListener(TranslatorPresenter.this);
            mVocalizer.start();
        }
    }

    @Override
    public void vocalizeTargetText() {
        String text = mView.getMTranslatedResult().getText().toString();
        if (!text.isEmpty()){
            resetVocalizer();
            mVocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, text, true, Vocalizer.Voice.OMAZH);
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
            mView.deactivateVoiceRecognizer();
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
            mView.getMCustomEditText().setText(recognition.getBestResultText());
            mView.stopAnimationMicroWaves();
        }
    }

    @Override
    public void onError(Recognizer recognizer, Error error) {
        Log.d("myLogs", " onError");
        if (mView != null) {
            mView.showError();
            UIUtils.showToast(mView.getContext(),
                    mView.getContext().getResources().getString(R.string.connection_error_content));
        }
    }
}