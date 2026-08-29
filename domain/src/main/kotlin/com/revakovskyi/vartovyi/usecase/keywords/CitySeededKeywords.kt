package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.constants.CITY_PLACEHOLDER
import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.utils.normalizeInputChars

/**
 * Cleans a raw city name; `null` if it ends up blank or shorter than
 * [KeywordsLimits.MIN_TERM_LENGTH]
 */
internal fun normalizeCityForSeed(rawCity: String): String? {
    val normalized = rawCity
        .normalizeInputChars()
        .replace(KeywordRuleFormat.INTERNAL_WHITESPACE_REGEX, KeywordRuleFormat.SINGLE_SPACE)
        .trim()

    return normalized.takeIf { it.length >= KeywordsLimits.MIN_TERM_LENGTH }
}

/** Substitutes [CITY_PLACEHOLDER] in [DEFAULT_KEYWORDS_SEED] with an already-normalized [city] */
internal fun buildCitySeededKeywords(city: String): List<String> =
    DEFAULT_KEYWORDS_SEED.map { template -> template.replace(CITY_PLACEHOLDER, city) }
