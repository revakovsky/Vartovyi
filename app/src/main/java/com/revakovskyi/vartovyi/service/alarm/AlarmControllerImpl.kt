package com.revakovskyi.vartovyi.service.alarm

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.revakovskyi.vartovyi.constants.AlarmContract
import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.alarm.AlarmStateHolder
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.atomic.AtomicBoolean

private const val SERVICE_START_WAKE_LOCK_TIMEOUT_MILLIS = 10_000L
private const val SERVICE_START_WAKE_LOCK_TAG = "Vartovyi:ServiceStartWakeLock"
private const val ALARM_CONTROLLER_TAG = "AlarmController"

@Suppress("TooGenericExceptionCaught")
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
            acquireServiceStartWakeLock()

            context.startForegroundService(
                Intent(context, AlarmService::class.java).apply {
                    putExtra(AlarmContract.EXTRA_SOURCE_CHANNEL_NAME, sourceChannelName)
                    putExtra(AlarmContract.EXTRA_SOURCE_MESSAGE_TEXT, sourceMessageText)
                }
            )
        } catch (throwable: Throwable) {
            releaseServiceStartWakeLock()
            Log.e(ALARM_CONTROLLER_TAG, "Failed to start alarm service", throwable)
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

    private fun acquireServiceStartWakeLock() {
        releaseServiceStartWakeLock()

        try {
            val powerManager =
                context.getSystemService(Context.POWER_SERVICE) as PowerManager

            serviceStartWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                SERVICE_START_WAKE_LOCK_TAG,
            ).apply {
                setReferenceCounted(false)
                acquire(SERVICE_START_WAKE_LOCK_TIMEOUT_MILLIS)
            }
        } catch (throwable: Throwable) {
            Log.e(ALARM_CONTROLLER_TAG, "Failed to acquire service start wake lock", throwable)
        }
    }

    companion object {

        private val isStartRequested = AtomicBoolean(false)
        private var serviceStartWakeLock: PowerManager.WakeLock? = null

        fun releaseServiceStartWakeLock() {
            try {
                serviceStartWakeLock?.let { wakeLock ->
                    if (wakeLock.isHeld) wakeLock.release()
                }
            } catch (throwable: Throwable) {
                Log.e(ALARM_CONTROLLER_TAG, "Failed to release service start wake lock", throwable)
            }

            serviceStartWakeLock = null
        }
    }

}
