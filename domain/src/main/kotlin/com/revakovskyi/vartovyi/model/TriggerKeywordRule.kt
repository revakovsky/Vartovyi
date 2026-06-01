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

    /**
     * Terms are sorted so that order-independent rules (ALL_WORDS matches in any order)
     * share one signature and are detected as duplicates regardless of input order.
     */
    fun normalizedSignature(): String {
        val normalizedTerms = terms
            .map { term -> normalizeText(term) }
            .sorted()
            .joinToString(separator = KeywordRuleFormat.NORMALIZED_SIGNATURE_SEPARATOR)
        return "${type.name}${KeywordRuleFormat.NORMALIZED_SIGNATURE_SEPARATOR}$normalizedTerms"
    }

    fun matches(text: String): Boolean {
        if (text.isBlank()) return false

        val normalizedText = normalizeText(text)
        val textTokens = extractWordTokens(text)

        return when (type) {
            TriggerKeywordRuleType.WORD -> {
                val termTokens = extractWordTokens(terms.firstOrNull().orEmpty())
                termTokens.isNotEmpty() && termTokens.all { token -> token in textTokens }
            }

            TriggerKeywordRuleType.ALL_WORDS -> {
                terms.isNotEmpty() && terms.all { term ->
                    val termTokens = extractWordTokens(term)
                    termTokens.isNotEmpty() && termTokens.all { token -> token in textTokens }
                }
            }

            TriggerKeywordRuleType.PHRASE -> {
                val phrase = normalizeText(terms.firstOrNull().orEmpty())
                phrase.isNotEmpty() && normalizedText.contains(phrase)
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
