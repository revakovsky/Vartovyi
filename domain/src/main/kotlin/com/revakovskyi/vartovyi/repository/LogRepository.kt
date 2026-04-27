package com.revakovskyi.vartovyi.repository

import androidx.paging.PagingData
import com.revakovskyi.vartovyi.model.AlertEvent
import kotlinx.coroutines.flow.Flow

interface LogRepository {

    val logEntries: Flow<PagingData<AlertEvent>>
    val lastAlarmTriggeredEvent: Flow<AlertEvent?>

    suspend fun getEntryIndexById(eventId: String): Int
    suspend fun addEntryAndTrimToLimit(event: AlertEvent, limit: Int): Boolean
    suspend fun clearLog()

}
