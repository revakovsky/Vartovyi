package com.revakovskyi.vartovyi.data.mappers

import com.revakovskyi.vartovyi.data.db.entity.AlertEventEntity
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.AlertEventStatus

internal fun AlertEventEntity.toDomain(): AlertEvent = AlertEvent(
    id = id,
    timestamp = timestamp,
    senderPackage = senderPackage,
    senderName = senderName,
    messageText = messageText,
    matchedKeyword = matchedKeyword,
    status = runCatching { AlertEventStatus.valueOf(status) }
        .getOrDefault(AlertEventStatus.SKIPPED),
)

internal fun AlertEvent.toEntity(signature: String): AlertEventEntity = AlertEventEntity(
    id = id,
    timestamp = timestamp,
    senderPackage = senderPackage,
    senderName = senderName,
    messageText = messageText,
    matchedKeyword = matchedKeyword,
    status = status.name,
    signature = signature,
)
