package com.karanchuk.roman.testtranslate.di.module

import com.karanchuk.roman.testtranslate.ui.main.MainActivity
import com.karanchuk.roman.testtranslate.ui.translator.selectlang.SelectLanguageActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeSourceLangActivityInjector(): SelectLanguageActivity

}
