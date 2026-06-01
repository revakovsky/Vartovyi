package com.revakovskyi.vartovyi.usecase.notification

import com.revakovskyi.vartovyi.contract.ElapsedRealtimeProvider
import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.AlertEventStatus
import com.revakovskyi.vartovyi.model.NotificationPayload
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.LogRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val EMPTY_STRING = ""
private const val EMPTY_MATCHED_KEYWORD = ""
private const val TIME_PATTERN = "HH:mm"
private const val UNICODE_VARIATION_SELECTOR_15 = '\uFE0E'
private const val UNICODE_VARIATION_SELECTOR_16 = '\uFE0F'
private const val ZERO_WIDTH_JOINER = '\u200D'
private const val ZERO_WIDTH_NON_JOINER = '\u200C'
private const val WORD_OR_DIGIT_REGEX = "[\\p{L}\\p{N}]"
private const val NON_WORD_OR_DIGIT_PREFIX_REGEX = "^[^\\p{L}\\p{N}]+"
private const val NON_WORD_OR_DIGIT_SUFFIX_REGEX = "[^\\p{L}\\p{N}]+$"

private const val TELEGRAM_PACKAGE_OFFICIAL = "org.telegram.messenger"
private const val TELEGRAM_PACKAGE_OFFICIAL_WEB = "org.telegram.messenger.web"
private const val TELEGRAM_PACKAGE_X = "org.thunderdog.challegram"
private const val TELEGRAM_PACKAGE_NEKO_X = "com.nekox.messenger"

private val TELEGRAM_PACKAGES = setOf(
    TELEGRAM_PACKAGE_OFFICIAL,
    TELEGRAM_PACKAGE_OFFICIAL_WEB,
    TELEGRAM_PACKAGE_X,
    TELEGRAM_PACKAGE_NEKO_X,
)
private val nonWordOrDigitPrefixRegex = Regex(NON_WORD_OR_DIGIT_PREFIX_REGEX)
private val nonWordOrDigitSuffixRegex = Regex(NON_WORD_OR_DIGIT_SUFFIX_REGEX)
private val wordOrDigitRegex = Regex(WORD_OR_DIGIT_REGEX)

interface ProcessIncomingTelegramNotificationUseCase {
    suspend operator fun invoke(payload: NotificationPayload): Boolean
}

class ProcessIncomingTelegramNotificationUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val keywordsRepository: KeywordsRepository,
    private val logRepository: LogRepository,
    private val alarmController: AlarmController,
    private val elapsedRealtimeProvider: ElapsedRealtimeProvider,
) : ProcessIncomingTelegramNotificationUseCase {

    private val triggerDecisionMutex = Mutex()

    override suspend operator fun invoke(payload: NotificationPayload): Boolean {
        if (payload.text.isBlank()) return false
        if (payload.packageName !in TELEGRAM_PACKAGES) return false
        if (!settingsRepository.isMonitoringActive.first()) return false

        val senderName = payload.title.ifBlank { payload.packageName }
        val effectiveTimestamp =
            if (payload.timestamp > 0) payload.timestamp
            else System.currentTimeMillis()

        val isInScheduleWindow = isWithinScheduleWindow(
            isScheduleEnabled = settingsRepository.isScheduleEnabled.first(),
            startTime = settingsRepository.startTime.first(),
            endTime = settingsRepository.endTime.first(),
        )
        val isChannelAllowed = isChannelAllowed(
            isFilterEnabled = keywordsRepository.isTelegramChannelFilterEnabled.first(),
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
            } else null

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

        return handleAlarmTrigger(
            payload = payload,
            senderName = senderName,
            effectiveTimestamp = effectiveTimestamp,
            matchedKeyword = matchedKeyword,
        )
    }

    private suspend fun handleAlarmTrigger(
        payload: NotificationPayload,
        senderName: String,
        effectiveTimestamp: Long,
        matchedKeyword: String,
    ): Boolean {
        return triggerDecisionMutex.withLock {
            val isAlarmRunning = alarmController.isAlarmRunning.first()
            val cooldownUntilElapsedRealtimeMillis =
                settingsRepository.alarmRetriggerCooldownUntilElapsedRealtimeMillis.first()
            val currentElapsedRealtimeMillis = elapsedRealtimeProvider.now()
            val isCooldownActive =
                cooldownUntilElapsedRealtimeMillis > currentElapsedRealtimeMillis

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
            settingsRepository.setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(
                untilElapsedRealtimeMillis = currentElapsedRealtimeMillis + cooldownDurationMillis,
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
        val logSizeLimit = settingsRepository.logSizeLimit.first()
        return logRepository.addEntryAndTrimToLimit(
            event = AlertEvent(
                id = UUID.randomUUID().toString(),
                timestamp = effectiveTimestamp,
                senderPackage = payload.packageName,
                senderName = senderName,
                messageText = payload.text,
                matchedKeyword = matchedKeyword,
                status = status,
            ),
            notificationKey = payload.notificationKey,
            limit = logSizeLimit,
        )
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
            .map { keyword -> parseTriggerKeywordRuleFromStorage(keyword) }
            .firstOrNull { keywordRule -> keywordRule.matches(text) }
            ?.displayValue
    }

    /** Fail-open: a disabled filter, or an enabled one with no channels, allows every notification. */
    private fun isChannelAllowed(
        isFilterEnabled: Boolean,
        title: String,
        allowedChannels: List<String>,
    ): Boolean {
        if (!isFilterEnabled || allowedChannels.isEmpty()) return true

        if (title.isBlank()) return false

        val normalizedTitle = normalizeChannelNameForComparison(title)
        if (normalizedTitle.isBlank()) return false

        return allowedChannels.any { channel ->
            val normalizedChannel = normalizeChannelNameForComparison(channel)
            normalizedChannel.isNotBlank() &&
                    normalizedChannel.equals(normalizedTitle, ignoreCase = true)
        }
    }

    private fun normalizeChannelNameForComparison(rawChannelName: String): String {
        val cleanedChannelName = rawChannelName
            .trim()
            .replace(UNICODE_VARIATION_SELECTOR_15.toString(), EMPTY_STRING)
            .replace(UNICODE_VARIATION_SELECTOR_16.toString(), EMPTY_STRING)
            .replace(ZERO_WIDTH_JOINER.toString(), EMPTY_STRING)
            .replace(ZERO_WIDTH_NON_JOINER.toString(), EMPTY_STRING)

        val withoutDecorativePrefix = cleanedChannelName.replace(
            nonWordOrDigitPrefixRegex,
            EMPTY_STRING,
        )
        val withoutDecorativeEdges = withoutDecorativePrefix.replace(
            nonWordOrDigitSuffixRegex,
            EMPTY_STRING,
        )

        return if (wordOrDigitRegex.containsMatchIn(withoutDecorativeEdges)) {
            withoutDecorativeEdges.trim()
        } else {
            cleanedChannelName
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
