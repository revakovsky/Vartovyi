package com.revakovskyi.vartovyi.data.repository

import android.content.Context
import android.content.Intent
import com.revakovskyi.vartovyi.domain.repository.AlarmController
import com.revakovskyi.vartovyi.service.AlarmService
import kotlinx.coroutines.flow.Flow

class AlarmControllerImpl(
    private val context: Context,
) : AlarmController {

    override val isAlarmRunning: Flow<Boolean> = AlarmService.isRunning

    override fun triggerAlarm() {
        context.startForegroundService(Intent(context, AlarmService::class.java))
    }

    override fun stopAlarm() {
        context.startService(
            Intent(context, AlarmService::class.java).apply {
                action = AlarmService.ACTION_STOP
            }
        )
    }

}
