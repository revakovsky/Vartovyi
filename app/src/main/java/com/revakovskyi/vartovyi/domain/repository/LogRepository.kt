package com.revakovskyi.vartovyi.domain.repository

import androidx.paging.PagingData
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    val logEntries: Flow<PagingData<AlertEvent>>
    val lastAlarmTriggeredEvent: Flow<AlertEvent?>

    suspend fun getEntryIndexById(eventId: String): Int
    suspend fun addEntry(event: AlertEvent): Boolean
    suspend fun trimToLimit(limit: Int)
    suspend fun clearLog()

}
