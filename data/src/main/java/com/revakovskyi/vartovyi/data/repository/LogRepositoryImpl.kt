package com.revakovskyi.vartovyi.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.revakovskyi.vartovyi.data.db.dao.AlertEventDao
import com.revakovskyi.vartovyi.data.mappers.toDomain
import com.revakovskyi.vartovyi.data.mappers.toEntity
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.AlertEventStatus
import com.revakovskyi.vartovyi.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.text.Normalizer

private const val INSERT_CONFLICT_RESULT = -1L
private const val SIGNATURE_HASH_ALGORITHM = "SHA-256"
private const val SIGNATURE_SEPARATOR = "|"
private const val WHITESPACE_REGEX = "\\s+"
private const val SINGLE_SPACE = " "
private const val HEX_BYTE_FORMAT = "%02x"
private const val NON_BREAKING_SPACE = ' '
private const val INVISIBLE_UNICODE_CHARS_REGEX = "[\\u200B-\\u200D\\uFEFF\\uFE00-\\uFE0F]"
private const val DEDUP_TIME_WINDOW_MILLIS = 60_000L
private val ALARM_TRIGGERED_STATUS_VALUE = AlertEventStatus.ALARM_TRIGGERED.name
private const val PAGE_SIZE = 50
private const val PREFETCH_DISTANCE = 20

internal class LogRepositoryImpl(
    private val alertEventDao: AlertEventDao,
) : LogRepository {

    override val logEntries: Flow<PagingData<AlertEvent>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = true,
        ),
        pagingSourceFactory = {
            alertEventDao.getAllPaged()
        },
    ).flow.map { pagingData ->
        pagingData.map { entity -> entity.toDomain() }
    }

    override val lastAlarmTriggeredEvent: Flow<AlertEvent?> =
        alertEventDao.getLastByStatus(ALARM_TRIGGERED_STATUS_VALUE)
            .map { entity -> entity?.toDomain() }

    override suspend fun getEntryIndexById(eventId: String): Int {
        return alertEventDao.getIndexById(eventId)
    }

    override suspend fun addEntryAndTrimToLimit(
        event: AlertEvent,
        notificationKey: String,
        postTime: Long,
        conversationMessagesCount: Int?,
        limit: Int,
    ): Boolean {
        val signature = buildSignature(
            event = event,
            notificationKey = notificationKey,
            postTime = postTime,
            conversationMessagesCount = conversationMessagesCount,
        )
        val insertResult = alertEventDao.insertOrUpdateAndTrimToLimit(
            entity = event.toEntity(signature = signature),
            limit = limit,
        )
        return insertResult != INSERT_CONFLICT_RESULT
    }

    override suspend fun clearLog() {
        alertEventDao.deleteAll()
    }

    private fun buildSignature(
        event: AlertEvent,
        notificationKey: String,
        postTime: Long,
        conversationMessagesCount: Int?,
    ): String {
        val signaturePayload = if (conversationMessagesCount != null) {
            buildConversationSignature(
                senderPackage = event.senderPackage,
                notificationKey = notificationKey,
                conversationMessagesCount = conversationMessagesCount,
            )
        } else {
            buildLegacyTextSignature(
                event = event,
                notificationKey = notificationKey,
                postTime = postTime,
            )
        }

        val digestBytes = MessageDigest
            .getInstance(SIGNATURE_HASH_ALGORITHM)
            .digest(signaturePayload.toByteArray())
        return digestBytes.joinToString(separator = "") { byte -> HEX_BYTE_FORMAT.format(byte) }
    }

    private fun buildConversationSignature(
        senderPackage: String,
        notificationKey: String,
        conversationMessagesCount: Int,
    ): String {
        return buildString {
            append(senderPackage)
            append(SIGNATURE_SEPARATOR)
            append(notificationKey)
            append(SIGNATURE_SEPARATOR)
            append(conversationMessagesCount)
        }
    }

    private fun buildLegacyTextSignature(
        event: AlertEvent,
        notificationKey: String,
        postTime: Long,
    ): String {
        val normalizedMessageText = normalizeForSignature(event.messageText)
        val normalizedSenderName = normalizeForSignature(event.senderName)
        val timeBucket = postTime / DEDUP_TIME_WINDOW_MILLIS

        return buildString {
            append(event.senderPackage)
            append(SIGNATURE_SEPARATOR)
            append(notificationKey)
            append(SIGNATURE_SEPARATOR)
            append(normalizedSenderName)
            append(SIGNATURE_SEPARATOR)
            append(normalizedMessageText)
            append(SIGNATURE_SEPARATOR)
            append(timeBucket)
        }
    }

    private fun normalizeForSignature(rawText: String): String {
        return Normalizer.normalize(rawText, Normalizer.Form.NFC)
            .replace(NON_BREAKING_SPACE, ' ')
            .replace(INVISIBLE_UNICODE_CHARS_REGEX.toRegex(), "")
            .trim()
            .lowercase()
            .replace(WHITESPACE_REGEX.toRegex(), SINGLE_SPACE)
    }

}
