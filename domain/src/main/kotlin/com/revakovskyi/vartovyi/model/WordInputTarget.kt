package com.revakovskyi.vartovyi.model

/** Identifies which Keywords-screen input field a raw word input comes from. */
sealed interface WordInputTarget {

    data class TriggerKeyword(val selectedType: TriggerKeywordRuleType) : WordInputTarget

    data object StopWord : WordInputTarget

    data object TelegramChannel : WordInputTarget

}
