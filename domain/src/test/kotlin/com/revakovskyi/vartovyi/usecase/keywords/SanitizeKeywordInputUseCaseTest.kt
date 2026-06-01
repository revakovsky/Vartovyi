package com.revakovskyi.vartovyi.usecase.keywords

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.result.KeywordSanitizationResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

class SanitizeKeywordInputUseCaseTest {

    private lateinit var useCase: SanitizeKeywordInputUseCase

    @BeforeEach
    fun setUp() {
        useCase = SanitizeKeywordInputUseCaseImpl()
    }

    // region Rejected inputs
    @Nested
    inner class RejectedInputs {

        @ParameterizedTest
        @CsvSource(
            "'', WORD",
            "'   ', WORD",
            "---!!!---, PHRASE",
        )
        fun `input resolves to Empty`(rawInput: String, selectedType: TriggerKeywordRuleType) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)
            assertThat(result).isEqualTo(KeywordSanitizationResult.Empty)
        }

        @Test
        fun `only-symbols input resolves to StartsWithNonAlphanumeric`() {
            val result = useCase(rawInput = "!!!@#\$", selectedType = TriggerKeywordRuleType.WORD)
            assertThat(result).isEqualTo(KeywordSanitizationResult.StartsWithNonAlphanumeric)
        }

        @ParameterizedTest
        @CsvSource(
            "a, WORD",
            "A + B + C, WORD",
            "!!!а, WORD",
            "!!!а, PHRASE",
            "a, PHRASE",
        )
        fun `input resolves to TermTooShort`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)
            assertThat(result).isEqualTo(KeywordSanitizationResult.TermTooShort)
        }

        @Test
        fun `input with only invisible unicode chars resolves to Empty`() {
            val result = useCase(
                rawInput = "​‌‍﻿ ",
                selectedType = TriggerKeywordRuleType.WORD
            )
            assertThat(result).isEqualTo(KeywordSanitizationResult.Empty)
        }

        @Test
        fun `newline in the middle resolves to MultiLineDetected`() {
            val result = useCase(rawInput = "abc\ndef", selectedType = TriggerKeywordRuleType.WORD)
            assertThat(result).isEqualTo(KeywordSanitizationResult.MultiLineDetected)
        }

        @Test
        fun `carriage return and newline resolves to MultiLineDetected`() {
            val result = useCase(
                rawInput = "abc\r\ndef",
                selectedType = TriggerKeywordRuleType.WORD
            )
            assertThat(result).isEqualTo(KeywordSanitizationResult.MultiLineDetected)
        }

        @Test
        fun `newline at the end resolves to MultiLineDetected`() {
            val result = useCase(rawInput = "abc\n", selectedType = TriggerKeywordRuleType.WORD)
            assertThat(result).isEqualTo(KeywordSanitizationResult.MultiLineDetected)
        }
    }
    // endregion

    // region Effective type resolution
    @Nested
    inner class EffectiveTypeResolution {

        @ParameterizedTest
        @CsvSource(
            "ракета, WORD, WORD, ракета, ракета",
            "ракета місто, WORD, ALL_WORDS, ракета+місто, ракета місто",
            "ракета летить над, WORD, ALL_WORDS, ракета+летить+над, ракета летить над",
            "ракета летить над містом, WORD, ALL_WORDS, ракета+летить+над+містом, ракета летить над містом",
            "ракета летить над великим містом зараз, WORD, ALL_WORDS, ракета+летить+над+великим+містом+зараз, ракета летить над великим містом зараз",
            "ракета летить над великим містом просто зараз, WORD, PHRASE, '\"ракета летить над великим містом просто зараз\"', ракета летить над великим містом просто зараз",
            "один два три чотири пять шість сім вісім, WORD, PHRASE, '\"один два три чотири пять шість сім вісім\"', один два три чотири пять шість сім вісім",
            "ракета, ALL_WORDS, WORD, ракета, ракета",
            "ракета місто, ALL_WORDS, ALL_WORDS, ракета+місто, ракета місто",
            "один два три чотири пять шість сім, ALL_WORDS, ALL_WORDS, один+два+три+чотири+пять+шість+сім, один два три чотири пять шість сім",
            "ракета, PHRASE, PHRASE, '\"ракета\"', ракета",
            "ракета місто, PHRASE, PHRASE, '\"ракета місто\"', ракета місто",
        )
        fun `input resolves to expected effective type and storage`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedType: TriggerKeywordRuleType,
            expectedStorage: String,
            expectedNormalizedRawInput: String,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)

            assertThat(result).isEqualTo(
                KeywordSanitizationResult.Sanitized(
                    effectiveType = expectedType,
                    storageValue = expectedStorage,
                    normalizedRawInput = expectedNormalizedRawInput,
                )
            )
        }
    }
    // endregion

    // region Word storage
    @Nested
    inner class WordStorage {

        @ParameterizedTest
        @CsvSource(
            "'  ракета  ', WORD, WORD, ракета, ракета",
            "Ракета, WORD, WORD, Ракета, Ракета",
            "ракета!!!, WORD, WORD, ракета, ракета!!!",
            "123, WORD, WORD, 123, 123",
            "!!!ракета, WORD, WORD, ракета, !!!ракета",
            "123ракета, WORD, WORD, 123ракета, 123ракета",
            "<ракета>, WORD, WORD, ракета, ракета",
        )
        fun `word input is stored as a single token`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedType: TriggerKeywordRuleType,
            expectedStorage: String,
            expectedNormalizedRawInput: String,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)

            assertThat(result).isEqualTo(
                KeywordSanitizationResult.Sanitized(
                    effectiveType = expectedType,
                    storageValue = expectedStorage,
                    normalizedRawInput = expectedNormalizedRawInput,
                )
            )
        }
    }
    // endregion

    // region All-words storage
    @Nested
    inner class AllWordsStorage {

        @ParameterizedTest
        @CsvSource(
            "!!!ракета місто, ALL_WORDS, ALL_WORDS, ракета+місто, !!!ракета місто",
            "123ракета місто, ALL_WORDS, ALL_WORDS, 123ракета+місто, 123ракета місто",
            "ракета-носій, WORD, ALL_WORDS, ракета+носій, ракета-носій",
            "'ракета,місто', WORD, ALL_WORDS, ракета+місто, 'ракета,місто'",
            "ракета+місто, WORD, ALL_WORDS, ракета+місто, ракета+місто",
            "'ракета,  місто', WORD, ALL_WORDS, ракета+місто, 'ракета,  місто'",
            "один два + три/чотири-пять — шість, ALL_WORDS, ALL_WORDS, один+два+три+чотири+пять+шість, один два + три/чотири-пять — шість",
            "ракета+<місто>, ALL_WORDS, ALL_WORDS, ракета+місто, ракета+місто",
        )
        fun `all-words input is stored as plus-joined tokens`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedType: TriggerKeywordRuleType,
            expectedStorage: String,
            expectedNormalizedRawInput: String,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)

            assertThat(result).isEqualTo(
                KeywordSanitizationResult.Sanitized(
                    effectiveType = expectedType,
                    storageValue = expectedStorage,
                    normalizedRawInput = expectedNormalizedRawInput,
                )
            )
        }
    }
    // endregion

    // region Short token tolerance
    @Nested
    inner class ShortTokenTolerance {

        @ParameterizedTest
        @CsvSource(
            "ab, WORD, WORD, ab, ab",
            "abc a def, WORD, ALL_WORDS, abc+a+def, abc a def",
            "a abc, WORD, ALL_WORDS, a+abc, a abc",
            "ab cd, ALL_WORDS, ALL_WORDS, ab+cd, ab cd",
            "a bc, ALL_WORDS, ALL_WORDS, a+bc, a bc",
            "ab, PHRASE, PHRASE, '\"ab\"', ab",
        )
        fun `input with at least one valid token is accepted`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedType: TriggerKeywordRuleType,
            expectedStorage: String,
            expectedNormalizedRawInput: String,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)

            assertThat(result).isEqualTo(
                KeywordSanitizationResult.Sanitized(
                    effectiveType = expectedType,
                    storageValue = expectedStorage,
                    normalizedRawInput = expectedNormalizedRawInput,
                )
            )
        }
    }
    // endregion

    // region Phrase storage
    @Nested
    inner class PhraseStorage {

        @ParameterizedTest
        @CsvSource(
            "!!!ракета над містом, PHRASE, PHRASE, '\"!!!ракета над містом\"', !!!ракета над містом",
            "!!!аб, PHRASE, PHRASE, '\"!!!аб\"', !!!аб",
            "123ракета над містом, PHRASE, PHRASE, '\"123ракета над містом\"', 123ракета над містом",
            "'один,два,три,чотири,пять,шість,сім', WORD, PHRASE, '\"один два три чотири пять шість сім\"', 'один,два,три,чотири,пять,шість,сім'",
            "'один,два,три,чотири,пять,шість,сім', PHRASE, PHRASE, '\"один,два,три,чотири,пять,шість,сім\"', 'один,два,три,чотири,пять,шість,сім'",
            "Шахед-136, PHRASE, PHRASE, '\"Шахед-136\"', Шахед-136",
            "'ракета  над  містом', PHRASE, PHRASE, '\"ракета над містом\"', 'ракета  над  містом'",
            "---ракета над містом, PHRASE, PHRASE, '\"---ракета над містом\"', ---ракета над містом",
            "ракета над містом!!!, PHRASE, PHRASE, '\"ракета над містом!!!\"', ракета над містом!!!",
            "'  ракета над містом  ', PHRASE, PHRASE, '\"ракета над містом\"', ракета над містом",
            "<ракета над містом>, PHRASE, PHRASE, '\"ракета над містом\"', ракета над містом",
        )
        fun `phrase input is stored quoted with cleaned content`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedType: TriggerKeywordRuleType,
            expectedStorage: String,
            expectedNormalizedRawInput: String,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)

            assertThat(result).isEqualTo(
                KeywordSanitizationResult.Sanitized(
                    effectiveType = expectedType,
                    storageValue = expectedStorage,
                    normalizedRawInput = expectedNormalizedRawInput,
                )
            )
        }
    }
    // endregion

    // region Quote handling
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class QuoteHandling {

        @ParameterizedTest
        @MethodSource("quoteCases")
        fun `quote handling produces the expected result`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedResult: KeywordSanitizationResult,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)
            assertThat(result).isEqualTo(expectedResult)
        }

        private fun quoteCases(): List<Arguments> = listOf(
            Arguments.of("\"\"", TriggerKeywordRuleType.WORD, KeywordSanitizationResult.Empty),
            Arguments.of("\"!!!\"", TriggerKeywordRuleType.WORD, KeywordSanitizationResult.Empty),
            Arguments.of(
                "\"a\"",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.TermTooShort
            ),
            Arguments.of(
                "\"ціль на місто\"",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.PHRASE,
                    storageValue = "\"ціль на місто\"",
                    normalizedRawInput = "\"ціль на місто\"",
                ),
            ),
            Arguments.of(
                "\"ціль на місто\"",
                TriggerKeywordRuleType.ALL_WORDS,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.PHRASE,
                    storageValue = "\"ціль на місто\"",
                    normalizedRawInput = "\"ціль на місто\"",
                ),
            ),
            Arguments.of(
                "\"ціль на місто\"",
                TriggerKeywordRuleType.PHRASE,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.PHRASE,
                    storageValue = "\"ціль на місто\"",
                    normalizedRawInput = "\"ціль на місто\"",
                ),
            ),
            Arguments.of(
                "\"ракета\"",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.PHRASE,
                    storageValue = "\"ракета\"",
                    normalizedRawInput = "\"ракета\"",
                ),
            ),
            Arguments.of(
                "abc\"def",
                TriggerKeywordRuleType.PHRASE,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.PHRASE,
                    storageValue = "\"abc\"def\"",
                    normalizedRawInput = "abc\"def",
                ),
            ),
            Arguments.of(
                "\"abc def",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.ALL_WORDS,
                    storageValue = "abc+def",
                    normalizedRawInput = "\"abc def",
                ),
            ),
            Arguments.of(
                "abc def\"",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.ALL_WORDS,
                    storageValue = "abc+def",
                    normalizedRawInput = "abc def\"",
                ),
            ),
            Arguments.of(
                "\"ціль\" \"місто\"",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.ALL_WORDS,
                    storageValue = "ціль+місто",
                    normalizedRawInput = "\"ціль\" \"місто\"",
                ),
            ),
            Arguments.of(
                "abc\"def",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.ALL_WORDS,
                    storageValue = "abc+def",
                    normalizedRawInput = "abc\"def",
                ),
            ),
            // exactly two quotes but NOT at both ends -> not a balanced phrase
            Arguments.of(
                "ab\"cd\"ef",
                TriggerKeywordRuleType.WORD,
                KeywordSanitizationResult.Sanitized(
                    effectiveType = TriggerKeywordRuleType.ALL_WORDS,
                    storageValue = "ab+cd+ef",
                    normalizedRawInput = "ab\"cd\"ef",
                ),
            ),
        )
    }
    // endregion

    // region Normalization
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Normalization {

        @ParameterizedTest
        @MethodSource("apostropheCases")
        fun `apostrophe variants are normalized to the canonical apostrophe`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedResult: KeywordSanitizationResult,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)
            assertThat(result).isEqualTo(expectedResult)
        }

        @ParameterizedTest
        @CsvSource(
            "ｒａｋｅｔａ, WORD, WORD, raketa, raketa",
            "ｒａｋｅｔａ ｍｉｓｔｏ, ALL_WORDS, ALL_WORDS, raketa+misto, raketa misto",
            "ｒａｋｅｔａ ｍｉｓｔｏ, PHRASE, PHRASE, '\"raketa misto\"', raketa misto",
        )
        fun `full-width unicode input is NFKC-normalized for each type`(
            rawInput: String,
            selectedType: TriggerKeywordRuleType,
            expectedType: TriggerKeywordRuleType,
            expectedStorage: String,
            expectedNormalizedRawInput: String,
        ) {
            val result = useCase(rawInput = rawInput, selectedType = selectedType)
            assertThat(result).isEqualTo(
                KeywordSanitizationResult.Sanitized(
                    effectiveType = expectedType,
                    storageValue = expectedStorage,
                    normalizedRawInput = expectedNormalizedRawInput,
                )
            )
        }

        private fun apostropheCases(): List<Arguments> {
            val normalizedWord = KeywordSanitizationResult.Sanitized(
                effectiveType = TriggerKeywordRuleType.WORD,
                storageValue = "Київʼянин",
                normalizedRawInput = "Київʼянин",
            )

            return listOf(
                // right single quotation mark
                Arguments.of("Київ’янин", TriggerKeywordRuleType.WORD, normalizedWord),
                // straight apostrophe
                Arguments.of("Київ'янин", TriggerKeywordRuleType.WORD, normalizedWord),
                // left single quotation mark
                Arguments.of("Київ‘янин", TriggerKeywordRuleType.WORD, normalizedWord),
                // modifier letter prime
                Arguments.of("Київʹянин", TriggerKeywordRuleType.WORD, normalizedWord),
                // modifier letter turned comma
                Arguments.of("Київʻянин", TriggerKeywordRuleType.WORD, normalizedWord),
                // prime
                Arguments.of("Київ′янин", TriggerKeywordRuleType.WORD, normalizedWord),
                // apostrophe normalized inside a phrase
                Arguments.of(
                    "ціль на Київ'янин",
                    TriggerKeywordRuleType.PHRASE,
                    KeywordSanitizationResult.Sanitized(
                        effectiveType = TriggerKeywordRuleType.PHRASE,
                        storageValue = "\"ціль на Київʼянин\"",
                        normalizedRawInput = "ціль на Київʼянин",
                    ),
                ),
            )
        }
    }
    // endregion

}
