package com.revakovskyi.vartovyi.data.mappers

import com.revakovskyi.vartovyi.data.db.entity.AlertEventEntity
import com.revakovskyi.vartovyi.domain.model.AlertEvent

fun AlertEventEntity.toDomain(): AlertEvent = AlertEvent(
    id = id,
    timestamp = timestamp,
    senderPackage = senderPackage,
    senderName = senderName,
    messageText = messageText,
    matchedKeyword = matchedKeyword,
)

fun AlertEvent.toEntity(): AlertEventEntity = AlertEventEntity(
    id = id,
    timestamp = timestamp,
    senderPackage = senderPackage,
    senderName = senderName,
    messageText = messageText,
    matchedKeyword = matchedKeyword,
)
