package com.revakovskyi.vartovyi.service.notification_monitoring

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.revakovskyi.vartovyi.domain.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

private const val MONITORING_WATCHDOG_WORK_NAME = "monitoring_watchdog_work"

class MonitoringWatchdogWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.get()
        val settingsRepository = koin.get<SettingsRepository>()
        val monitoringController = koin.get<MonitoringController>()

        if (settingsRepository.isMonitoringActive.first()) {
            monitoringController.startMonitoring()
        }

        return Result.success()
    }

    companion object {
        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<MonitoringWatchdogWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                MONITORING_WATCHDOG_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(MONITORING_WATCHDOG_WORK_NAME)
        }
    }

}
