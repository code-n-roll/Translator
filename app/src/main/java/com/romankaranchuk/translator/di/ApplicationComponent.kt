package com.romankaranchuk.translator.di

import com.romankaranchuk.translator.TranslatorApplication
import com.romankaranchuk.translator.di.module.ActivityModule
import com.romankaranchuk.translator.di.module.ApplicationModule
import com.romankaranchuk.translator.di.module.DatabaseModule
import com.romankaranchuk.translator.di.module.FragmentModule
import com.romankaranchuk.translator.di.module.NetworkModule
import com.romankaranchuk.translator.di.module.ViewModelModule
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
