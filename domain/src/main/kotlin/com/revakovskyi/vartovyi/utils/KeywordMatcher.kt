package com.revakovskyi.vartovyi.utils

class KeywordMatcher {

    fun matches(text: String, keywords: List<String>, stopWords: List<String>): Boolean {
        val lowerText = text.lowercase()
        val hasKeyword = keywords.any { keyword -> lowerText.contains(keyword.lowercase()) }
        if (!hasKeyword) return false
        val hasStopWord = stopWords.any { stop -> lowerText.contains(stop.lowercase()) }
        return !hasStopWord
    }

    fun findMatchedKeyword(text: String, keywords: List<String>): String? {
        val lowerText = text.lowercase()
        return keywords.firstOrNull { keyword -> lowerText.contains(keyword.lowercase()) }
    }

}
