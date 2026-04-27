package com.revakovskyi.vartovyi.service.notification_monitoring

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.SettingsRepository

class MonitoringWorkerFactory(
    private val settingsRepository: SettingsRepository,
    private val monitoringController: MonitoringController,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        if (workerClassName != MonitoringWatchdogWorker::class.java.name) {
            return null
        }

        return MonitoringWatchdogWorker(
            appContext = appContext,
            params = workerParameters,
            settingsRepository = settingsRepository,
            monitoringController = monitoringController,
        )
    }

}
