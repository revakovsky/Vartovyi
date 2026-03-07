package com.revakovskyi.vartovyi.domain.model

data class ScheduleSettings(
    val isScheduleEnabled: Boolean,
    val startTime: String,
    val endTime: String,
    val alarmDurationSeconds: Int,
    val isVibrationEnabled: Boolean,
)
