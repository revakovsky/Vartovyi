package com.revakovskyi.vartovyi.data.repository

import android.content.Context
import android.content.Intent
import com.revakovskyi.vartovyi.domain.repository.AlarmController
import com.revakovskyi.vartovyi.service.AlarmService
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicBoolean

class AlarmControllerImpl(
    private val context: Context,
) : AlarmController {

    override val isAlarmRunning: Flow<Boolean> = AlarmService.isRunning

    override fun triggerAlarm() {
        if (AlarmService.isRunning.value) return

        if (!isStartRequested.compareAndSet(false, true)) return

        try {
            context.startForegroundService(Intent(context, AlarmService::class.java))
        } finally {
            isStartRequested.set(false)
        }
    }

    override fun stopAlarm() {
        isStartRequested.set(false)

        context.startService(
            Intent(context, AlarmService::class.java).apply {
                action = AlarmService.ACTION_STOP
            }
        )
    }

    companion object {
        private val isStartRequested = AtomicBoolean(false)
    }

}
