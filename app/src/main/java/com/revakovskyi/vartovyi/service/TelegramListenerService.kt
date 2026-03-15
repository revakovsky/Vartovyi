package com.revakovskyi.vartovyi.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.revakovskyi.vartovyi.domain.usecase.notification.NotificationPayload
import com.revakovskyi.vartovyi.domain.usecase.notification.ProcessIncomingTelegramNotificationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val EMPTY_VALUE = ""

class TelegramListenerService : NotificationListenerService(), KoinComponent {

    private val processIncomingTelegramNotificationUseCase: ProcessIncomingTelegramNotificationUseCase by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

        serviceScope.launch {
            processIncomingTelegramNotificationUseCase(payload)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun extractNotificationTitle(extras: android.os.Bundle): String =
        extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
            ?: EMPTY_VALUE

    private fun extractMessageText(extras: android.os.Bundle): String =
        extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: EMPTY_VALUE

}
