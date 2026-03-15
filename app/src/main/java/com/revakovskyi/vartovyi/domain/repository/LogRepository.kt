package com.revakovskyi.vartovyi.domain.repository

import com.revakovskyi.vartovyi.domain.model.AlertEvent
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    val logEntries: Flow<List<AlertEvent>>

    suspend fun existsBySignature(
        senderPackage: String,
        senderName: String,
        messageText: String,
        timestamp: Long,
    ): Boolean

    suspend fun addEntry(event: AlertEvent)
    suspend fun trimToLimit(limit: Int)
    suspend fun clearLog()

}
