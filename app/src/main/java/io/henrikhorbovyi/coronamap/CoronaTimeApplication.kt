package io.henrikhorbovyi.coronamap

import android.app.Application
import io.henrikhorbovyi.coronamap.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

/**
 * .:.:.:. Created by @henrikhorbovyi on 3/31/20 .:.:.:.
 */

class CoronaTimeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        koinSetup()
        timberSetup()
    }

    private fun koinSetup() {
        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@CoronaTimeApplication)
            modules(listOf(appModules))
        }
    }

    private fun timberSetup() {
        /*if (BuildConfig.DEBUG)*/ Timber.plant(Timber.DebugTree())
    }
}