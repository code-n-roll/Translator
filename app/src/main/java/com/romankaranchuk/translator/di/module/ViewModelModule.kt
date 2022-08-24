package com.romankaranchuk.translator.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.romankaranchuk.translator.di.util.ViewModelFactory
import com.romankaranchuk.translator.di.util.ViewModelKey
import com.romankaranchuk.translator.ui.translator.TranslatorViewModel
import com.romankaranchuk.translator.ui.translator.selectlang.SelectLanguageViewModel
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
