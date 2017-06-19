package com.karanchuk.roman.testtranslate.presentation.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.karanchuk.roman.testtranslate.data.TranslatorDataSource;
import com.karanchuk.roman.testtranslate.data.TranslatorRepository;
import com.karanchuk.roman.testtranslate.data.local.TablesPersistenceContract;
import com.karanchuk.roman.testtranslate.data.local.TranslatorLocalDataSource;
import com.karanchuk.roman.testtranslate.data.net.DictionaryYandexAPI;
import com.karanchuk.roman.testtranslate.data.net.TranslatorYandexAPI;
import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;
import com.karanchuk.roman.testtranslate.presentation.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;
import com.karanchuk.roman.testtranslate.presentation.model.TranslationResponse;
import com.karanchuk.roman.testtranslate.presentation.presenter.TranslatorPresenter;
import com.karanchuk.roman.testtranslate.presentation.view.adapter.TranslatorRecyclerAdapter;
import com.karanchuk.roman.testtranslate.presentation.view.fragment.TranslatorFragment;
import com.karanchuk.roman.testtranslate.presentation.view.state_holder.TranslatorStateHolder;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.karanchuk.roman.testtranslate.data.net.DictionaryYandexAPI.API_BASE_URL_DICTIONARY;
import static com.karanchuk.roman.testtranslate.data.net.TranslatorYandexAPI.API_BASE_URL_TRANSLATOR;
import static com.karanchuk.roman.testtranslate.presentation.Constants.DICTIONARY_API_KEY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.EDITTEXT_DATA;
import static com.karanchuk.roman.testtranslate.presentation.Constants.IS_FAVORITE;
import static com.karanchuk.roman.testtranslate.presentation.Constants.PREFS_NAME;
import static com.karanchuk.roman.testtranslate.presentation.Constants.SRC_LANG;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSLATOR_API_KEY;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_CONTENT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRANSL_RESULT;
import static com.karanchuk.roman.testtranslate.presentation.Constants.TRG_LANG;

/**
 * Created by roman on 16.6.17.
 */

