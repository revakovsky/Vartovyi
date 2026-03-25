package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.MonitoringState

interface HomeUiContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val monitoringState: MonitoringState = MonitoringState.INACTIVE,
        val isScheduleEnabled: Boolean = false,
        val startTime: String = "22:00",
        val endTime: String = "07:00",
        val lastAlertEvent: AlertEvent? = null,
        val alarmRetriggerCooldownMillis: Long = 0L,
        val isListenerServiceActive: Boolean = false,
        val keywords: List<String> = emptyList(),
    )

    sealed interface Action {
        data object ToggleMonitoring : Action
        data object NavigateToKeywords : Action
        data class NavigateToLog(val logEntryId: String? = null) : Action
    }

    sealed interface Event {
        data object NavigateToKeywords : Event
        data class NavigateToLog(val logEntryId: String? = null) : Event
    }

}
