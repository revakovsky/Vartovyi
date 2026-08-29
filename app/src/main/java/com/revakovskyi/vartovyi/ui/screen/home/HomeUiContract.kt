package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.ui.util.unwrapPhraseQuotes
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage

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
    ) {
        /**
         * True while trigger words are unset: list is empty or still equals
         * [DEFAULT_KEYWORDS_SEED]
         */
        val needsKeywordsAttention: Boolean
            get() = keywords.isEmpty() || keywords.all { keyword -> keyword in DEFAULT_KEYWORDS_SEED }

        val displayKeywords: List<String>
            get() = keywords.map { keyword ->
                parseTriggerKeywordRuleFromStorage(keyword).displayValue.unwrapPhraseQuotes()
            }
    }

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
