package com.mionix.myapplication

import android.app.Application
import com.mionix.myapplication.di.appModule
import com.mionix.myapplication.di.repositoryModule
import com.mionix.myapplication.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // start Koin!
        startKoin {
            androidLogger(Level.DEBUG)
            // Android context
            androidContext(this@MyApplication)
            // modules
            modules(arrayListOf(
                appModule/*,retrofitModule*/,
                viewModelModule,
                repositoryModule
            ))
        }
    }
}