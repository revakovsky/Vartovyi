package com.revakovskyi.vartovyi.service.notification_monitoring

import android.content.Context
import com.revakovskyi.vartovyi.domain.controllers.notification_monitoring.MonitoringController
import kotlinx.coroutines.flow.Flow

class MonitoringControllerImpl(
    private val context: Context,
) : MonitoringController {

    override val isMonitoringRunning: Flow<Boolean> = MonitoringForegroundService.isRunning

    override fun startMonitoring() {
        MonitoringForegroundService.start(context)
        MonitoringWatchdogWorker.enqueue(context)
    }

    override fun stopMonitoring() {
        MonitoringForegroundService.stop(context)
        MonitoringWatchdogWorker.cancel(context)
    }

}
