package com.revakovskyi.vartovyi.model

data class NotificationPayload(
    val packageName: String,
    val notificationKey: String,
    val title: String,
    val text: String,
    val timestamp: Long,
)

