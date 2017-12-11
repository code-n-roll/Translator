package com.karanchuk.roman.testtranslate.edittextnavigator;

import com.karanchuk.roman.testtranslate.common.BasePresenter;
import com.karanchuk.roman.testtranslate.common.model.TranslatedItem;

/**
 * Created by roman on 28.6.17.
 */

public interface EditTextLayoutPresenter extends BasePresenter {

    TranslatedItem getNextItemFromHistory();

    TranslatedItem getPreviousItemFromHistory();
}
