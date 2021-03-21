package com.karanchuk.roman.testtranslate.di.module

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.karanchuk.roman.testtranslate.di.FragmentInjectionFactory
import com.karanchuk.roman.testtranslate.ui.stored.StoredFragment
import com.karanchuk.roman.testtranslate.ui.stored.history.HistoryFragment
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorFragment
import dagger.Binds
import dagger.Module
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap

@Module
abstract class FragmentModule {

    @Binds
    abstract fun bindFragmentFactory(factory: FragmentInjectionFactory): FragmentFactory

    @Binds
    @IntoMap
    @FragmentKey(TranslatorFragment::class)
    abstract fun bindTranslatorFragment(fragment: TranslatorFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(StoredFragment::class)
    abstract fun bindStoredFragment(fragment: StoredFragment): Fragment
}
