package com.romankaranchuk.translator.di.module

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.romankaranchuk.translator.di.FragmentInjectionFactory
import com.romankaranchuk.translator.ui.translator.TranslatorFragment
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
    @FragmentKey(com.romankaranchuk.translator.ui.stored.StoredFragment::class)
    abstract fun bindStoredFragment(fragment: com.romankaranchuk.translator.ui.stored.StoredFragment): Fragment
}
