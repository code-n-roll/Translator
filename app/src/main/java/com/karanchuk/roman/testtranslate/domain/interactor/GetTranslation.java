package com.karanchuk.roman.testtranslate.domain.interactor;

import com.karanchuk.roman.testtranslate.domain.executor.PostExecutionThread;
import com.karanchuk.roman.testtranslate.domain.executor.ThreadExecutor;
import com.karanchuk.roman.testtranslate.domain.repository.TranslatorRepository;
import com.karanchuk.roman.testtranslate.presentation.model.TranslationResponse;

import io.reactivex.Observable;

/**
 * Created by roman on 28.6.17.
 */

public class GetTranslation extends UseCase<TranslationResponse, String, String, String> {
    private final TranslatorRepository mTranslatorRepository;

    public GetTranslation(TranslatorRepository translatorRepository,
                          ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.mTranslatorRepository = translatorRepository;
    }

    @Override
    Observable<TranslationResponse> buildUseCaseObservable(String key, String lang, String text) {
        return mTranslatorRepository.translation(key, lang, text);
    }
}
