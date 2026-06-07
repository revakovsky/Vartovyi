package com.revakovskyi.vartovyi.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource

class TriggerKeywordRuleTest {

    // region parseTriggerKeywordRuleFromStorage()
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class StorageParsing {

        @ParameterizedTest
        @MethodSource("parseCases")
        fun `parser builds the expected rule from a stored value`(
            storageValue: String,
            expectedRule: TriggerKeywordRule,
        ) {
            assertThat(parseTriggerKeywordRuleFromStorage(storageValue)).isEqualTo(expectedRule)
        }

        private fun parseCases(): List<Arguments> = listOf(
            Arguments.of(
                "ракета",
                TriggerKeywordRule(
                    storageValue = "ракета",
                    type = TriggerKeywordRuleType.WORD,
                    terms = listOf("ракета"),
                    displayValue = "ракета",
                ),
            ),
            Arguments.of(
                "  ракета  ",
                TriggerKeywordRule(
                    storageValue = "ракета",
                    type = TriggerKeywordRuleType.WORD,
                    terms = listOf("ракета"),
                    displayValue = "ракета",
                ),
            ),
            Arguments.of(
                "\"ракета місто\"",
                TriggerKeywordRule(
                    storageValue = "\"ракета місто\"",
                    type = TriggerKeywordRuleType.PHRASE,
                    terms = listOf("ракета місто"),
                    displayValue = "\"ракета місто\"",
                ),
            ),
            Arguments.of(
                "\"  ракета  \"",
                TriggerKeywordRule(
                    storageValue = "\"ракета\"",
                    type = TriggerKeywordRuleType.PHRASE,
                    terms = listOf("ракета"),
                    displayValue = "\"ракета\"",
                ),
            ),
            Arguments.of(
                "ракета+місто",
                TriggerKeywordRule(
                    storageValue = "ракета+місто",
                    type = TriggerKeywordRuleType.ALL_WORDS,
                    terms = listOf("ракета", "місто"),
                    displayValue = "ракета + місто",
                ),
            ),
            Arguments.of(
                "ракета + + місто",
                TriggerKeywordRule(
                    storageValue = "ракета+місто",
                    type = TriggerKeywordRuleType.ALL_WORDS,
                    terms = listOf("ракета", "місто"),
                    displayValue = "ракета + місто",
                ),
            ),
            Arguments.of(
                "ракета+",
                TriggerKeywordRule(
                    storageValue = "ракета+",
                    type = TriggerKeywordRuleType.ALL_WORDS,
                    terms = listOf("ракета"),
                    displayValue = "ракета",
                ),
            ),
            Arguments.of(
                "",
                TriggerKeywordRule(
                    storageValue = "",
                    type = TriggerKeywordRuleType.WORD,
                    terms = listOf(""),
                    displayValue = "",
                ),
            ),
            Arguments.of(
                "   ",
                TriggerKeywordRule(
                    storageValue = "",
                    type = TriggerKeywordRuleType.WORD,
                    terms = listOf(""),
                    displayValue = "",
                ),
            ),
            Arguments.of(
                "\"",
                TriggerKeywordRule(
                    storageValue = "\"",
                    type = TriggerKeywordRuleType.WORD,
                    terms = listOf("\""),
                    displayValue = "\"",
                ),
            ),
            Arguments.of(
                "\"\"",
                TriggerKeywordRule(
                    storageValue = "\"\"",
                    type = TriggerKeywordRuleType.PHRASE,
                    terms = listOf(""),
                    displayValue = "\"\"",
                ),
            ),
            Arguments.of(
                "+",
                TriggerKeywordRule(
                    storageValue = "",
                    type = TriggerKeywordRuleType.ALL_WORDS,
                    terms = emptyList(),
                    displayValue = "",
                ),
            ),
        )
    }
    // endregion

