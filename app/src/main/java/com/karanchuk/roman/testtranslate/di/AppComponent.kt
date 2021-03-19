package com.karanchuk.roman.testtranslate.di

import com.karanchuk.roman.testtranslate.ui.translator.sourcelang.SourceLangActivity
import com.karanchuk.roman.testtranslate.ui.translator.targetlang.TargetLangActivity
import com.karanchuk.roman.testtranslate.ui.translator.TranslatorPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class, UtilModule::class])
interface AppComponent {

    fun inject(presenter: TranslatorPresenter)

    fun inject(activity: SourceLangActivity)

    fun inject(activity: TargetLangActivity)
}