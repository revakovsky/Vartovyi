package com.revakovskyi.vartovyi.utils

import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType

fun parseTriggerKeywordRuleFromStorage(storageValue: String): TriggerKeywordRule {
    val trimmedValue = storageValue.trim()

    val isQuotedPhrase = trimmedValue.length >= 2 &&
            trimmedValue.startsWith(KeywordRuleFormat.QUOTE) &&
            trimmedValue.endsWith(KeywordRuleFormat.QUOTE)

    if (isQuotedPhrase) {
        val phraseValue = trimmedValue
            .removePrefix(KeywordRuleFormat.QUOTE)
            .removeSuffix(KeywordRuleFormat.QUOTE)
            .trim()
        val displayValue = "${KeywordRuleFormat.QUOTE}$phraseValue${KeywordRuleFormat.QUOTE}"
        return TriggerKeywordRule(
            storageValue = displayValue,
            type = TriggerKeywordRuleType.PHRASE,
            terms = listOf(phraseValue),
            displayValue = displayValue,
        )
    }

    if (trimmedValue.contains(KeywordRuleFormat.ALL_WORDS_SEPARATOR)) {
        val allWordsTerms = trimmedValue.split(KeywordRuleFormat.ALL_WORDS_SEPARATOR)
            .map { value -> value.trim() }
            .filter { value -> value.isNotBlank() }
        val displayValue = allWordsTerms.joinToString(
            separator = KeywordRuleFormat.DISPLAY_ALL_WORDS_SEPARATOR,
        )
        val rebuiltStorageValue = buildAllWordsStorageValue(allWordsTerms)
        return TriggerKeywordRule(
            storageValue = rebuiltStorageValue,
            type = TriggerKeywordRuleType.ALL_WORDS,
            terms = allWordsTerms,
            displayValue = displayValue,
        )
    }

    return TriggerKeywordRule(
        storageValue = trimmedValue,
        type = TriggerKeywordRuleType.WORD,
        terms = listOf(trimmedValue),
        displayValue = trimmedValue,
    )
}

private fun buildAllWordsStorageValue(terms: List<String>): String {
    val normalizedTerms = terms.filter { term -> term.isNotBlank() }
    if (normalizedTerms.isEmpty()) return KeywordRuleFormat.EMPTY_VALUE

    val baseValue = normalizedTerms.joinToString(
        separator = KeywordRuleFormat.ALL_WORDS_SEPARATOR,
    )
    return if (normalizedTerms.size == 1) {
        "$baseValue${KeywordRuleFormat.ALL_WORDS_SEPARATOR}"
    } else {
        baseValue
    }
}
