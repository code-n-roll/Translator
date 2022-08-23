package com.karanchuk.roman.testtranslate

import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.karanchuk.roman.testtranslate.common.Constants.SPEECH_KIT_API_KEY
import com.karanchuk.roman.testtranslate.di.DaggerApplicationComponent
import com.karanchuk.roman.testtranslate.di.module.ApplicationModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import ru.yandex.speechkit.SpeechKit
import javax.inject.Inject

class TranslatorApplication : MultiDexApplication(), HasActivityInjector {//, HasSupportFragmentInjector {

    companion object {
//        lateinit var appComponent: ApplicationComponent
        lateinit var instance: TranslatorApplication
    }

    @Inject lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
//    @Inject lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate() {
        super.onCreate()

        setupDagger()
//        setupSpeechKit()
        instance = this
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }

//    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
//        return fragmentDispatchingAndroidInjector
//    }

    private fun setupDagger() {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
            .inject(this)
    }

    private fun setupSpeechKit() {
        SpeechKit.getInstance().init(this, SPEECH_KIT_API_KEY);
    }
}