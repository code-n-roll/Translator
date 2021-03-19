package com.karanchuk.roman.testtranslate

import android.app.Application
import com.karanchuk.roman.testtranslate.di.*


class TestTranslatorApplication : Application() {

    companion object {
        lateinit var appComponent: AppComponent
        lateinit var instance: TestTranslatorApplication
    }

    override fun onCreate() {
        super.onCreate()

        initDagger()
        instance = this
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder()
                .networkModule(NetworkModule())
                .databaseModule(DatabaseModule())
                .utilModule(UtilModule())
                .build()
    }
}