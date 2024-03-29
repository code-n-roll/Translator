package com.romankaranchuk.translator.di.module

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.romankaranchuk.translator.data.datasource.LanguagesLocalDataSource
import dagger.Module
import dagger.Provides

@Module
class DataSourceModule {

    @Provides
    fun provideLanguagesDataSource(
        context: Context,
        gson: Gson,
        sharedPrefs: SharedPreferences
    ): LanguagesLocalDataSource {
        return LanguagesLocalDataSource(context, gson, sharedPrefs)
    }
}