package com.revakovskyi.vartovyi.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.revakovskyi.vartovyi.data.db.entity.AlertEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertEventDao {

    @Query("SELECT * FROM alert_events ORDER BY timestamp DESC")
    fun getAll(): Flow<List<AlertEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AlertEventEntity)

    @Query("DELETE FROM alert_events WHERE id NOT IN (SELECT id FROM alert_events ORDER BY timestamp DESC LIMIT :limit)")
    suspend fun trimToLimit(limit: Int)

    @Query("DELETE FROM alert_events")
    suspend fun deleteAll()

}
