package com.revakovskyi.vartovyi.constants

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.model.PopularTelegramChannel
import com.revakovskyi.vartovyi.model.WordInputTarget
import com.revakovskyi.vartovyi.result.KeywordSanitizationResult
import com.revakovskyi.vartovyi.usecase.keywords.SanitizeWordInputUseCase
import com.revakovskyi.vartovyi.usecase.keywords.SanitizeWordInputUseCaseImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PopularTelegramChannelsTest {

    private lateinit var useCase: SanitizeWordInputUseCase

    @BeforeEach
    fun setUp() {
        useCase = SanitizeWordInputUseCaseImpl()
    }

    @ParameterizedTest
    @MethodSource("popularChannels")
    fun `display name is accepted by the sanitizer and stored verbatim`(
        channel: PopularTelegramChannel,
    ) {
        val outcome = useCase(
            rawInput = channel.displayName,
            target = WordInputTarget.TelegramChannel,
        )

        assertThat(outcome).isInstanceOf(KeywordSanitizationResult.Sanitized::class)
        assertThat((outcome as KeywordSanitizationResult.Sanitized).storageValue)
            .isEqualTo(channel.displayName)
    }

    @ParameterizedTest
    @MethodSource("popularChannels")
    fun `handle starts with the at sign`(channel: PopularTelegramChannel) {
        assertThat(channel.handle.startsWith("@")).isTrue()
    }

    @Test
    fun `display names are unique ignoring case`() {
        val distinctNames = POPULAR_TELEGRAM_CHANNELS
            .map { channel -> channel.displayName.lowercase() }
            .distinct()

        assertThat(distinctNames.size).isEqualTo(POPULAR_TELEGRAM_CHANNELS.size)
    }

    @Test
    fun `handles are unique`() {
        val distinctHandles = POPULAR_TELEGRAM_CHANNELS
            .map { channel -> channel.handle.lowercase() }
            .distinct()

        assertThat(distinctHandles.size).isEqualTo(POPULAR_TELEGRAM_CHANNELS.size)
    }

    private fun popularChannels(): List<PopularTelegramChannel> = POPULAR_TELEGRAM_CHANNELS

}
