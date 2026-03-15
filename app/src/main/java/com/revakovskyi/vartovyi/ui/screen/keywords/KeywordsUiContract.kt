package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.compose.runtime.Immutable

interface KeywordsUiContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val keywords: List<String> = emptyList(),
        val stopWords: List<String> = emptyList(),
        val inputKeyword: String = "",
        val inputStopWord: String = "",
        val isTelegramChannelFilterEnabled: Boolean = false,
        val telegramChannels: List<String> = emptyList(),
        val inputTelegramChannel: String = "",
        val duplicateWord: String? = null,
    )

    sealed interface Action {
        data class UpdateKeywordInput(val value: String) : Action
        data class UpdateStopWordInput(val value: String) : Action
        data object AddKeyword : Action
        data class RemoveKeyword(val keyword: String) : Action
        data object AddStopWord : Action
        data class RemoveStopWord(val stopWord: String) : Action
        data object ToggleTelegramChannelFilter : Action
        data class UpdateTelegramChannelInput(val value: String) : Action
        data object AddTelegramChannel : Action
        data class RemoveTelegramChannel(val channel: String) : Action
        data object DismissDuplicateWordDialog : Action
    }

    sealed interface Event {
        data object KeywordAdded : Event
        data object StopWordAdded : Event
        data object TelegramChannelAdded : Event
    }

}
