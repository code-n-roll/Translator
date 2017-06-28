package com.karanchuk.roman.testtranslate.presentation.view;

import com.karanchuk.roman.testtranslate.presentation.model.TranslatedItem;
import com.karanchuk.roman.testtranslate.presentation.presenter.HistoryPresenter;

import java.util.List;

/**
 * Created by roman on 29.6.17.
 */

public interface HistoryView extends BaseView<HistoryPresenter> {
    void handleShowClearStored();
    void handleShowingSearchView(List<TranslatedItem> items);
}
