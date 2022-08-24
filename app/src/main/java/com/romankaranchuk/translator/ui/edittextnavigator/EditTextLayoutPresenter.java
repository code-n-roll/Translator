package com.romankaranchuk.translator.ui.edittextnavigator;

import com.romankaranchuk.translator.data.database.model.TranslatedItem;
import com.romankaranchuk.translator.ui.base.BasePresenter;


public interface EditTextLayoutPresenter extends BasePresenter {

    TranslatedItem getNextItemFromHistory();

    TranslatedItem getPreviousItemFromHistory();
}
