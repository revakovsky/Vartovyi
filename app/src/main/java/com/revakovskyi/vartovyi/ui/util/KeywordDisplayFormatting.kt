package com.revakovskyi.vartovyi.ui.util

import com.revakovskyi.vartovyi.constants.KeywordRuleFormat

/**
 * Quotes are storage syntax for [com.revakovskyi.vartovyi.model.TriggerKeywordRuleType.PHRASE],
 * not text meant for the user's eyes — strip them for any on-screen display
 */
fun String.unwrapPhraseQuotes(): String =
    if (length >= 2 &&
        startsWith(KeywordRuleFormat.QUOTE) &&
        endsWith(KeywordRuleFormat.QUOTE)
    ) {
        substring(1, length - 1)
    } else {
        this
    }
