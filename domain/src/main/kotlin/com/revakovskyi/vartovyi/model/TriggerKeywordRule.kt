package com.revakovskyi.vartovyi.model

private const val QUOTE = "\""
private const val ALL_WORDS_SEPARATOR = "+"
private const val DISPLAY_ALL_WORDS_SEPARATOR = " + "
private const val STORAGE_ALL_WORDS_SEPARATOR = "+"
private const val NORMALIZED_SIGNATURE_SEPARATOR = "|"
private const val EMPTY_VALUE = ""
private const val NON_WORD_REGEX = "[^\\p{L}\\p{N}]+"
private const val WHITESPACE_REGEX = "\\s+"
private const val SINGLE_SPACE = " "

enum class TriggerKeywordRuleType {
    WORD,
    ALL_WORDS,
    PHRASE,
}

data class TriggerKeywordRule(
    val storageValue: String,
    val type: TriggerKeywordRuleType,
    val terms: List<String>,
    val displayValue: String,
) {
    fun normalizedSignature(): String {
        val normalizedTerms =
            terms.joinToString(separator = NORMALIZED_SIGNATURE_SEPARATOR) { term ->
                normalizeText(term)
            }
        return "${type.name}$NORMALIZED_SIGNATURE_SEPARATOR$normalizedTerms"
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

    companion object {
        fun fromStorageValue(storageValue: String): TriggerKeywordRule {
            val trimmedValue = storageValue.trim()

            val isQuotedPhrase = trimmedValue.length >= 2 &&
                    trimmedValue.startsWith(QUOTE) &&
                    trimmedValue.endsWith(QUOTE)
            if (isQuotedPhrase) {
                val phraseValue = trimmedValue
                    .removePrefix(QUOTE)
                    .removeSuffix(QUOTE)
                    .trim()
                val displayValue = "$QUOTE$phraseValue$QUOTE"
                return TriggerKeywordRule(
                    storageValue = displayValue,
                    type = TriggerKeywordRuleType.PHRASE,
                    terms = listOf(phraseValue),
                    displayValue = displayValue,
                )
            }

            if (trimmedValue.contains(ALL_WORDS_SEPARATOR)) {
                val allWordsTerms = trimmedValue.split(ALL_WORDS_SEPARATOR)
                    .map { value -> value.trim() }
                    .filter { value -> value.isNotBlank() }
                val displayValue =
                    allWordsTerms.joinToString(separator = DISPLAY_ALL_WORDS_SEPARATOR)
                val storageValue = buildAllWordsStorageValue(allWordsTerms)
                return TriggerKeywordRule(
                    storageValue = storageValue,
                    type = TriggerKeywordRuleType.ALL_WORDS,
                    terms = allWordsTerms,
                    displayValue = displayValue,
                )
            }

            return TriggerKeywordRule(
                storageValue = trimmedValue,
                type = TriggerKeywordRuleType.WORD,
                terms = listOf(trimmedValue),
                displayValue = trimmedValue,
            )
        }

        fun create(
            type: TriggerKeywordRuleType,
            input: String,
        ): TriggerKeywordRule? {
            val normalizedInput = input.trim()
            if (normalizedInput.isBlank()) return null

            return when (type) {
                TriggerKeywordRuleType.WORD -> {
                    val tokenizedWord = normalizeDisplayText(normalizedInput)
                    if (tokenizedWord.isBlank()) return null
                    fromStorageValue(tokenizedWord)
                }

                TriggerKeywordRuleType.ALL_WORDS -> {
                    val words = normalizedInput.split(NON_WORD_REGEX.toRegex())
                        .map { value -> normalizeDisplayText(value) }
                        .filter { value -> value.isNotBlank() }
                    if (words.isEmpty()) return null
                    val displayValue = words.joinToString(separator = DISPLAY_ALL_WORDS_SEPARATOR)
                    val storageValue = buildAllWordsStorageValue(words)
                    TriggerKeywordRule(
                        storageValue = storageValue,
                        type = TriggerKeywordRuleType.ALL_WORDS,
                        terms = words,
                        displayValue = displayValue,
                    )
                }

                TriggerKeywordRuleType.PHRASE -> {
                    val phrase = normalizeDisplayText(normalizedInput)
                    if (phrase.isBlank()) return null
                    fromStorageValue("$QUOTE$phrase$QUOTE")
                }
            }
        }

        private fun extractWordTokens(text: String): Set<String> {
            return text.lowercase()
                .split(NON_WORD_REGEX.toRegex())
                .map { token -> token.trim() }
                .filter { token -> token.isNotBlank() }
                .toSet()
        }

        private fun normalizeText(value: String): String {
            return value.lowercase()
                .replace(WHITESPACE_REGEX.toRegex(), SINGLE_SPACE)
                .trim()
        }

        private fun normalizeDisplayText(value: String): String {
            return value.replace(WHITESPACE_REGEX.toRegex(), SINGLE_SPACE)
                .trim()
        }

        private fun buildAllWordsStorageValue(terms: List<String>): String {
            val normalizedTerms = terms.filter { term -> term.isNotBlank() }
            if (normalizedTerms.isEmpty()) return EMPTY_VALUE

            val baseValue = normalizedTerms.joinToString(separator = STORAGE_ALL_WORDS_SEPARATOR)
            return if (normalizedTerms.size == 1) {
                "$baseValue$STORAGE_ALL_WORDS_SEPARATOR"
            } else {
                baseValue
            }
        }
    }
}
