package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.constants.POPULAR_TELEGRAM_CHANNELS
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.State
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import org.junit.jupiter.api.Test

class KeywordsStateDerivationTest {

    private val sampleKeyword = parseTriggerKeywordRuleFromStorage("ракета")

    @Test
    fun `hasKeywordDataToClear is false when everything is empty`() {
        val state = State()

        assertThat(state.hasKeywordDataToClear).isFalse()
    }

    @Test
    fun `hasKeywordDataToClear is true when only keywords are present`() {
        val state = State(keywords = listOf(sampleKeyword))

        assertThat(state.hasKeywordDataToClear).isTrue()
    }

    @Test
    fun `hasKeywordDataToClear is true when only stop words are present`() {
        val state = State(stopWords = listOf("розвід"))

        assertThat(state.hasKeywordDataToClear).isTrue()
    }

    @Test
    fun `hasKeywordDataToClear is true when only telegram channels are present`() {
        val state = State(telegramChannels = listOf("Повітряні Сили"))

        assertThat(state.hasKeywordDataToClear).isTrue()
    }

    @Test
    fun `canExport is false when everything is empty`() {
        val state = State()

        assertThat(state.canExport).isFalse()
    }

    @Test
    fun `canExport is true when only stop words are present`() {
        val state = State(stopWords = listOf("розвід"))

        assertThat(state.canExport).isTrue()
    }

    @Test
    fun `notYetAddedTelegramChannels returns all suggestions when none is added`() {
        val state = State()

        assertThat(state.notYetAddedTelegramChannels).isEqualTo(POPULAR_TELEGRAM_CHANNELS)
    }

    @Test
    fun `notYetAddedTelegramChannels hides an already added channel ignoring case`() {
        val added = POPULAR_TELEGRAM_CHANNELS.first().displayName

        val state = State(telegramChannels = listOf(added.uppercase()))

        assertThat(state.notYetAddedTelegramChannels)
            .isEqualTo(POPULAR_TELEGRAM_CHANNELS.drop(1))
    }

    @Test
    fun `hasSuggestedTelegramChannels is false once every default channel is added`() {
        val allDisplayNames = POPULAR_TELEGRAM_CHANNELS.map { channel -> channel.displayName }

        val state = State(telegramChannels = allDisplayNames)

        assertThat(state.hasSuggestedTelegramChannels).isFalse()
        assertThat(state.notYetAddedTelegramChannels).isEmpty()
        assertThat(state.suggestedTelegramChannels).isEmpty()
    }

    @Test
    fun `button stays visible but list is empty when the query matches no channel`() {
        val state = State(inputTelegramChannel = "zzzzzznomatch")

        assertThat(state.hasSuggestedTelegramChannels).isTrue()
        assertThat(state.suggestedTelegramChannels).isEmpty()
    }

}
