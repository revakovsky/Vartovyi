package com.revakovskyi.vartovyi.service.alarm

import android.content.Context
import android.content.Intent
import com.revakovskyi.vartovyi.domain.constants.AlarmContract
import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmStateHolder
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicBoolean

class AlarmControllerImpl(
    private val context: Context,
    private val alarmStateHolder: AlarmStateHolder,
) : AlarmController {

    override val isAlarmRunning: Flow<Boolean> = alarmStateHolder.isRunning

    override fun triggerAlarm(
        sourceChannelName: String,
        sourceMessageText: String,
    ) {
        if (alarmStateHolder.isRunning.value) return

        if (!isStartRequested.compareAndSet(false, true)) return

        try {
            context.startForegroundService(
                Intent(context, AlarmService::class.java).apply {
                    putExtra(AlarmContract.EXTRA_SOURCE_CHANNEL_NAME, sourceChannelName)
                    putExtra(AlarmContract.EXTRA_SOURCE_MESSAGE_TEXT, sourceMessageText)
                }
            )
        } finally {
            isStartRequested.set(false)
        }
    }

    override fun stopAlarm() {
        isStartRequested.set(false)

        context.startService(
            Intent(context, AlarmService::class.java).apply {
                action = AlarmContract.ACTION_STOP
            }
        )
    }

    companion object {
        private val isStartRequested = AtomicBoolean(false)
    }

}
