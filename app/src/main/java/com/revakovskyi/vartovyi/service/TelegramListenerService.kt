package com.revakovskyi.vartovyi.service

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.usecase.monitoring.ToggleMonitoringUseCase
import com.revakovskyi.vartovyi.usecase.notification.NotificationPayload
import com.revakovskyi.vartovyi.usecase.notification.ProcessIncomingTelegramNotificationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val EMPTY_VALUE = ""
private const val TELEGRAM_LISTENER_TAG = "TelegramListenerService"
private const val RECENT_NOTIFICATION_SIGNATURES_LIMIT = 5

class TelegramListenerService : NotificationListenerService(), KoinComponent {

    private val processIncomingTelegramNotificationUseCase: ProcessIncomingTelegramNotificationUseCase by inject()
    private val toggleMonitoringUseCase: ToggleMonitoringUseCase by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val deduplicationLock = Any()
    private val recentNotificationSignatures =
        ArrayDeque<String>(RECENT_NOTIFICATION_SIGNATURES_LIMIT)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val statusBarNotification = sbn ?: return
        val extras = statusBarNotification.notification.extras ?: return

        val messageText = extractMessageText(extras)
        if (messageText.isBlank()) return

        val title = extractNotificationTitle(extras)
        val payload = NotificationPayload(
            packageName = statusBarNotification.packageName,
            title = title,
            text = messageText,
            timestamp = statusBarNotification.postTime,
        )

        if (isDuplicateNotification(statusBarNotification, payload)) return

        serviceScope.launch {
            processIncomingTelegramNotificationUseCase(payload)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()

        serviceScope.launch {
            runCatching {
                val isMonitoringActive = settingsRepository.isMonitoringActive.first()
                if (!isMonitoringActive) return@runCatching

                toggleMonitoringUseCase(isCurrentlyActive = true)

                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = getString(R.string.monitoring_disabled_listener_revoked),
                        duration = SnackbarDuration.Long,
                    )
                )
            }.onFailure { throwable ->
                Log.e(
                    TELEGRAM_LISTENER_TAG,
                    "Failed to deactivate monitoring on listener disconnect",
                    throwable,
                )
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        synchronized(deduplicationLock) {
            recentNotificationSignatures.clear()
        }
        super.onDestroy()
    }

    private fun extractNotificationTitle(extras: Bundle): String =
        extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
            ?: EMPTY_VALUE

    private fun extractMessageText(extras: Bundle): String =
        extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: extractMessageFromMessagingStyle(extras)
            ?: EMPTY_VALUE

    private fun extractMessageFromMessagingStyle(extras: Bundle): String? {
        val messages = runCatching {
            Notification.MessagingStyle.Message.getMessagesFromBundleArray(
                extras.getParcelableArray(Notification.EXTRA_MESSAGES)
            )
        }.getOrNull() ?: return null

        return messages
            .asReversed()
            .firstNotNullOfOrNull { message ->
                message.text
                    ?.toString()
                    ?.takeIf { text -> text.isNotBlank() }
            }
    }

    private fun isDuplicateNotification(
        statusBarNotification: StatusBarNotification,
        payload: NotificationPayload,
    ): Boolean {
        val signature = buildNotificationSignature(statusBarNotification, payload)

        synchronized(deduplicationLock) {
            if (signature in recentNotificationSignatures) {
                return true
            }

            if (recentNotificationSignatures.size >= RECENT_NOTIFICATION_SIGNATURES_LIMIT) {
                recentNotificationSignatures.removeFirst()
            }

            recentNotificationSignatures.addLast(signature)
            return false
        }
    }

    private fun buildNotificationSignature(
        statusBarNotification: StatusBarNotification,
        payload: NotificationPayload,
    ): String {
        return buildString {
            append(statusBarNotification.packageName)
            append('|')
            append(statusBarNotification.key)
            append('|')
            append(payload.timestamp)
            append('|')
            append(payload.title)
            append('|')
            append(payload.text)
        }
    }

}
