package com.revakovskyi.vartovyi.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.constants.DEFAULT_STOP_WORDS_SEED
import com.revakovskyi.vartovyi.data.datastore.KeywordsDataStore
import com.revakovskyi.vartovyi.model.KeywordsDataSnapshot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KeywordsRepositoryImplTest {

    private lateinit var keywordsDataStore: KeywordsDataStore
    private lateinit var repository: KeywordsRepositoryImpl

    @BeforeEach
    fun setUp() {
        keywordsDataStore = mockk(relaxed = true)
        repository = KeywordsRepositoryImpl(keywordsDataStore = keywordsDataStore)
    }

    @Nested
    inner class AddInputNormalization {

        @Test
        fun `addKeyword with blank input returns true without touching the data store`() = runTest {
            val result = repository.addKeyword("   ")

            assertThat(result).isTrue()
            coVerify(exactly = 0) { keywordsDataStore.addKeywordIfMissing(any()) }
        }

        @Test
        fun `addKeyword trims surrounding whitespace before delegating`() = runTest {
            coEvery { keywordsDataStore.addKeywordIfMissing("ракета") } returns true

            val result = repository.addKeyword("   ракета   ")

            assertThat(result).isTrue()
            coVerify(exactly = 1) { keywordsDataStore.addKeywordIfMissing("ракета") }
        }

        @Test
        fun `addStopWord with blank input returns true without touching the data store`() = runTest {
            val result = repository.addStopWord("  \t ")

            assertThat(result).isTrue()
            coVerify(exactly = 0) { keywordsDataStore.addStopWordIfMissing(any()) }
        }

        @Test
        fun `addStopWord trims surrounding whitespace before delegating`() = runTest {
            coEvery { keywordsDataStore.addStopWordIfMissing("Пригород") } returns true

            val result = repository.addStopWord("  Пригород ")

            assertThat(result).isTrue()
            coVerify(exactly = 1) { keywordsDataStore.addStopWordIfMissing("Пригород") }
        }

        @Test
        fun `addTelegramChannel with blank input returns true without touching the data store`() =
            runTest {
                val result = repository.addTelegramChannel("    ")

                assertThat(result).isTrue()
                coVerify(exactly = 0) { keywordsDataStore.addTelegramChannelIfMissing(any()) }
            }

        @Test
        fun `addTelegramChannel trims surrounding whitespace before delegating`() = runTest {
            coEvery { keywordsDataStore.addTelegramChannelIfMissing("TLK News") } returns true

            val result = repository.addTelegramChannel("  TLK News  ")

            assertThat(result).isTrue()
            coVerify(exactly = 1) { keywordsDataStore.addTelegramChannelIfMissing("TLK News") }
        }
    }

    @Nested
    inner class RestoreDefaults {

        @Test
        fun `restoreDefaultKeywords merges the keywords seed and returns the data store count`() =
            runTest {
                coEvery { keywordsDataStore.mergeKeywords(DEFAULT_KEYWORDS_SEED) } returns 3

                val result = repository.restoreDefaultKeywords()

                assertThat(result).isEqualTo(3)
                coVerify(exactly = 1) { keywordsDataStore.mergeKeywords(DEFAULT_KEYWORDS_SEED) }
            }

        @Test
        fun `restoreDefaultStopWords merges the stop-words seed and returns the data store count`() =
            runTest {
                coEvery { keywordsDataStore.mergeStopWords(DEFAULT_STOP_WORDS_SEED) } returns 2

                val result = repository.restoreDefaultStopWords()

                assertThat(result).isEqualTo(2)
                coVerify(exactly = 1) { keywordsDataStore.mergeStopWords(DEFAULT_STOP_WORDS_SEED) }
            }
    }

    @Nested
    inner class ReplaceAllData {

        @Test
        fun `replaceAllKeywordsData forwards the transform and returns the data store result`() =
            runTest {
                val transform: (KeywordsDataSnapshot) -> KeywordsDataSnapshot = { currentData ->
                    currentData
                }
                coEvery { keywordsDataStore.replaceAllData(transform) } returns true

                val result = repository.replaceAllKeywordsData(transform)

                assertThat(result).isTrue()
                coVerify(exactly = 1) { keywordsDataStore.replaceAllData(transform) }
            }

        @Test
        fun `replaceAllKeywordsData returns false when the data store write fails`() = runTest {
            coEvery { keywordsDataStore.replaceAllData(any()) } returns false

            val result = repository.replaceAllKeywordsData { currentData -> currentData }

            assertThat(result).isFalse()
        }
    }

}
