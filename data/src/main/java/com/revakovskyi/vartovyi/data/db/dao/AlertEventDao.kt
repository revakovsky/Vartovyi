package com.revakovskyi.vartovyi.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.revakovskyi.vartovyi.data.db.entity.AlertEventEntity
import kotlinx.coroutines.flow.Flow

private const val INSERT_CONFLICT_RESULT = -1L

@Dao
internal interface AlertEventDao {

    @Query("SELECT * FROM alert_events ORDER BY timestamp DESC, id DESC")
    fun getAllPaged(): PagingSource<Int, AlertEventEntity>

    @Query("SELECT * FROM alert_events WHERE status = :status ORDER BY timestamp DESC, id DESC LIMIT 1")
    fun getLastByStatus(status: String): Flow<AlertEventEntity?>

    @Query(
        """
        SELECT CASE
            WHEN EXISTS(SELECT 1 FROM alert_events WHERE id = :eventId) THEN (
                SELECT COUNT(*)
                FROM alert_events
                WHERE
                    timestamp > (SELECT timestamp FROM alert_events WHERE id = :eventId)
                    OR (
                        timestamp = (SELECT timestamp FROM alert_events WHERE id = :eventId)
                        AND id > :eventId
                    )
            )
            ELSE -1
        END
        """
    )
    suspend fun getIndexById(eventId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: AlertEventEntity): Long

    @Transaction
    suspend fun insertAndTrimToLimit(entity: AlertEventEntity, limit: Int): Long {
        val insertResult = insert(entity)
        if (insertResult == INSERT_CONFLICT_RESULT) {
            return insertResult
        }

        trimToLimit(limit)
        return insertResult
    }

    @Query("DELETE FROM alert_events WHERE id NOT IN (SELECT id FROM alert_events ORDER BY timestamp DESC, id DESC LIMIT :limit)")
    suspend fun trimToLimit(limit: Int)

    @Query("DELETE FROM alert_events")
    suspend fun deleteAll()

}
