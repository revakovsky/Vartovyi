package com.revakovskyi.vartovyi.service

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.core.app.NotificationCompat
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.NotificationPayload
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.usecase.monitoring.ToggleMonitoringUseCase
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

class TelegramListenerService : NotificationListenerService(), KoinComponent {

    private val processIncomingTelegramNotificationUseCase: ProcessIncomingTelegramNotificationUseCase by inject()
    private val toggleMonitoringUseCase: ToggleMonitoringUseCase by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onListenerConnected() {
        super.onListenerConnected()

        val active = runCatching { activeNotifications }.getOrNull() ?: return
        active.forEach { sbn -> handleIncomingNotification(sbn) }
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

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        handleIncomingNotification(sbn)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun handleIncomingNotification(sbn: StatusBarNotification?) {
        val statusBarNotification = sbn ?: return
        val notification = statusBarNotification.notification ?: return
        val extras = notification.extras ?: return

        if (notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return

        val messagingStyle = extractMessagingStyle(notification)
        val messageText = extractMessageText(
            extras = extras,
            messagingStyle = messagingStyle,
        )
        if (messageText.isBlank()) return

        val title = extractNotificationTitle(extras)
        val messageTimestamp = notification.`when`
            .takeIf { it > 0 }
            ?: statusBarNotification.postTime

        val payload = NotificationPayload(
            packageName = statusBarNotification.packageName,
            notificationKey = statusBarNotification.key.orEmpty(),
            title = title,
            text = messageText,
            timestamp = messageTimestamp,
        )

        serviceScope.launch {
            runCatching {
                processIncomingTelegramNotificationUseCase(payload)
            }.onFailure { throwable ->
                Log.e(
                    TELEGRAM_LISTENER_TAG,
                    "Failed to process incoming Telegram notification",
                    throwable,
                )
            }
        }
    }

    private fun extractNotificationTitle(extras: Bundle): String =
        extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
            ?: EMPTY_VALUE

    private fun extractMessageText(
        extras: Bundle,
        messagingStyle: NotificationCompat.MessagingStyle?,
    ): String =
        extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: extractLatestMessageFromMessagingStyle(messagingStyle)
            ?: EMPTY_VALUE

    private fun extractMessagingStyle(notification: Notification): NotificationCompat.MessagingStyle? {
        return runCatching {
            NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(notification)
        }.getOrNull()
    }

    private fun extractLatestMessageFromMessagingStyle(
        messagingStyle: NotificationCompat.MessagingStyle?,
    ): String? {
        if (messagingStyle == null) return null

        return messagingStyle.messages
            .asReversed()
            .firstNotNullOfOrNull { message ->
                message.text
                    ?.toString()
                    ?.takeIf { text -> text.isNotBlank() }
            }
    }

}
