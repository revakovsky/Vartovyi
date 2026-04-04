package com.revakovskyi.vartovyi.ui.screen.settings

import androidx.compose.runtime.Immutable

interface SettingsUiContract {

    enum class SettingsSection {
        DATA, SOUND, SCHEDULE, LEGAL,
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val isScheduleEnabled: Boolean = false,
        val startTime: String = "22:00",
        val endTime: String = "07:00",
        val alarmDurationSeconds: Int = 60,
        val alarmVolumePercent: Int = 100,
        val alarmSoundUri: String = "",
        val logSizeLimit: Int = 500,
        val alarmRetriggerCooldownDurationMillis: Long = 5 * 60 * 1000L,
        val isMonitoringActive: Boolean = false,
        val isAlarmRunning: Boolean = false,
        val expandedSection: SettingsSection? = null,
    )

    sealed interface Action {
        data class SetScheduleEnabled(val enabled: Boolean) : Action
        data class SetStartTime(val time: String) : Action
        data class SetEndTime(val time: String) : Action
        data class SetAlarmDuration(val seconds: Int) : Action
        data class SetAlarmVolume(val percent: Int) : Action
        data class SetAlarmSoundUri(val uri: String) : Action
        data class SetLogSizeLimit(val limit: Int) : Action
        data class SetAlarmRetriggerCooldownDurationMillis(val durationMillis: Long) : Action
        data class ToggleTestAlarm(
            val sourceChannelName: String,
            val sourceMessageText: String,
        ) : Action

        data object StartExternalPickerNavigation : Action
        data class ToggleSection(val section: SettingsSection) : Action
        data object CollapseSectionsOnScreenStop : Action
        data object OpenPrivacyPolicy : Action
        data object OpenTermsOfUse : Action
    }

    sealed interface Event {
        data object ShowDisableMonitoringForTestAlarm : Event
        data class OpenUrl(val url: String) : Event
    }

}
