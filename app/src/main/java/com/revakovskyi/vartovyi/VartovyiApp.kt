package com.revakovskyi.vartovyi

import android.app.Application
import com.revakovskyi.vartovyi.data.di.databaseModule
import com.revakovskyi.vartovyi.data.di.repositoryModule
import com.revakovskyi.vartovyi.di.appModule
import com.revakovskyi.vartovyi.di.useCaseModule
import com.revakovskyi.vartovyi.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class VartovyiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@VartovyiApp)
            androidLogger(Level.DEBUG)
            modules(appModule, databaseModule, repositoryModule, useCaseModule, viewModelModule)
        }
    }

}
