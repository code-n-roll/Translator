package com.romankaranchuk.translator.di

import com.romankaranchuk.translator.TranslatorApplication
import com.romankaranchuk.translator.di.module.*
import com.romankaranchuk.translator.di.module.injector.ActivityModule
import com.romankaranchuk.translator.di.module.injector.FragmentModule
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ActivityModule::class,
    FragmentModule::class,

    ViewModelModule::class,

    ApplicationModule::class,
    NetworkModule::class,
    DatabaseModule::class,
    RepositoryModule::class,

    AndroidInjectionModule::class,
])
interface ApplicationComponent {
    fun inject(app: TranslatorApplication)
}
