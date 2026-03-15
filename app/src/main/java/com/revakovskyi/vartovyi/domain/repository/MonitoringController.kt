package com.revakovskyi.vartovyi.domain.repository

import kotlinx.coroutines.flow.Flow

interface MonitoringController {
    val isMonitoringRunning: Flow<Boolean>

    fun startMonitoring()

    fun stopMonitoring()

}
