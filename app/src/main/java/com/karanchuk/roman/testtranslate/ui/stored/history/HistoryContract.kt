package com.karanchuk.roman.testtranslate.ui.stored.history

import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem
import com.karanchuk.roman.testtranslate.ui.base.BaseView

interface HistoryContract {

    interface HistoryPresenter {
        fun attachView(fragment: HistoryFragment)
        fun detachView()
        fun clickOnSetFavoriteItem(item: TranslatedItem)
        fun getSearchedText(newText: String): List<TranslatedItem>
        fun performContextItemDeletion(position: Int)
        fun clickOnItemStoredRecycler(item: TranslatedItem)
        val historyTranslatedItems: List<TranslatedItem>
    }

    interface HistoryView : BaseView<HistoryPresenter> {
        fun handleShowClearStored()
        fun handleShowingSearchView(items: List<TranslatedItem>)
    }
}