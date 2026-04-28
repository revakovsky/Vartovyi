package com.revakovskyi.vartovyi.service.notification_monitoring

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.controllers.alarm.AlarmRetriggerCooldownStateHolder
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.ui.MainActivity
import com.revakovskyi.vartovyi.usecase.monitoring.SyncMonitoringRuntimeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.atomic.AtomicBoolean

private const val MONITORING_NOTIFICATION_ID = 2001
private const val MONITORING_CHANNEL_ID = "vartovyi_monitoring"
private const val GREEN_ACCENT_COLOR_RES_ID = android.R.color.holo_green_dark
private const val ACTION_STOP = "com.revakovskyi.vartovyi.ACTION_STOP_MONITORING"
private const val MONITORING_SERVICE_TAG = "MonitoringService"
private const val STOP_DATASTORE_WRITE_TIMEOUT_MILLIS = 2_000L

class MonitoringForegroundService : Service(), KoinComponent {

    private val settingsRepository: SettingsRepository by inject()
    private val alarmRetriggerCooldownStateHolder: AlarmRetriggerCooldownStateHolder by inject()
    private val syncMonitoringRuntimeUseCase: SyncMonitoringRuntimeUseCase by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val isStopRequested = AtomicBoolean(false)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        alarmRetriggerCooldownStateHolder.bindMonitoringScope(serviceScope)

        serviceScope.launch {
            runCatching {
                syncMonitoringRuntimeUseCase()
            }.onFailure { throwable ->
                Log.e(
                    MONITORING_SERVICE_TAG,
                    "Failed to sync monitoring runtime on service start",
                    throwable,
                )
            }
        }

        serviceScope.launch {
            settingsRepository.alarmRetriggerCooldownUntilElapsedRealtimeMillis.collect { untilElapsedRealtimeMillis ->
                alarmRetriggerCooldownStateHolder.setCooldownUntilElapsedRealtimeMillis(
                    untilElapsedRealtimeMillis
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            if (!isStopRequested.compareAndSet(false, true)) {
                return START_NOT_STICKY
            }

            MonitoringWatchdogWorker.cancel(this)
            _isRunning.update { false }

            serviceScope.launch {
                withContext(NonCancellable) {
                    withTimeoutOrNull(STOP_DATASTORE_WRITE_TIMEOUT_MILLIS) {
                        settingsRepository.setMonitoringActive(false)
                        settingsRepository.setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(0L)
                    }
                }

                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            return START_NOT_STICKY
        }

        _isRunning.update { true }
        startForeground(MONITORING_NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    override fun onDestroy() {
        _isRunning.update { false }
        alarmRetriggerCooldownStateHolder.clearAndUnbind()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            MONITORING_CHANNEL_ID,
            getString(R.string.monitoring_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.monitoring_channel_description)
            setSound(null, null)
            enableVibration(false)
        }

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, MonitoringForegroundService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, MONITORING_CHANNEL_ID)
            .setSmallIcon(R.drawable.security_on)
            .setContentTitle(getString(R.string.monitoring_notification_title))
            .setContentText(getString(R.string.monitoring_notification_text))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setColor(ContextCompat.getColor(this, GREEN_ACCENT_COLOR_RES_ID))
            .setColorized(true)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .addAction(
                0,
                getString(R.string.monitoring_deactivate),
                stopPendingIntent,
            )
            .build()
    }

    companion object {
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        fun start(context: Context) {
            context.startForegroundService(
                Intent(context, MonitoringForegroundService::class.java)
            )
        }

        fun stop(context: Context) {
            context.startService(
                Intent(context, MonitoringForegroundService::class.java).apply {
                    action = ACTION_STOP
                }
            )
        }
    }

}
