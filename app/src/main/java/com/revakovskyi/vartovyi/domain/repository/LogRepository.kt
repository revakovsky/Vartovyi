package com.revakovskyi.vartovyi.domain.repository

import com.revakovskyi.vartovyi.domain.model.AlertEvent
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    val logEntries: Flow<List<AlertEvent>>

    suspend fun addEntry(event: AlertEvent): Boolean
    suspend fun trimToLimit(limit: Int)
    suspend fun clearLog()

}
