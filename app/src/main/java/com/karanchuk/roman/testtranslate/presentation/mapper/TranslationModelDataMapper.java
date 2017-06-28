package com.karanchuk.roman.testtranslate.presentation.mapper;

import com.karanchuk.roman.testtranslate.domain.Translation;
import com.karanchuk.roman.testtranslate.presentation.model.study_work.TranslationModel;

/**
 * Created by roman on 28.6.17.
 */

public class TranslationModelDataMapper {
    public TranslationModelDataMapper() {
    }

    public TranslationModel transform(Translation translation){
        if (translation == null){
            throw new IllegalArgumentException("Cannot transform a null value");
        }

        final TranslationModel translationModel = new TranslationModel();

        return translationModel;
    }
}
