package com.revakovskyi.vartovyi.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val BOOT_RECEIVER_TAG = "BootReceiver"
private const val BOOT_INIT_TIMEOUT_MILLIS = 9_000L

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val settingsRepository: SettingsRepository by inject()
    private val monitoringController: MonitoringController by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                withTimeoutOrNull(BOOT_INIT_TIMEOUT_MILLIS) {
                    settingsRepository.setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(0L)

                    if (settingsRepository.isMonitoringActive.first()) {
                        monitoringController.startMonitoring()
                    }
                }
            } catch (throwable: Throwable) {
                Log.e(BOOT_RECEIVER_TAG, "Boot init failed", throwable)
            } finally {
                pendingResult.finish()
            }
        }
    }

}
