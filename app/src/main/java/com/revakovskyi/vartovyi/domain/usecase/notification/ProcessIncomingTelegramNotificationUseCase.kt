package com.revakovskyi.vartovyi.domain.usecase.notification

import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.repository.AlarmController
import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val EMPTY_MATCHED_KEYWORD = ""
private const val TIME_PATTERN = "HH:mm"

interface ProcessIncomingTelegramNotificationUseCase {
    suspend operator fun invoke(payload: NotificationPayload): Boolean
}

data class NotificationPayload(
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
)

class ProcessIncomingTelegramNotificationUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val keywordsRepository: KeywordsRepository,
    private val logRepository: LogRepository,
    private val alarmController: AlarmController,
) : ProcessIncomingTelegramNotificationUseCase {

    override suspend operator fun invoke(payload: NotificationPayload): Boolean {
        if (payload.text.isBlank()) return false

        val selectedTelegramPackages = settingsRepository.selectedTelegramPackages.first()
        if (payload.packageName !in selectedTelegramPackages) return false

        if (!settingsRepository.isMonitoringActive.first()) return false

        val senderName = payload.title.ifBlank { payload.packageName }
        val effectiveTimestamp =
            if (payload.timestamp > 0) payload.timestamp
            else System.currentTimeMillis()

        val isAlreadyLogged = logRepository.existsBySignature(
            senderPackage = payload.packageName,
            senderName = senderName,
            messageText = payload.text,
            timestamp = effectiveTimestamp,
        )
        if (isAlreadyLogged) return false

        val isScheduleEnabled = settingsRepository.isScheduleEnabled.first()
        val isInScheduleWindow =
            isWithinScheduleWindow(
                isScheduleEnabled = isScheduleEnabled,
                startTime = settingsRepository.startTime.first(),
                endTime = settingsRepository.endTime.first(),
            )

        val isTelegramChannelFilterEnabled =
            keywordsRepository.isTelegramChannelFilterEnabled.first()
        val isChannelAllowed =
            isChannelAllowed(
                isFilterEnabled = isTelegramChannelFilterEnabled,
                title = payload.title,
                allowedChannels = keywordsRepository.telegramChannels.first(),
            )

        val matchedKeyword =
            if (isInScheduleWindow && isChannelAllowed) {
                findMatchedKeyword(
                    text = payload.text,
                    keywords = keywordsRepository.keywords.first(),
                    stopWords = keywordsRepository.stopWords.first(),
                )
            } else {
                null
            }

        addLogEntry(
            payload = payload,
            senderName = senderName,
            effectiveTimestamp = effectiveTimestamp,
            matchedKeyword = matchedKeyword ?: EMPTY_MATCHED_KEYWORD,
        )

        if (matchedKeyword != null) {
            val isAlarmRunning = alarmController.isAlarmRunning.first()
            if (isAlarmRunning) return true

            alarmController.triggerAlarm(matchedKeyword)
            return true
        }

        return false
    }

    private suspend fun addLogEntry(
        payload: NotificationPayload,
        senderName: String,
        effectiveTimestamp: Long,
        matchedKeyword: String,
    ) {
        logRepository.addEntry(
            event = AlertEvent(
                id = UUID.randomUUID().toString(),
                timestamp = effectiveTimestamp,
                senderPackage = payload.packageName,
                senderName = senderName,
                messageText = payload.text,
                matchedKeyword = matchedKeyword,
            )
        )

        val logSizeLimit = settingsRepository.logSizeLimit.first()
        logRepository.trimToLimit(logSizeLimit)
    }

    private fun findMatchedKeyword(
        text: String,
        keywords: List<String>,
        stopWords: List<String>,
    ): String? {
        if (keywords.isEmpty()) return null

        val lowerText = text.lowercase()
        val hasStopWord = stopWords.any { stopWord -> lowerText.contains(stopWord.lowercase()) }
        if (hasStopWord) return null

        return keywords.firstOrNull { keyword -> lowerText.contains(keyword.lowercase()) }
    }

    private fun isChannelAllowed(
        isFilterEnabled: Boolean,
        title: String,
        allowedChannels: List<String>,
    ): Boolean {
        if (!isFilterEnabled) return true
        if (title.isBlank()) return false
        if (allowedChannels.isEmpty()) return false

        return allowedChannels.any { channel ->
            channel.equals(title, ignoreCase = true)
        }
    }

    private fun isWithinScheduleWindow(
        isScheduleEnabled: Boolean,
        startTime: String,
        endTime: String,
    ): Boolean {
        if (!isScheduleEnabled) return true

        val formatter = DateTimeFormatter.ofPattern(TIME_PATTERN)
        val start = runCatching { LocalTime.parse(startTime, formatter) }.getOrNull() ?: return true
        val end = runCatching { LocalTime.parse(endTime, formatter) }.getOrNull() ?: return true

        if (start == end) return true

        val now = LocalTime.now()
        return if (start.isBefore(end)) {
            now >= start && now < end
        } else {
            now >= start || now < end
        }
    }

}
