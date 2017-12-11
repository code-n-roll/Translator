package com.karanchuk.roman.testtranslate.stored.history;

import com.karanchuk.roman.testtranslate.common.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.common.BasePresenter;

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
