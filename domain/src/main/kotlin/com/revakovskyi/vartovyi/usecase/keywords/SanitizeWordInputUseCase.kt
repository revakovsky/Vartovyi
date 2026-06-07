package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.model.WordInputTarget
import com.revakovskyi.vartovyi.result.KeywordSanitizationResult
import com.revakovskyi.vartovyi.utils.normalizeApostrophes
import com.revakovskyi.vartovyi.utils.normalizeUnicode

/**
 * Sanitizes raw word input from the Keywords screen. Cleaning is shared across all targets;
 * only the storage format differs: trigger-rule format (quotes / `+`) for
 * [WordInputTarget.TriggerKeyword], plain cleaned text for the rest.
 */
interface SanitizeWordInputUseCase {
    operator fun invoke(
        rawInput: String,
        target: WordInputTarget,
    ): KeywordSanitizationResult
}

class SanitizeWordInputUseCaseImpl : SanitizeWordInputUseCase {

    override operator fun invoke(
        rawInput: String,
        target: WordInputTarget,
    ): KeywordSanitizationResult {
        val preprocessed = rawInput.normalizeUnicode()
            .replace(KeywordRuleFormat.PLACEHOLDER_BRACKETS_REGEX, "")
            .replace(KeywordRuleFormat.INVISIBLE_CHARS_REGEX, "")
            .normalizeApostrophes()

        if (KeywordRuleFormat.NEW_LINE_REGEX.containsMatchIn(preprocessed)) {
            return KeywordSanitizationResult.MultiLineDetected
        }

        val trimmed = preprocessed.trim()
        if (trimmed.isEmpty()) return KeywordSanitizationResult.Empty

        return when (target) {
            is WordInputTarget.TriggerKeyword -> sanitizeTriggerKeyword(
                trimmed = trimmed,
                selectedType = target.selectedType,
            )

            is WordInputTarget.StopWord,
            is WordInputTarget.TelegramChannel,
                -> sanitizePlainText(trimmed)
        }
    }

    private fun sanitizeTriggerKeyword(
        trimmed: String,
        selectedType: TriggerKeywordRuleType,
    ): KeywordSanitizationResult {
        val hasBalancedOuterQuotes = hasBalancedOuterQuotes(trimmed)
        val isPhraseIntent = hasBalancedOuterQuotes ||
                selectedType == TriggerKeywordRuleType.PHRASE

        return if (isPhraseIntent) {
            sanitizeWholeText(
                trimmed = trimmed,
                stripOuterQuotes = hasBalancedOuterQuotes,
                wrapInQuotes = true,
            )
        } else {
            sanitizeAsWordOrAllWords(trimmed = trimmed, selectedType = selectedType)
        }
    }

    private fun sanitizePlainText(trimmed: String): KeywordSanitizationResult {
        return sanitizeWholeText(
            trimmed = trimmed,
            stripOuterQuotes = hasBalancedOuterQuotes(trimmed),
            wrapInQuotes = false,
        )
    }

    private fun hasBalancedOuterQuotes(trimmed: String): Boolean {
        val totalQuotes = trimmed.count { character ->
            character == KeywordRuleFormat.QUOTE.first()
        }
        return totalQuotes == 2 &&
                trimmed.startsWith(KeywordRuleFormat.QUOTE) &&
                trimmed.endsWith(KeywordRuleFormat.QUOTE)
    }

    private fun sanitizeWholeText(
        trimmed: String,
        stripOuterQuotes: Boolean,
        wrapInQuotes: Boolean,
    ): KeywordSanitizationResult {
        val unquoted = if (stripOuterQuotes) {
            trimmed.substring(1, trimmed.length - 1)
        } else {
            trimmed
        }
        val cleanedText = unquoted
            .replace(KeywordRuleFormat.INTERNAL_WHITESPACE_REGEX, KeywordRuleFormat.SINGLE_SPACE)
            .trim()
        if (cleanedText.isEmpty()) return KeywordSanitizationResult.Empty

        val alphanumericCount = cleanedText.count { character -> character.isLetterOrDigit() }
        if (alphanumericCount == 0) return KeywordSanitizationResult.Empty
        if (alphanumericCount < KeywordsLimits.MIN_TERM_LENGTH) {
            return KeywordSanitizationResult.TermTooShort
        }

        val storageValue = if (wrapInQuotes) {
            "${KeywordRuleFormat.QUOTE}$cleanedText${KeywordRuleFormat.QUOTE}"
        } else {
            cleanedText
        }

        return KeywordSanitizationResult.Sanitized(
            effectiveType = TriggerKeywordRuleType.PHRASE,
            storageValue = storageValue,
            normalizedRawInput = trimmed,
        )
    }

    private fun sanitizeAsWordOrAllWords(
        trimmed: String,
        selectedType: TriggerKeywordRuleType,
    ): KeywordSanitizationResult {
        val tokens = extractTokens(trimmed)
        if (tokens.isEmpty()) return KeywordSanitizationResult.StartsWithNonAlphanumeric

        if (tokens.all { token -> token.length < KeywordsLimits.MIN_TERM_LENGTH }) {
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
        if (selectedType == TriggerKeywordRuleType.ALL_WORDS && tokens.size == 1) {
            return TriggerKeywordRuleType.WORD
        }

        if (selectedType != TriggerKeywordRuleType.WORD) return selectedType

        return when {
            tokens.size == 1 -> TriggerKeywordRuleType.WORD
            tokens.size <= KeywordsLimits.MAX_TOKENS_FOR_ALL_WORDS_PROMOTION ->
                TriggerKeywordRuleType.ALL_WORDS

            else -> TriggerKeywordRuleType.PHRASE
        }
    }

    /** ALL_WORDS always has 2+ tokens here — [resolveEffectiveType] demotes single-token to WORD. */
    private fun buildStorageValue(
        effectiveType: TriggerKeywordRuleType,
        tokens: List<String>,
    ): String = when (effectiveType) {
        TriggerKeywordRuleType.PHRASE -> {
            val phrase = tokens.joinToString(KeywordRuleFormat.PHRASE_TERM_SEPARATOR)
            "${KeywordRuleFormat.QUOTE}$phrase${KeywordRuleFormat.QUOTE}"
        }

        TriggerKeywordRuleType.ALL_WORDS -> {
            tokens.joinToString(KeywordRuleFormat.ALL_WORDS_SEPARATOR)
        }

        TriggerKeywordRuleType.WORD -> tokens.first()
    }

}