public class TranslatorPresenterImpl implements TranslatorPresenter,
    TranslatorRepository.HistoryTranslatedItemsRepositoryObserver
{
    private TranslationSaver mSaver;
    private SharedPreferences mSettings;
    private TranslatorRepository mRepository;
    private Handler mMainHandler;
    private TranslatorFragment mView;
    private List<TranslatedItem> mHistoryTranslatedItems;
    private DictDefinition mCurDictDefinition;
    private JsonObject mLanguagesMap;

    private CompositeDisposable mCompositeDisposable;
    private TranslatorYandexAPI mTranslatorAPI;
    private DictionaryYandexAPI mDictionaryAPI;
    private String mRequestedText;
    private String mTranslationDirection;

    public TranslatorPresenterImpl(TranslatorFragment view) {
        mView = view;


        mSaver = new TranslationSaver();

        mSettings = view.getActivity().getSharedPreferences(PREFS_NAME, 0);

        TranslatorDataSource localDataSource = TranslatorLocalDataSource.
                getInstance(view.getContext());
        mRepository = TranslatorRepository.getInstance(localDataSource);
        mHistoryTranslatedItems = mRepository.getTranslatedItems(
                TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY);

        mMainHandler = new Handler(view.getContext().getMainLooper());

        mLanguagesMap = JsonUtils.getJsonObjectFromFile(
                view.getActivity().getAssets(), "langs.json");

        final String dictDefString = mSettings.getString(TRANSL_CONTENT,"");
        if (!dictDefString.isEmpty()){
            mCurDictDefinition = JsonUtils.getDictDefinitionFromJson(
                    new JsonParser().parse(dictDefString).getAsJsonObject());
        }

        initTranslatorYandexAPI();
        initDictionaryYandexAPI();
    }

    public TranslationSaver getSaver() {
        return mSaver;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void onStart() {
        mCompositeDisposable = new CompositeDisposable();
        mRepository.addHistoryContentObserver(this);
    }

    @Override
    public void onStop() {
        mCompositeDisposable = null;
        mRepository.removeHistoryContentObserver(this);
        saveToSharedPreferences();
    }

    @Override
    public void requestTranslatorAPI() {
//        try {
//            TranslatorAPIUtils.getTranslate(mView.mCustomEditText.getTextTextView().toString(),
//                    mView.getContext().getAssets(),
//                    mView.mButtonSrcLang.getTextTextView().toString().toLowerCase(),
//                    mView.mButtonTrgLang.getTextTextView().toString().toLowerCase(),
//                    mView.mTranslatedResult,
//                    mView.mTranslateRecyclerView,
//                    mSaver,
//                    mHistoryTranslatedItems,
//                    mSettings
//            );
//
//        } catch (IOException e){
//            e.printStackTrace();
//        }

        JsonObject langs = JsonUtils.getJsonObjectFromFile(mView.getContext().getAssets(), "langs.json");

        String srcLang = mView.mButtonSrcLang.getText().toString().toLowerCase();
        String trgLang = mView.mButtonTrgLang.getText().toString().toLowerCase();

        String srcLangAPI = langs.get(srcLang).getAsString();
        String trgLangAPI = langs.get(trgLang).getAsString();
        mTranslationDirection = srcLangAPI.concat("-").concat(trgLangAPI);

        mRequestedText = mView.mCustomEditText.getText().toString();

        mCompositeDisposable.add(mTranslatorAPI.getTranslation(TRANSLATOR_API_KEY,
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

        mSaver.setDictDefinition(dictDefinition);
        new Thread(mSaver).start();

        TranslatorStateHolder.getInstance().notifyTranslatorAPIResult(true);
    }

    private void handleDictionaryError(Throwable error){
        error.printStackTrace();
        mView.showRetry();
        mView.hideLoading();
        if (isOnline()){

        } else {

        }
    }

    private void handleTranslatingResponse(TranslationResponse translation){
        Log.d("myLogs", translation.getText().toString());
        mView.mTranslatedResult.setText(translation.getText().get(0));
        mView.showSuccess();

        requestDictionaryAPI();
    }

    private void handleTranslatingError(Throwable error){
        error.printStackTrace();
        mView.showRetry();
        mView.hideLoading();
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
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(EDITTEXT_DATA, mView.mCustomEditText.getText().toString());
        editor.putString(SRC_LANG, mView.mButtonSrcLang.getText().toString());
        editor.putString(TRG_LANG, mView.mButtonTrgLang.getText().toString());
        editor.putString(TRANSL_RESULT, mView.mTranslatedResult.getText().toString());

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

    @Override
    public void clearContainerSuccess() {
        mView.mTranslateRecyclerView.getAdapter().notifyDataSetChanged();
        mSaver.setDictDefinition(null);
        mCurDictDefinition = null;
    }

    @Override
    public void onHistoryTranslatedItemsChanged() {
        mMainHandler.post(() -> mHistoryTranslatedItems = mRepository.getTranslatedItems(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY));
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
                    mLanguagesMap.get(mView.mButtonSrcLang.getText().toString().toLowerCase()).getAsString(),
                    mLanguagesMap.get(mView.mButtonTrgLang.getText().toString().toLowerCase()).getAsString(),
                    mView.mButtonSrcLang.getText().toString(),
                    mView.mButtonTrgLang.getText().toString(),
                    mView.mCustomEditText.getText().toString(),
                    mView.mTranslatedResult.getText().toString(),
                    "false",
                    mDictDefinition.getJsonToStringRepr()
            );
            if (!mHistoryTranslatedItems.contains(mCurTranslatedItem)) {
                mRepository.saveTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
            } else {
                final int index = mHistoryTranslatedItems.indexOf(mCurTranslatedItem);
                mCurTranslatedItem.setIsFavorite(mHistoryTranslatedItems.get(index).getIsFavorite());
                mRepository.saveTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_HISTORY, mCurTranslatedItem);
                if (mCurTranslatedItem.isFavorite()){
                    mRepository.deleteTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES, mHistoryTranslatedItems.get(index));
                    mRepository.saveTranslatedItem(TablesPersistenceContract.TranslatedItemEntry.TABLE_NAME_FAVORITES, mCurTranslatedItem);
                }
            }
        }
    }
}
