package com.revakovskyi.vartovyi

import android.app.Application
import androidx.work.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.data.di.databaseModule
import com.revakovskyi.vartovyi.data.di.repositoryModule
import com.revakovskyi.vartovyi.di.appModule
import com.revakovskyi.vartovyi.di.useCaseModule
import com.revakovskyi.vartovyi.di.viewModelModule
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.service.notification_monitoring.MonitoringWorkerFactory
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class VartovyiApp : Application(), Configuration.Provider {

    private val settingsRepository: SettingsRepository by inject()
    private val monitoringController: MonitoringController by inject()

    override fun onCreate() {
        super.onCreate()
        initCrashlytics()
        initKoin()
    }

    private fun initCrashlytics() {
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@VartovyiApp)
            androidLogger(Level.DEBUG)
            modules(appModule, databaseModule, repositoryModule, useCaseModule, viewModelModule)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                MonitoringWorkerFactory(
                    settingsRepository = settingsRepository,
                    monitoringController = monitoringController,
                )
            )
            .build()

}
