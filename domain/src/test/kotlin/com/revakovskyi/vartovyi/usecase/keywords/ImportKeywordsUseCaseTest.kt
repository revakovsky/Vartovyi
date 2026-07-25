package com.revakovskyi.vartovyi.usecase.keywords

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.revakovskyi.vartovyi.model.ImportStrategy
import com.revakovskyi.vartovyi.model.KeywordsBackup
import com.revakovskyi.vartovyi.model.KeywordsDataSnapshot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private fun backupJson(
    keywords: List<String> = emptyList(),
    stopWords: List<String> = emptyList(),
    telegramChannels: List<String> = emptyList(),
    version: Int = KeywordsBackup.CURRENT_VERSION,
): String {
    val backup = KeywordsBackup(
        version = version,
        keywords = keywords,
        stopWords = stopWords,
        telegramChannels = telegramChannels,
    )

    return Json.encodeToString(KeywordsBackup.serializer(), backup)
}

class ImportKeywordsUseCaseTest {

    private lateinit var repository: FakeKeywordsRepository
    private lateinit var useCase: ImportKeywordsUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeKeywordsRepository()
        useCase = ImportKeywordsUseCaseImpl(keywordsRepository = repository)
    }

    @Nested
    inner class Replace {

        @Test
        fun `replaces all existing data with backup contents`() = runTest {
            repository.snapshot.value = KeywordsDataSnapshot(
                keywords = listOf("старе"),
                stopWords = listOf("стоп"),
                telegramChannels = listOf("Old Channel"),
            )

            val result = useCase(
                jsonContent = backupJson(
                    keywords = listOf("ракета"),
                    stopWords = listOf("ппо"),
                    telegramChannels = listOf("New Channel"),
                ),
                strategy = ImportStrategy.REPLACE,
            )

            assertThat(result).isEqualTo(
                ImportResult.Success(
                    strategy = ImportStrategy.REPLACE,
                    addedCount = 3,
                    skippedCount = 0,
                )
            )
            assertThat(repository.snapshot.value).isEqualTo(
                KeywordsDataSnapshot(
                    keywords = listOf("ракета"),
                    stopWords = listOf("ппо"),
                    telegramChannels = listOf("New Channel"),
                )
            )
        }

        @Test
        fun `deduplicates entries inside the backup file itself`() = runTest {
            val result = useCase(
                jsonContent = backupJson(
                    keywords = listOf("ракета", "Ракета"),
                    stopWords = listOf("ППО", "ппо"),
                    telegramChannels = listOf("Channel", "Channel"),
                ),
                strategy = ImportStrategy.REPLACE,
            )

            assertThat(result).isEqualTo(
                ImportResult.Success(
                    strategy = ImportStrategy.REPLACE,
                    addedCount = 3,
                    skippedCount = 3,
                )
            )
            assertThat(repository.snapshot.value.keywords).containsExactly("ракета")
            assertThat(repository.snapshot.value.stopWords).containsExactly("ППО")
            assertThat(repository.snapshot.value.telegramChannels).containsExactly("Channel")
        }

    }

    @Nested
    inner class Merge {

        @Test
        fun `skips keyword duplicated by normalized signature regardless of case`() = runTest {
            repository.snapshot.value = repository.snapshot.value.copy(
                keywords = listOf("ракета"),
            )

            val result = useCase(
                jsonContent = backupJson(keywords = listOf("Ракета")),
                strategy = ImportStrategy.MERGE,
            )

            assertThat(result).isEqualTo(
                ImportResult.Success(
                    strategy = ImportStrategy.MERGE,
                    addedCount = 0,
                    skippedCount = 1,
                )
            )
            assertThat(repository.snapshot.value.keywords).containsExactly("ракета")
        }

        @Test
        fun `keeps the same word in a different rule mode`() = runTest {
            repository.snapshot.value = repository.snapshot.value.copy(
                keywords = listOf("ракета"),
            )

            val result = useCase(
                jsonContent = backupJson(keywords = listOf("\"ракета\"")),
                strategy = ImportStrategy.MERGE,
            )

            assertThat(result).isEqualTo(
                ImportResult.Success(
                    strategy = ImportStrategy.MERGE,
                    addedCount = 1,
                    skippedCount = 0,
                )
            )
            assertThat(repository.snapshot.value.keywords)
                .containsExactly("ракета", "\"ракета\"")
        }

        @Test
        fun `skips ALL_WORDS keyword with the same terms in a different order`() = runTest {
            repository.snapshot.value = repository.snapshot.value.copy(
                keywords = listOf("ракета+харків"),
            )

            val result = useCase(
                jsonContent = backupJson(keywords = listOf("Харків+Ракета")),
                strategy = ImportStrategy.MERGE,
            )

            assertThat(result).isEqualTo(
                ImportResult.Success(
                    strategy = ImportStrategy.MERGE,
                    addedCount = 0,
                    skippedCount = 1,
                )
            )
            assertThat(repository.snapshot.value.keywords).containsExactly("ракета+харків")
        }

        @Test
        fun `deduplicates stop words ignoring case`() = runTest {
            repository.snapshot.value = repository.snapshot.value.copy(
                stopWords = listOf("ППО"),
            )

            val result = useCase(
                jsonContent = backupJson(stopWords = listOf("ппо", "розвід")),
                strategy = ImportStrategy.MERGE,
            )

            assertThat(result).isEqualTo(
                ImportResult.Success(
                    strategy = ImportStrategy.MERGE,
                    addedCount = 1,
                    skippedCount = 1,
                )
            )
            assertThat(repository.snapshot.value.stopWords).containsExactly("ППО", "розвід")
        }

        @Test
        fun `compares telegram channels exactly`() = runTest {
            repository.snapshot.value = repository.snapshot.value.copy(
                telegramChannels = listOf("News"),
            )

            val result = useCase(
                jsonContent = backupJson(telegramChannels = listOf("news", "News")),
                strategy = ImportStrategy.MERGE,
            )

            assertThat(result).isEqualTo(
                ImportResult.Success(
                    strategy = ImportStrategy.MERGE,
                    addedCount = 1,
                    skippedCount = 1,
                )
            )
            assertThat(repository.snapshot.value.telegramChannels)
                .containsExactly("News", "news")
        }

    }

    @Nested
    inner class Failures {

        @Test
        fun `returns WriteError and keeps existing data when the write fails`() = runTest {
            repository.snapshot.value = repository.snapshot.value.copy(
                keywords = listOf("старе"),
            )
            repository.shouldFailWrite = true

            val result = useCase(
                jsonContent = backupJson(keywords = listOf("нове")),
                strategy = ImportStrategy.REPLACE,
            )

            assertThat(result).isEqualTo(ImportResult.WriteError)
            assertThat(repository.snapshot.value.keywords).containsExactly("старе")
        }

        @Test
        fun `returns InvalidFormat without writing for malformed json`() = runTest {
            val result = useCase(jsonContent = "not a json", strategy = ImportStrategy.REPLACE)

            assertThat(result).isInstanceOf(ImportResult.InvalidFormat::class)
            assertThat(repository.replaceAllCallCount).isEqualTo(0)
        }

        @Test
        fun `returns UnsupportedVersion without writing for a newer backup version`() = runTest {
            val result = useCase(
                jsonContent = backupJson(version = KeywordsBackup.CURRENT_VERSION + 1),
                strategy = ImportStrategy.MERGE,
            )

            assertThat(result).isEqualTo(
                ImportResult.UnsupportedVersion(fileVersion = KeywordsBackup.CURRENT_VERSION + 1)
            )
            assertThat(repository.replaceAllCallCount).isEqualTo(0)
        }

    }

    @Test
    fun `merge into an empty store behaves like replace`() = runTest {
        val mergeResult = useCase(
            jsonContent = backupJson(
                keywords = listOf("ракета"),
                stopWords = listOf("ппо"),
                telegramChannels = listOf("Channel"),
            ),
            strategy = ImportStrategy.MERGE,
        )

        assertThat(mergeResult).isEqualTo(
            ImportResult.Success(
                strategy = ImportStrategy.MERGE,
                addedCount = 3,
                skippedCount = 0,
            )
        )
        assertThat(repository.snapshot.value).isEqualTo(
            KeywordsDataSnapshot(
                keywords = listOf("ракета"),
                stopWords = listOf("ппо"),
                telegramChannels = listOf("Channel"),
            )
        )
    }

}