    // region matches()
    @Nested
    inner class Matching {

        @ParameterizedTest
        @CsvSource(
            // WORD — whole token, case-insensitive (NOT a substring)
            "ракета, Ворожа РАКЕТА летить, true",
            "ракета, тихе місто, false",
            "рак, виловили рак сьогодні, true",
            "рак, летить ракета, false",
            // ALL_WORDS — every term present, order- and case-independent, no stemming
            "ракета+місто, ракета і місто, true",
            "ракета+місто, ракета над полем, false",
            "ракета+місто, ракету і місто, false",
            "ракета+місто, спершу місто потім ракета, true",
            "ракета+місто, РАКЕТА та МІСТО горять, true",
            // PHRASE — raw substring (can match mid-word), whitespace-collapsed, case-insensitive
            "'\"Шахед-136\"', Шахед-136 влучив у ціль, true",
            "'\"Шахед-136\"', повідомлення про шахед-136 у місті, true",
            "'\"Шахед-136\"', 'шахед впав, було 136 уламків', false",
            "'\"Шахед-136\"', Шахед 136 у небі, false",
            "'\"ворожа ракета\"', зафіксовано ворожа ракета над містом, true",
            "'\"ворожа ракета\"', 'ворожа    ракета у небі', true",
            "'\"рак\"', летить ракета, true",
            // empty / corrupt rules (only reachable via bad data) must match NOTHING
            "+, ракета летить, false",
            "'\"\"', ракета летить, false",
            "'', ракета летить, false",
            // blank message text never matches any rule
            "ракета, '   ', false",
        )
        fun `matches resolves WORD by token, ALL_WORDS by all tokens, PHRASE by substring`(
            storageValue: String,
            text: String,
            expectedMatch: Boolean,
        ) {
            val rule = parseTriggerKeywordRuleFromStorage(storageValue)
            assertThat(rule.matches(text)).isEqualTo(expectedMatch)
        }
    }
    // endregion

    // region normalizedSignature() — duplicate detection
    @Nested
    inner class NormalizedSignature {

        @Test
        fun `same WORD in different letter case produces the same signature`() {
            val lowercaseRule = parseTriggerKeywordRuleFromStorage("ракета")
            val uppercaseRule = parseTriggerKeywordRuleFromStorage("Ракета")
            assertThat(uppercaseRule.normalizedSignature())
                .isEqualTo(lowercaseRule.normalizedSignature())
        }

        @Test
        fun `same ALL_WORDS terms in different case produce the same signature`() {
            val lowercaseRule = parseTriggerKeywordRuleFromStorage("ракета+місто")
            val uppercaseRule = parseTriggerKeywordRuleFromStorage("Ракета+Місто")
            assertThat(uppercaseRule.normalizedSignature())
                .isEqualTo(lowercaseRule.normalizedSignature())
        }

        @Test
        fun `different words produce different signatures`() {
            val rocketRule = parseTriggerKeywordRuleFromStorage("ракета")
            val cityRule = parseTriggerKeywordRuleFromStorage("місто")
            assertThat(rocketRule.normalizedSignature())
                .isNotEqualTo(cityRule.normalizedSignature())
        }

        @Test
        fun `same terms with different type produce different signatures`() {
            val wordRule = parseTriggerKeywordRuleFromStorage("ракета")
            val phraseRule = parseTriggerKeywordRuleFromStorage("\"ракета\"")
            assertThat(phraseRule.normalizedSignature())
                .isNotEqualTo(wordRule.normalizedSignature())
        }

        @Test
        fun `same WORD with different apostrophe variants produces the same signature`() {
            // U+0027 straight apostrophe vs U+2019 right single quotation mark
            val straightApostrophe = parseTriggerKeywordRuleFromStorage("Київ'янин")
            val curlyApostrophe = parseTriggerKeywordRuleFromStorage("Київ’янин")
            assertThat(curlyApostrophe.normalizedSignature())
                .isEqualTo(straightApostrophe.normalizedSignature())
        }

        @Test
        fun `same PHRASE with collapsed whitespace produces the same signature`() {
            val singleSpaced = parseTriggerKeywordRuleFromStorage("\"ракета над містом\"")
            val multiSpaced = parseTriggerKeywordRuleFromStorage("\"ракета  над  містом\"")
            assertThat(multiSpaced.normalizedSignature())
                .isEqualTo(singleSpaced.normalizedSignature())
        }

        @Test
        fun `same ALL_WORDS terms in different order produce the same signature`() {
            // ALL_WORDS matching is order-independent, so its signature must be too —
            // entering the same words in a different order is detected as a duplicate.
            val orderA = parseTriggerKeywordRuleFromStorage("ракета+місто")
            val orderB = parseTriggerKeywordRuleFromStorage("місто+ракета")
            assertThat(orderB.normalizedSignature())
                .isEqualTo(orderA.normalizedSignature())
        }

    }
    // endregion

}
