package com.romankaranchuk.translator.ui.stored.history

interface HistoryContract {

    interface HistoryPresenter {
        fun attachView(fragment: HistoryFragment)
        fun detachView()
        fun clickOnSetFavoriteItem(item: com.romankaranchuk.translator.data.database.model.TranslatedItem)
        fun getSearchedText(newText: String): List<com.romankaranchuk.translator.data.database.model.TranslatedItem>
        fun performContextItemDeletion(position: Int)
        fun clickOnItemStoredRecycler(item: com.romankaranchuk.translator.data.database.model.TranslatedItem)
        val historyTranslatedItems: List<com.romankaranchuk.translator.data.database.model.TranslatedItem>
    }

    interface HistoryView :
        com.romankaranchuk.translator.ui.base.BaseView<HistoryPresenter> {
        fun handleShowClearStored()
        fun handleShowingSearchView(items: List<com.romankaranchuk.translator.data.database.model.TranslatedItem>)
    }
}