package com.karanchuk.roman.testtranslate.presentation.model.study_work;

import com.karanchuk.roman.testtranslate.presentation.model.Expression;
import com.karanchuk.roman.testtranslate.presentation.model.Meaning;
import com.karanchuk.roman.testtranslate.presentation.model.Synonym;

import java.util.List;

/**
 * Created by roman on 28.6.17.
 */

public class TranslationModel {
    private String mNumber;

    private String mText;

    private String mGen;

    private List<Synonym> mSynonyms;

    private List<Meaning> mMeanings;

    private List<Expression> mExpressions;

    private String mRepresentSynonyms;
    private String mRepresentMeanings;
    private String mRepresentExpressions;
}
