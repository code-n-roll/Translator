package com.karanchuk.roman.testtranslate.ui.stored.history;

import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.ui.base.BasePresenter;

import java.util.List;

/**
 * Created by roman on 16.6.17.
 */

public interface HistoryPresenter extends BasePresenter {

    void clickOnSetFavoriteItem(TranslatedItem item);

    List<TranslatedItem> getSearchedText(String newText);

    void performContextItemDeletion(int position);

    void clickOnItemStoredRecycler(TranslatedItem item);
}
