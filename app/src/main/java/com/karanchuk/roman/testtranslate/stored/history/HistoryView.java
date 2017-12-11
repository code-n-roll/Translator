package com.karanchuk.roman.testtranslate.stored.history;

import com.karanchuk.roman.testtranslate.common.BaseView;
import com.karanchuk.roman.testtranslate.common.model.TranslatedItem;

import java.util.List;

/**
 * Created by roman on 29.6.17.
 */

public interface HistoryView extends BaseView<HistoryPresenter> {

    void handleShowClearStored();

    void handleShowingSearchView(List<TranslatedItem> items);
}
