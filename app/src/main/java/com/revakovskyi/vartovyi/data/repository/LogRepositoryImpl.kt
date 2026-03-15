package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.data.db.dao.AlertEventDao
import com.revakovskyi.vartovyi.data.mappers.toDomain
import com.revakovskyi.vartovyi.data.mappers.toEntity
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LogRepositoryImpl(
    private val alertEventDao: AlertEventDao,
) : LogRepository {

    override val logEntries: Flow<List<AlertEvent>> = alertEventDao.getAll()
        .map { entities -> entities.map { it.toDomain() } }

    override suspend fun existsBySignature(
        senderPackage: String,
        senderName: String,
        messageText: String,
        timestamp: Long,
    ): Boolean {
        return alertEventDao.existsBySignature(
            senderPackage = senderPackage,
            senderName = senderName,
            messageText = messageText,
            timestamp = timestamp,
        )
    }

    override suspend fun addEntry(event: AlertEvent) {
        alertEventDao.insert(event.toEntity())
    }

    override suspend fun trimToLimit(limit: Int) {
        alertEventDao.trimToLimit(limit)
    }

    override suspend fun clearLog() {
        alertEventDao.deleteAll()
    }

}
