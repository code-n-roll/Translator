package com.karanchuk.roman.testtranslate.di

import com.karanchuk.roman.testtranslate.presentation.ui.translator.TranslatorPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface AppComponent {

    fun inject(presenter: TranslatorPresenter)
}