package com.revakovskyi.vartovyi.domain.controllers.notification_monitoring

import kotlinx.coroutines.flow.Flow

interface MonitoringController {

    val isMonitoringRunning: Flow<Boolean>

    fun startMonitoring()
    fun stopMonitoring()
}
