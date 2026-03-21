package com.revakovskyi.vartovyi.domain.usecase.notification

import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.AlertEventStatus
import com.revakovskyi.vartovyi.domain.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val EMPTY_MATCHED_KEYWORD = ""
private const val TIME_PATTERN = "HH:mm"
private const val TELEGRAM_PACKAGE_NAME = "org.telegram.messenger"

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

    private val triggerDecisionMutex = Mutex()

    override suspend operator fun invoke(payload: NotificationPayload): Boolean {
        if (payload.text.isBlank()) return false

        if (payload.packageName != TELEGRAM_PACKAGE_NAME) return false

        if (!settingsRepository.isMonitoringActive.first()) return false

        val senderName = payload.title.ifBlank { payload.packageName }
        val effectiveTimestamp =
            if (payload.timestamp > 0) payload.timestamp
            else System.currentTimeMillis()

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

        if (matchedKeyword == null) {
            addLogEntry(
                payload = payload,
                senderName = senderName,
                effectiveTimestamp = effectiveTimestamp,
                matchedKeyword = EMPTY_MATCHED_KEYWORD,
                status = AlertEventStatus.SKIPPED,
            )
            return false
        }

        return triggerDecisionMutex.withLock {
            val isAlarmRunning = alarmController.isAlarmRunning.first()
            val cooldownUntilEpochMillis =
                settingsRepository.alarmRetriggerCooldownUntilEpochMillis.first()
            val currentEpochMillis = System.currentTimeMillis()
            val isCooldownActive = cooldownUntilEpochMillis > currentEpochMillis

            if (isAlarmRunning || isCooldownActive) {
                addLogEntry(
                    payload = payload,
                    senderName = senderName,
                    effectiveTimestamp = effectiveTimestamp,
                    matchedKeyword = matchedKeyword,
                    status = AlertEventStatus.SKIPPED_COOLDOWN,
                )
                return@withLock true
            }

            val isLogInserted = addLogEntry(
                payload = payload,
                senderName = senderName,
                effectiveTimestamp = effectiveTimestamp,
                matchedKeyword = matchedKeyword,
                status = AlertEventStatus.ALARM_TRIGGERED,
            )
            if (!isLogInserted) return@withLock true

            val cooldownDurationMillis =
                settingsRepository.alarmRetriggerCooldownDurationMillis.first()
            settingsRepository.setAlarmRetriggerCooldownUntilEpochMillis(
                untilEpochMillis = currentEpochMillis + cooldownDurationMillis,
            )

            alarmController.triggerAlarm(
                sourceChannelName = senderName,
                sourceMessageText = payload.text,
            )
            return@withLock true
        }
    }

    private suspend fun addLogEntry(
        payload: NotificationPayload,
        senderName: String,
        effectiveTimestamp: Long,
        matchedKeyword: String,
        status: AlertEventStatus,
    ): Boolean {
        val isInserted = logRepository.addEntry(
            event = AlertEvent(
                id = UUID.randomUUID().toString(),
                timestamp = effectiveTimestamp,
                senderPackage = payload.packageName,
                senderName = senderName,
                messageText = payload.text,
                matchedKeyword = matchedKeyword,
                status = status,
            )
        )
        if (!isInserted) return false

        val logSizeLimit = settingsRepository.logSizeLimit.first()
        logRepository.trimToLimit(logSizeLimit)
        return true
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

        return keywords.asSequence()
            .map { keyword -> TriggerKeywordRule.fromStorageValue(keyword) }
            .firstOrNull { keywordRule -> keywordRule.matches(text) }
            ?.displayValue
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
            now in start..<end
        } else {
            now !in end..<start
        }
    }

}
