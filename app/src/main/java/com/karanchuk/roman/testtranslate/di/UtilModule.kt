package com.karanchuk.roman.testtranslate.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }
}