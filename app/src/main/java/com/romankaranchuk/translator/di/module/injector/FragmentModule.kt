package com.romankaranchuk.translator.di.module.injector

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.romankaranchuk.translator.di.util.FragmentInjectionFactory
import com.romankaranchuk.translator.ui.stored.StoredFragment
import com.romankaranchuk.translator.ui.translator.TranslatorFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
abstract class FragmentModule {

    @Binds
    abstract fun bindFragmentFactory(factory: FragmentInjectionFactory): FragmentFactory

//    @Binds
//    @IntoMap
//    @ClassKey(TranslatorFragment::class)
//    abstract fun bindTranslatorFragment(fragment: TranslatorFragment): Fragment

    @Binds
    @IntoMap
    @ClassKey(StoredFragment::class)
    abstract fun bindStoredFragment(fragment: StoredFragment): Fragment

    @ContributesAndroidInjector
    abstract fun contributeTranslatorFragment(): TranslatorFragment
}
