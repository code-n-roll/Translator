package com.karanchuk.roman.testtranslate.di

import com.karanchuk.roman.testtranslate.TranslatorApplication
import com.karanchuk.roman.testtranslate.di.module.ActivityModule
import com.karanchuk.roman.testtranslate.di.module.ApplicationModule
import com.karanchuk.roman.testtranslate.di.module.DatabaseModule
import com.karanchuk.roman.testtranslate.di.module.FragmentModule
import com.karanchuk.roman.testtranslate.di.module.NetworkModule
import com.karanchuk.roman.testtranslate.di.module.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ActivityModule::class,
    ApplicationModule::class,
    FragmentModule::class,
    ViewModelModule::class,
    NetworkModule::class,
    DatabaseModule::class
])
interface ApplicationComponent{
    fun inject(app: TranslatorApplication)
}
