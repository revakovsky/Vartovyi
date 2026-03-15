package com.revakovskyi.vartovyi.data.repository

import android.content.Context
import com.revakovskyi.vartovyi.domain.repository.MonitoringController
import com.revakovskyi.vartovyi.service.MonitoringForegroundService
import com.revakovskyi.vartovyi.service.watchdog.MonitoringWatchdogWorker

class MonitoringControllerImpl(
    private val context: Context,
) : MonitoringController {

    override fun startMonitoring() {
        MonitoringForegroundService.start(context)
        MonitoringWatchdogWorker.enqueue(context)
    }

    override fun stopMonitoring() {
        MonitoringForegroundService.stop(context)
        MonitoringWatchdogWorker.cancel(context)
    }

}
