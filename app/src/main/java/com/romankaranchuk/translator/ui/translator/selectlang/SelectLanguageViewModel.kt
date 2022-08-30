package com.romankaranchuk.translator.ui.translator.selectlang

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.romankaranchuk.translator.data.database.model.Language
import com.romankaranchuk.translator.data.datasource.LanguagesLocalDataSource
import com.romankaranchuk.translator.ui.base.BaseViewModel
import com.romankaranchuk.translator.ui.base.launchOnIO
import com.romankaranchuk.translator.ui.base.switchToUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class SelectLanguageViewModel @Inject constructor(
    private val languagesLocalDataSource: LanguagesLocalDataSource
) : BaseViewModel(), DefaultLifecycleObserver {

    private var _isSource = false
    val isSource get() = _isSource

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        _isSource = (owner as FragmentActivity).intent.getStringExtra("TYPE") == "SOURCE"
        loadLanguages()
    }

    private var _viewState = MutableSharedFlow<ViewState>()
    val viewState = _viewState.asSharedFlow()

    private fun loadLanguages() = launchOnIO {
        val languagesSorted = languagesLocalDataSource.getLanguages().sorted()

        val abbr = languagesLocalDataSource.restoreSelectedLanguage(_isSource)
        val selectedLang = languagesSorted.find { it.abbr == abbr } ?: "Unknown"
        val selectedLangIndex = languagesSorted.indexOf(selectedLang)

        switchToUi {
            _viewState.emit(ViewState.ShowLanguages(languagesSorted, selectedLangIndex))
        }
    }

    fun onLanguageItemClick(language: Language) = launchOnIO {
        languagesLocalDataSource.saveSelectedLanguage(_isSource, language)

        switchToUi {
            _viewState.emit(ViewState.ShowLanguageSelected(language))
        }
    }

    sealed class ViewState {
        class ShowLanguages(val languages: List<Language>, val selectedId: Int) : ViewState()
        class ShowLanguageSelected(val language: Language) : ViewState()
    }
}