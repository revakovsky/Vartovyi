package com.revakovskyi.vartovyi.model

data class ScheduleSettings(
    val isScheduleEnabled: Boolean,
    val startTime: String,
    val endTime: String,
    val alarmDurationSeconds: Int,
    val alarmVolumePercent: Int,
    val alarmSoundUri: String,
)
