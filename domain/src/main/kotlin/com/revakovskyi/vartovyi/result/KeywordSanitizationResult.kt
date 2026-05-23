package com.revakovskyi.vartovyi.result

import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType

sealed interface KeywordSanitizationResult {

    data object Empty : KeywordSanitizationResult
    data object MultiLineDetected : KeywordSanitizationResult
    data object TermTooShort : KeywordSanitizationResult

    data class Sanitized(
        val effectiveType: TriggerKeywordRuleType,
        val storageValue: String,
        val normalizedRawInput: String,
    ) : KeywordSanitizationResult

}
