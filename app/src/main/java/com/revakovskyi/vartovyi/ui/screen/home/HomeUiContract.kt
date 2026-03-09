package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.MonitoringState

interface HomeUiContract {

    @Immutable
    data class State(
        val monitoringState: MonitoringState = MonitoringState.INACTIVE,
        val isScheduleEnabled: Boolean = false,
        val startTime: String = "22:00",
        val endTime: String = "07:00",
        val lastAlertEvent: AlertEvent? = null,
        val isListenerServiceActive: Boolean = false,
        val keywords: List<String> = emptyList(),
    )

    sealed interface Action {
        data object ToggleMonitoring : Action
        data object TestAlarm : Action
        data object NavigateToKeywords : Action
        data object NavigateToLog : Action
        data object NavigateToSettings : Action
        data object NavigateToPermissions : Action
    }

    sealed interface Event {
        data object MonitoringStarted : Event
        data object MonitoringStopped : Event
        data object NavigateToKeywords : Event
        data object NavigateToLog : Event
        data object NavigateToSettings : Event
        data object NavigateToPermissions : Event
        data class Error(val message: String) : Event
    }

}
