package com.revakovskyi.vartovyi.usecase.keywords

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.revakovskyi.vartovyi.contract.CrashReporter
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MigrateLegacyChannelFilterUseCaseTest {

    private val keywordsRepository = mockk<KeywordsRepository>()
    private val crashReporter = mockk<CrashReporter>(relaxed = true)

    private lateinit var useCase: MigrateLegacyChannelFilterUseCase

    @BeforeEach
    fun setUp() {
        useCase = MigrateLegacyChannelFilterUseCaseImpl(
            keywordsRepository = keywordsRepository,
            crashReporter = crashReporter,
        )
    }

    @Test
    fun `successful migration is not reported`() = runTest {
        coEvery { keywordsRepository.migrateChannelFilterFlagIfNeeded() } returns true

        useCase()

        verify(exactly = 0) { crashReporter.report(any()) }
    }

    @Test
    fun `silently failed write is reported`() = runTest {
        coEvery { keywordsRepository.migrateChannelFilterFlagIfNeeded() } returns false

        useCase()

        val reported = slot<Throwable>()
        verify(exactly = 1) { crashReporter.report(capture(reported)) }
        assertThat(reported.captured).isInstanceOf(ChannelFilterMigrationException::class)
    }

    @Test
    fun `thrown error is reported with the original cause`() = runTest {
        val failure = IllegalStateException("boom")
        coEvery { keywordsRepository.migrateChannelFilterFlagIfNeeded() } throws failure

        useCase()

        val reported = slot<Throwable>()
        verify(exactly = 1) { crashReporter.report(capture(reported)) }
        assertThat(reported.captured).isInstanceOf(ChannelFilterMigrationException::class)
        assertThat(reported.captured.cause).isEqualTo(failure)
    }

    @Test
    fun `cancellation is rethrown and never reported`() = runTest {
        coEvery {
            keywordsRepository.migrateChannelFilterFlagIfNeeded()
        } throws CancellationException()

        var rethrown: Throwable? = null
        try {
            useCase()
        } catch (cancellation: CancellationException) {
            rethrown = cancellation
        }

        assertThat(rethrown).isNotNull()
        verify(exactly = 0) { crashReporter.report(any()) }
    }

}
