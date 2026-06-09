package com.revakovskyi.vartovyi.usecase.keywords

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.model.KeywordsBackup
import com.revakovskyi.vartovyi.model.KeywordsDataSnapshot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExportKeywordsUseCaseTest {

    private lateinit var repository: FakeKeywordsRepository
    private lateinit var useCase: ExportKeywordsUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeKeywordsRepository()
        useCase = ExportKeywordsUseCaseImpl(keywordsRepository = repository)
    }

    @Test
    fun `invoke returns Success mapping all four sources into the backup`() = runTest {
        repository.snapshot.value = KeywordsDataSnapshot(
            keywords = listOf("ракета", "шахед"),
            stopWords = listOf("навчання", "відбій"),
            telegramChannels = listOf("Повітряні Сили", "TLK News"),
            isTelegramChannelFilterEnabled = true,
        )

        val result = useCase()

        assertThat(result).isInstanceOf(ExportResult.Success::class)
        val backup = decodeBackup((result as ExportResult.Success).jsonContent)

        assertThat(backup.version).isEqualTo(KeywordsBackup.CURRENT_VERSION)
        assertThat(backup.keywords).containsExactly("ракета", "шахед")
        assertThat(backup.stopWords).containsExactly("навчання", "відбій")
        assertThat(backup.telegramChannels).containsExactly("Повітряні Сили", "TLK News")
        assertThat(backup.isTelegramChannelFilterEnabled).isTrue()
    }

    @Test
    fun `invoke serializes empty lists and disabled filter`() = runTest {
        repository.snapshot.value = KeywordsDataSnapshot(
            keywords = emptyList(),
            stopWords = emptyList(),
            telegramChannels = emptyList(),
            isTelegramChannelFilterEnabled = false,
        )

        val result = useCase()

        assertThat(result).isInstanceOf(ExportResult.Success::class)
        val backup = decodeBackup((result as ExportResult.Success).jsonContent)

        assertThat(backup.keywords).isEmpty()
        assertThat(backup.stopWords).isEmpty()
        assertThat(backup.telegramChannels).isEmpty()
        assertThat(backup.isTelegramChannelFilterEnabled).isFalse()
    }

    private fun decodeBackup(jsonContent: String): KeywordsBackup =
        Json.decodeFromString(KeywordsBackup.serializer(), jsonContent)

}
