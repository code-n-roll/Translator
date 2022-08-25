package com.romankaranchuk.translator.ui.translator.selectlang

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.romankaranchuk.translator.data.database.model.Language
import com.romankaranchuk.translator.data.datasource.LanguagesDataSource
import com.romankaranchuk.translator.ui.base.BaseViewModel
import com.romankaranchuk.translator.ui.base.launchOnIO
import com.romankaranchuk.translator.ui.base.switchToUi
import javax.inject.Inject

class SelectLanguageViewModel @Inject constructor(
    private val languagesDataSource: LanguagesDataSource
) : BaseViewModel(), DefaultLifecycleObserver {

    private var _isSource = false
    val isSource get() = _isSource

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        _isSource = (owner as FragmentActivity).intent.getStringExtra("TYPE") == "SOURCE"
        loadLanguages()
    }

    private var _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private fun loadLanguages() = launchOnIO {
        val languagesSorted = languagesDataSource.getLanguagesFromJson()

        val abbr = languagesDataSource.restoreSelectedLanguage(_isSource)
        val selectedLang = languagesSorted.find { it.abbr == abbr } ?: "Unknown"
        val selectedLangIndex = languagesSorted.indexOf(selectedLang)

        switchToUi {
            _viewState.value = ViewState.ShowLanguages(languagesSorted, selectedLangIndex)
        }
    }

    fun onLanguageItemClick(language: Language) = launchOnIO {
        languagesDataSource.saveSelectedLanguage(_isSource, language)

        switchToUi {
            _viewState.value = ViewState.ShowLanguageSelected(language)
        }
    }

    sealed class ViewState {
        class ShowLanguages(val languages: List<Language>, val selectedId: Int) : ViewState()
        class ShowLanguageSelected(val language: Language) : ViewState()
    }
}