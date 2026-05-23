package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.result.KeywordSanitizationResult
import com.revakovskyi.vartovyi.utils.normalizeApostrophes
import com.revakovskyi.vartovyi.utils.normalizeUnicode

interface SanitizeKeywordInputUseCase {
    operator fun invoke(
        rawInput: String,
        selectedType: TriggerKeywordRuleType,
    ): KeywordSanitizationResult
}

class SanitizeKeywordInputUseCaseImpl : SanitizeKeywordInputUseCase {

    override operator fun invoke(
        rawInput: String,
        selectedType: TriggerKeywordRuleType,
    ): KeywordSanitizationResult {
        val preprocessed = rawInput.normalizeUnicode()
            .replace(KeywordRuleFormat.INVISIBLE_CHARS_REGEX, "")
            .normalizeApostrophes()

        if (KeywordRuleFormat.NEW_LINE_REGEX.containsMatchIn(preprocessed)) {
            return KeywordSanitizationResult.MultiLineDetected
        }

        val trimmed = preprocessed.trim()
        if (trimmed.isEmpty()) return KeywordSanitizationResult.Empty

        val hasBalancedOuterQuotes = hasBalancedOuterQuotes(trimmed)
        val isPhraseIntent = hasBalancedOuterQuotes ||
                selectedType == TriggerKeywordRuleType.PHRASE

        return if (isPhraseIntent) {
            sanitizeAsPhrase(trimmed = trimmed, stripOuterQuotes = hasBalancedOuterQuotes)
        } else {
            sanitizeAsWordOrAllWords(trimmed = trimmed, selectedType = selectedType)
        }
    }

    private fun hasBalancedOuterQuotes(trimmed: String): Boolean {
        val totalQuotes = trimmed.count { character ->
            character == KeywordRuleFormat.QUOTE.first()
        }
        return totalQuotes == 2 &&
                trimmed.length >= 2 &&
                trimmed.startsWith(KeywordRuleFormat.QUOTE) &&
                trimmed.endsWith(KeywordRuleFormat.QUOTE)
    }

    private fun sanitizeAsPhrase(
        trimmed: String,
        stripOuterQuotes: Boolean,
    ): KeywordSanitizationResult {
        val unquoted = if (stripOuterQuotes) {
            trimmed.substring(1, trimmed.length - 1)
        } else {
            trimmed
        }
        val cleanedPhrase = unquoted
            .replace(KeywordRuleFormat.LEADING_NON_ALPHANUMERIC_REGEX, "")
            .replace(KeywordRuleFormat.TRAILING_NON_ALPHANUMERIC_REGEX, "")
            .replace(KeywordRuleFormat.INTERNAL_WHITESPACE_REGEX, KeywordRuleFormat.SINGLE_SPACE)
        if (cleanedPhrase.isEmpty()) return KeywordSanitizationResult.Empty

        return KeywordSanitizationResult.Sanitized(
            effectiveType = TriggerKeywordRuleType.PHRASE,
            storageValue = "${KeywordRuleFormat.QUOTE}$cleanedPhrase${KeywordRuleFormat.QUOTE}",
            normalizedRawInput = trimmed,
        )
    }

    private fun sanitizeAsWordOrAllWords(
        trimmed: String,
        selectedType: TriggerKeywordRuleType,
    ): KeywordSanitizationResult {
        val tokens = extractTokens(trimmed)
        if (tokens.isEmpty()) return KeywordSanitizationResult.Empty

        if (tokens.any { token -> token.length < KeywordsLimits.MIN_TERM_LENGTH }) {
            return KeywordSanitizationResult.TermTooShort
        }

        val effectiveType = resolveEffectiveType(selectedType = selectedType, tokens = tokens)
        val storageValue = buildStorageValue(effectiveType = effectiveType, tokens = tokens)

        return KeywordSanitizationResult.Sanitized(
            effectiveType = effectiveType,
            storageValue = storageValue,
            normalizedRawInput = trimmed,
        )
    }

    private fun extractTokens(trimmed: String): List<String> {
        val stripped = trimmed
            .replace(KeywordRuleFormat.LEADING_NON_ALPHANUMERIC_REGEX, "")
            .replace(KeywordRuleFormat.TRAILING_NON_ALPHANUMERIC_REGEX, "")
        if (stripped.isEmpty()) return emptyList()

        return stripped
            .replace(KeywordRuleFormat.NON_ALPHANUMERIC_RUN_REGEX, KeywordRuleFormat.ALL_WORDS_SEPARATOR)
            .split(KeywordRuleFormat.ALL_WORDS_SEPARATOR)
            .filter { token -> token.isNotBlank() }
    }

    private fun resolveEffectiveType(
        selectedType: TriggerKeywordRuleType,
        tokens: List<String>,
    ): TriggerKeywordRuleType {
        if (selectedType != TriggerKeywordRuleType.WORD) return selectedType

        return when {
            tokens.size == 1 -> TriggerKeywordRuleType.WORD
            tokens.size <= KeywordsLimits.MAX_TOKENS_FOR_ALL_WORDS_PROMOTION ->
                TriggerKeywordRuleType.ALL_WORDS

            else -> TriggerKeywordRuleType.PHRASE
        }
    }

    private fun buildStorageValue(
        effectiveType: TriggerKeywordRuleType,
        tokens: List<String>,
    ): String = when (effectiveType) {
        TriggerKeywordRuleType.PHRASE -> {
            val phrase = tokens.joinToString(KeywordRuleFormat.PHRASE_TERM_SEPARATOR)
            "${KeywordRuleFormat.QUOTE}$phrase${KeywordRuleFormat.QUOTE}"
        }

        TriggerKeywordRuleType.ALL_WORDS -> {
            val joined = tokens.joinToString(KeywordRuleFormat.ALL_WORDS_SEPARATOR)
            if (tokens.size == 1) "$joined${KeywordRuleFormat.ALL_WORDS_SEPARATOR}" else joined
        }

        TriggerKeywordRuleType.WORD -> tokens.first()
    }

}
