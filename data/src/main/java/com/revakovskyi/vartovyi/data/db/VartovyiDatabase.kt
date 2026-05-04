package com.revakovskyi.vartovyi.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.revakovskyi.vartovyi.data.db.dao.AlertEventDao
import com.revakovskyi.vartovyi.data.db.entity.AlertEventEntity

@Database(
    entities = [AlertEventEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class VartovyiDatabase : RoomDatabase() {

    abstract fun alertEventDao(): AlertEventDao

}
