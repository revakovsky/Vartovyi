package com.revakovskyi.vartovyi.utils

import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import java.text.Normalizer

internal fun String.normalizeApostrophes(): String =
    replace(KeywordRuleFormat.APOSTROPHE_VARIANTS_REGEX, KeywordRuleFormat.UKRAINIAN_APOSTROPHE)

internal fun String.normalizeUnicode(): String =
    Normalizer.normalize(this, Normalizer.Form.NFKC)

/**
 * Shared cleanup for free-text input: strips placeholder brackets and invisible characters,
 * normalizes Unicode and apostrophes
 */
internal fun String.normalizeInputChars(): String =
    normalizeUnicode()
        .replace(KeywordRuleFormat.PLACEHOLDER_BRACKETS_REGEX, KeywordRuleFormat.EMPTY_VALUE)
        .replace(KeywordRuleFormat.INVISIBLE_CHARS_REGEX, KeywordRuleFormat.EMPTY_VALUE)
        .normalizeApostrophes()

/** Brings both sides of a match (stored value and incoming text) to one canonical form. */
internal fun String.normalizeForMatching(): String =
    normalizeUnicode()
        .normalizeApostrophes()
        .lowercase()
        .replace(KeywordRuleFormat.INTERNAL_WHITESPACE_REGEX, KeywordRuleFormat.SINGLE_SPACE)
        .trim()
