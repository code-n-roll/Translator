package com.karanchuk.roman.testtranslate.ui.edittextnavigator;

import com.karanchuk.roman.testtranslate.ui.base.BasePresenter;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;

/**
 * Created by roman on 28.6.17.
 */

public interface EditTextLayoutPresenter extends BasePresenter {

    TranslatedItem getNextItemFromHistory();

    TranslatedItem getPreviousItemFromHistory();
}
