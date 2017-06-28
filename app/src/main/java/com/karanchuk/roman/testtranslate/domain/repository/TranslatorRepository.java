package com.karanchuk.roman.testtranslate.domain.repository;

import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;
import com.karanchuk.roman.testtranslate.presentation.model.TranslationResponse;

import io.reactivex.Observable;

/**
 * Created by roman on 28.6.17.
 */

public interface TranslatorRepository {
    Observable<TranslationResponse> translation(final String key,
                                                final String lang,
                                                final String text);
    Observable<DictDefinition> dictDefinition(final String key,
                                              final String lang,
                                              final String text);
}
