package com.romankaranchuk.translator.di.module

import com.romankaranchuk.translator.ui.main.MainActivity
import com.romankaranchuk.translator.ui.translator.selectlang.SelectLanguageActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeSourceLangActivityInjector(): SelectLanguageActivity

}
