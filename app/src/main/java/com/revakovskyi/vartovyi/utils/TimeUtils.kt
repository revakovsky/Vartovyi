package com.revakovskyi.vartovyi.utils

import java.util.Calendar

private const val SECONDS_IN_MINUTE = 60

object TimeUtils {

    fun isCurrentTimeInRange(startTime: String, endTime: String): Boolean {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minutes = now.get(Calendar.MINUTE)
        val currentMinutes = hour * SECONDS_IN_MINUTE + minutes
        val startMinutes = parseTimeToMinutes(startTime)
        val endMinutes = parseTimeToMinutes(endTime)
        return if (startMinutes <= endMinutes) {
            currentMinutes in startMinutes..endMinutes
        } else {
            currentMinutes !in (endMinutes + 1)..<startMinutes
        }
    }

    private fun parseTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return hours * SECONDS_IN_MINUTE + minutes
    }

}
