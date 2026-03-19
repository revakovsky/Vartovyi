package com.revakovskyi.vartovyi.ui.screen.settings

import androidx.compose.runtime.Immutable

interface SettingsUiContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val isScheduleEnabled: Boolean = false,
        val startTime: String = "22:00",
        val endTime: String = "07:00",
        val alarmDurationSeconds: Int = 60,
        val alarmVolumePercent: Int = 100,
        val alarmSoundUri: String = "",
        val isVibrationEnabled: Boolean = true,
        val selectedTelegramPackages: Set<String> = setOf("org.telegram.messenger"),
        val logSizeLimit: Int = 500,
        val isMonitoringActive: Boolean = false,
        val isAlarmRunning: Boolean = false,
    )

    sealed interface Action {
        data class SetScheduleEnabled(val enabled: Boolean) : Action
        data class SetStartTime(val time: String) : Action
        data class SetEndTime(val time: String) : Action
        data class SetAlarmDuration(val seconds: Int) : Action
        data class SetAlarmVolume(val percent: Int) : Action
        data class SetAlarmSoundUri(val uri: String) : Action
        data class SetVibrationEnabled(val enabled: Boolean) : Action
        data class SetTelegramPackages(val packages: Set<String>) : Action
        data class SetLogSizeLimit(val limit: Int) : Action
        data object NavigateBack : Action
        data class ToggleTestAlarm(
            val sourceChannelName: String,
            val sourceMessageText: String,
        ) : Action
    }

    sealed interface Event {
        data object SettingsSaved : Event
        data object NavigateBack : Event
        data object ShowDisableMonitoringForTestAlarm : Event
        data class Error(val message: String) : Event
    }

}
