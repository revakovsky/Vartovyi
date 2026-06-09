package com.revakovskyi.vartovyi.usecase.keywords

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.model.WordInputTarget
import com.revakovskyi.vartovyi.result.KeywordSanitizationResult
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * Regression suite over a real user backup: every stored value must survive manual re-entry
 * through the sanitizer AND still match the original incoming message text.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeywordsBackupRoundTripTest {

    private lateinit var useCase: SanitizeWordInputUseCase

    @BeforeEach
    fun setUp() {
        useCase = SanitizeWordInputUseCaseImpl()
    }

    @ParameterizedTest
    @MethodSource("backupKeywords")
    fun `backup keyword is accepted manually and both versions trigger on the original message`(
        storedKeyword: String,
    ) {
        val outcome = useCase(
            rawInput = storedKeyword,
            target = WordInputTarget.TriggerKeyword(selectedType = TriggerKeywordRuleType.WORD),
        )

        assertThat(outcome).isInstanceOf(KeywordSanitizationResult.Sanitized::class)
        val sanitized = outcome as KeywordSanitizationResult.Sanitized

        val importedRule = parseTriggerKeywordRuleFromStorage(storedKeyword)
        val reEnteredRule = parseTriggerKeywordRuleFromStorage(sanitized.storageValue)
        val incomingMessage = buildIncomingMessage(storedKeyword)

        assertThat(importedRule.matches(incomingMessage)).isTrue()
        assertThat(reEnteredRule.matches(incomingMessage)).isTrue()
        assertThat(reEnteredRule.normalizedSignature())
            .isEqualTo(importedRule.normalizedSignature())
    }

    @ParameterizedTest
    @MethodSource("backupStopWords")
    fun `backup stop word is accepted manually and stays identical`(storedStopWord: String) {
        val outcome = useCase(rawInput = storedStopWord, target = WordInputTarget.StopWord)

        assertThat(outcome).isInstanceOf(KeywordSanitizationResult.Sanitized::class)
        assertThat((outcome as KeywordSanitizationResult.Sanitized).storageValue)
            .isEqualTo(storedStopWord)
    }

    @ParameterizedTest
    @MethodSource("backupTelegramChannels")
    fun `backup telegram channel is accepted manually and stays identical`(storedChannel: String) {
        val outcome = useCase(rawInput = storedChannel, target = WordInputTarget.TelegramChannel)

        assertThat(outcome).isInstanceOf(KeywordSanitizationResult.Sanitized::class)
        assertThat((outcome as KeywordSanitizationResult.Sanitized).storageValue)
            .isEqualTo(storedChannel)
    }

    private fun buildIncomingMessage(storedKeyword: String): String {
        val content = storedKeyword
            .removeSurrounding(KeywordRuleFormat.QUOTE)
            .replace(KeywordRuleFormat.ALL_WORDS_SEPARATOR, KeywordRuleFormat.SINGLE_SPACE)

        return "Увага: $content — деталі в каналі"
    }

    private fun backupKeywords(): List<String> = listOf(
        "Аеропорт",
        "Аеропортом",
        "Аэропорт",
        "Ньютона",
        "Город+в+укрытие",
        "ракета+харків",
        "ракета+харьков",
        "\"Ракета!!!\"",
        "\"Ракета⚠️⚠️⚠️\"",
        "\"Цель на город\"",
        "\"Швидкісна на місто\"",
        "нд",
        "\"Попередній напрямок на місто\"",
        "НБ",
        "Аэропортом",
        "\"Ціль на місто\"",
        "місто+в+укриття",
        "\"Балістика на місто\"",
        "\"ракета на місто\"",
        "попередня+загроза+для+міста",
        "\"Переходьте в укриття\"",
        "\"Вихід з Бєлгорода\"",
        "Город+в+укрытии",
        "\"цели на город⚠️⚠️⚠️\"",
        "\"Курс на город⚠️⚠️⚠️\"",
        "\"виходи балістики\"",
        "\"Швидкісна ціль з БНР❗️\"",
        "Турбоатом",
        "\"Нові Будинки\"",
        "\"Льва Ландау\"",
        "\"Новые дома\"",
        "\"this is a test\"",
        "Автоград",
        "Байрона",
        "Павленки",
        "\"Швидкісна на Харкіа\"",
        "\"Вихід з БНР\"",
        "\"выход с БНР\"",
        "\"Цель на Харьков\"",
        "баллистика+на+Харьков",
        "Харків+в+укриття",
        "\"Балістика‼️\"",
        "\"Швидкісна на Харків❗️\"",
    )

    private fun backupStopWords(): List<String> = listOf(
        "пригород",
        "развед",
        "розвід",
        "ППО",
        "ПВО",
    )

    private fun backupTelegramChannels(): List<String> = listOf(
        "TLK News",
        "monitor 1654 | Харків",
        "Кохана",
    )

}
