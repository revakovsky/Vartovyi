package com.revakovskyi.vartovyi.model

import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.utils.normalizeApostrophes
import com.revakovskyi.vartovyi.utils.normalizeUnicode

data class TriggerKeywordRule(
    val storageValue: String,
    val type: TriggerKeywordRuleType,
    val terms: List<String>,
    val displayValue: String,
) {

    fun normalizedSignature(): String {
        val normalizedTerms = terms.joinToString(
            separator = KeywordRuleFormat.NORMALIZED_SIGNATURE_SEPARATOR,
        ) { term -> normalizeText(term) }
        return "${type.name}${KeywordRuleFormat.NORMALIZED_SIGNATURE_SEPARATOR}$normalizedTerms"
    }

    fun matches(text: String): Boolean {
        if (text.isBlank()) return false

        val normalizedText = normalizeText(text)
        val textTokens = extractWordTokens(text)

        return when (type) {
            TriggerKeywordRuleType.WORD -> {
                val term = terms.firstOrNull() ?: return false
                extractWordTokens(term).all { token -> token in textTokens }
            }

            TriggerKeywordRuleType.ALL_WORDS -> {
                terms.all { term ->
                    extractWordTokens(term).all { token -> token in textTokens }
                }
            }

            TriggerKeywordRuleType.PHRASE -> {
                val phrase = terms.firstOrNull() ?: return false
                normalizedText.contains(normalizeText(phrase))
            }
        }
    }

    private fun extractWordTokens(text: String): Set<String> {
        return text.normalizeUnicode()
            .normalizeApostrophes()
            .lowercase()
            .split(KeywordRuleFormat.NON_ALPHANUMERIC_RUN_REGEX)
            .map { token -> token.trim() }
            .filter { token -> token.isNotBlank() }
            .toSet()
    }

    private fun normalizeText(value: String): String {
        return value.normalizeUnicode()
            .normalizeApostrophes()
            .lowercase()
            .replace(KeywordRuleFormat.INTERNAL_WHITESPACE_REGEX, KeywordRuleFormat.SINGLE_SPACE)
            .trim()
    }

}
