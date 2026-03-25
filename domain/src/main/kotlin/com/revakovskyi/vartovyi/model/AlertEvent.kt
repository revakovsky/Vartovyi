package com.revakovskyi.vartovyi.model

import kotlinx.serialization.Serializable

enum class AlertEventStatus {
    ALARM_TRIGGERED,
    SKIPPED,
    SKIPPED_COOLDOWN,
}

@Serializable
data class AlertEvent(
    val id: String,
    val timestamp: Long,
    val senderPackage: String,
    val senderName: String,
    val messageText: String,
    val matchedKeyword: String,
    val status: AlertEventStatus,
)
