package com.romankaranchuk.translator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.multidex.MultiDexApplication
import com.romankaranchuk.translator.common.Constants.SPEECH_KIT_API_KEY
import com.romankaranchuk.translator.di.module.ApplicationModule
import com.romankaranchuk.translator.di.DaggerApplicationComponent
import com.romankaranchuk.translator.di.util.Injectable
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
//import ru.yandex.speechkit.SpeechKit
import timber.log.Timber
import javax.inject.Inject

class TranslatorApplication : MultiDexApplication(), HasAndroidInjector {

    companion object {
//        lateinit var appComponent: ApplicationComponent
        lateinit var instance: TranslatorApplication
    }

    @Inject lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()

        setupDagger()
        setupTimber()
//        setupSpeechKit()
        instance = this
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return activityDispatchingAndroidInjector
    }

    private fun setupTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun setupDagger() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityStopped(activity: Activity) {}

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is FragmentActivity) {
                    activity.supportFragmentManager
                        .registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                            override fun onFragmentPreAttached(
                                fm: FragmentManager,
                                f: Fragment,
                                context: Context
                            ) {
                                super.onFragmentPreAttached(fm, f, context)
                                if (f is Injectable) {
                                    AndroidSupportInjection.inject(f)
                                }
                            }
                        }, true)
                }
            }
        })

        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
            .inject(this)
    }

//    private fun setupSpeechKit() {
//        SpeechKit.getInstance().init(this, SPEECH_KIT_API_KEY);
//    }
}