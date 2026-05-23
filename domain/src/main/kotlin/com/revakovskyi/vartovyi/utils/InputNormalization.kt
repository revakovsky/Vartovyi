package com.revakovskyi.vartovyi.utils

import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import java.text.Normalizer

internal fun String.normalizeApostrophes(): String =
    replace(KeywordRuleFormat.APOSTROPHE_VARIANTS_REGEX, KeywordRuleFormat.UKRAINIAN_APOSTROPHE)

internal fun String.normalizeUnicode(): String =
    Normalizer.normalize(this, Normalizer.Form.NFKC)
