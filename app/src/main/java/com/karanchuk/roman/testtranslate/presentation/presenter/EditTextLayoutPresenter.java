package com.karanchuk.roman.testtranslate.presentation.presenter;

import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;

/**
 * Created by roman on 28.6.17.
 */

public interface EditTextLayoutPresenter extends BasePresenter {
    TranslatedItem getNextItemFromHistory();
    TranslatedItem getPreviousItemFromHistory();
}
