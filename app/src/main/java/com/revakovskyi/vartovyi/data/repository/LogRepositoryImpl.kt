package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.data.db.dao.AlertEventDao
import com.revakovskyi.vartovyi.data.mappers.toDomain
import com.revakovskyi.vartovyi.data.mappers.toEntity
import com.revakovskyi.vartovyi.domain.model.AlertEvent
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

class LogRepositoryImpl(
    private val alertEventDao: AlertEventDao,
) : LogRepository {

    override val logEntries: Flow<List<AlertEvent>> = alertEventDao.getAll()
        .map { entities -> entities.map { it.toDomain() } }

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
