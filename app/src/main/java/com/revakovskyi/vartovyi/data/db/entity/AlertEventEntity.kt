package com.revakovskyi.vartovyi.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alert_events",
    indices = [
        Index(
            value = ["signature"],
            unique = true,
        ),
    ],
)
data class AlertEventEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val senderPackage: String,
    val senderName: String,
    val messageText: String,
    val matchedKeyword: String,
    val status: String,
    val signature: String,
)
