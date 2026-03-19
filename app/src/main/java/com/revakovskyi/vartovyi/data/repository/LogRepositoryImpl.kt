package com.revakovskyi.vartovyi.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.revakovskyi.vartovyi.data.db.dao.AlertEventDao
import com.revakovskyi.vartovyi.data.mappers.toDomain
import com.revakovskyi.vartovyi.data.mappers.toEntity
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.AlertEventStatus
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private const val INSERT_CONFLICT_RESULT = -1L
private const val SIGNATURE_HASH_ALGORITHM = "SHA-256"
private const val SIGNATURE_SEPARATOR = "|"
private const val WHITESPACE_REGEX = "\\s+"
private const val SINGLE_SPACE = " "
private const val HEX_BYTE_FORMAT = "%02x"
private val ALARM_TRIGGERED_STATUS_VALUE = AlertEventStatus.ALARM_TRIGGERED.name
private const val PAGE_SIZE = 50
private const val PREFETCH_DISTANCE = 20

class LogRepositoryImpl(
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

    override suspend fun addEntry(event: AlertEvent): Boolean {
        val signature = buildSignature(event)
        val insertResult = alertEventDao.insert(event.toEntity(signature = signature))
        return insertResult != INSERT_CONFLICT_RESULT
    }

    override suspend fun trimToLimit(limit: Int) {
        alertEventDao.trimToLimit(limit)
    }

    override suspend fun clearLog() {
        alertEventDao.deleteAll()
    }

    private fun buildSignature(event: AlertEvent): String {
        val normalizedMessageText = event.messageText
            .trim()
            .lowercase()
            .replace(WHITESPACE_REGEX.toRegex(), SINGLE_SPACE)
        val normalizedSenderName = event.senderName.trim().lowercase()
        val signaturePayload = buildString {
            append(event.senderPackage)
            append(SIGNATURE_SEPARATOR)
            append(normalizedSenderName)
            append(SIGNATURE_SEPARATOR)
            append(normalizedMessageText)
        }
        val digestBytes = MessageDigest.getInstance(SIGNATURE_HASH_ALGORITHM)
            .digest(signaturePayload.toByteArray())
        return digestBytes.joinToString(separator = "") { byte -> HEX_BYTE_FORMAT.format(byte) }
    }

}
