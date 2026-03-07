package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.compose.runtime.Immutable

interface KeywordsUiContract {

    @Immutable
    data class State(
        val keywords: List<String> = emptyList(),
        val stopWords: List<String> = emptyList(),
        val inputKeyword: String = "",
        val inputStopWord: String = "",
    )

    sealed interface Action {
        data class UpdateKeywordInput(val value: String) : Action
        data class UpdateStopWordInput(val value: String) : Action
        data object AddKeyword : Action
        data class RemoveKeyword(val keyword: String) : Action
        data object AddStopWord : Action
        data class RemoveStopWord(val stopWord: String) : Action
        data object NavigateBack : Action
    }

    sealed interface Event {
        data object KeywordAdded : Event
        data object StopWordAdded : Event
        data object NavigateBack : Event
        data class Error(val message: String) : Event
    }

}
