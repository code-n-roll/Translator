package com.karanchuk.roman.testtranslate.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.karanchuk.roman.testtranslate.di.ViewModelFactory
import com.karanchuk.roman.testtranslate.di.ViewModelKey
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorViewModel
import com.karanchuk.roman.testtranslate.ui.translator.selectlang.SelectLanguageViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TranslatorViewModel::class)
    abstract fun bindTranslatorViewModel(viewModel: TranslatorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectLanguageViewModel::class)
    abstract fun bindSelectLanguageViewModel(viewModel: SelectLanguageViewModel): ViewModel
}
