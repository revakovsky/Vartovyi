package com.revakovskyi.vartovyi.constants

object KeywordRuleFormat {

    const val QUOTE: String = "\""
    const val ALL_WORDS_SEPARATOR: String = "+"
    const val DISPLAY_ALL_WORDS_SEPARATOR: String = " + "
    const val NORMALIZED_SIGNATURE_SEPARATOR: String = "|"
    const val PHRASE_TERM_SEPARATOR: String = " "
    const val SINGLE_SPACE: String = " "
    const val EMPTY_VALUE: String = ""
    const val UKRAINIAN_APOSTROPHE: String = "ʼ"

    val NEW_LINE_REGEX: Regex = Regex("\\r?\\n")
    val INVISIBLE_CHARS_REGEX: Regex = Regex("[\\u200B-\\u200D\\uFEFF\\u00A0]")
    val LEADING_NON_ALPHANUMERIC_REGEX: Regex = Regex("^[^\\p{L}\\p{N}]+")
    val TRAILING_NON_ALPHANUMERIC_REGEX: Regex = Regex("[^\\p{L}\\p{N}]+$")
    val NON_ALPHANUMERIC_RUN_REGEX: Regex = Regex("[^\\p{L}\\p{N}]+")
    val INTERNAL_WHITESPACE_REGEX: Regex = Regex("\\s+")
    val APOSTROPHE_VARIANTS_REGEX: Regex = Regex("[\\u0027\\u2018\\u2019\\u02B9\\u02BB\\u2032]")

}
