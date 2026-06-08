package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsNone
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.revakovskyi.vartovyi.model.ImportStrategy
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.usecase.keywords.ImportResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsImportTest : KeywordsViewModelBaseTest() {

    @Test
    fun `RequestImport with existing data shows the strategy dialog without launching picker`() =
        runTest(testDispatcher) {
            every { observeKeywordsUseCase() } returns flowOf(listOf("ракета"))

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RequestImport)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isImportStrategyDialogVisible).isEqualTo(true)
            assertThat(events).containsNone(Event.LaunchImportFilePicker)
        }

    @Test
    fun `RequestImport without data launches picker silently with REPLACE strategy`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RequestImport)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isImportStrategyDialogVisible).isEqualTo(false)
            assertThat(viewModel.state.value.pendingImportStrategy)
                .isEqualTo(ImportStrategy.REPLACE)
            assertThat(events).contains(Event.LaunchImportFilePicker)
        }

    @Test
    fun `SelectImportStrategy hides dialog, stores strategy and launches picker`() =
        runTest(testDispatcher) {
            every { observeKeywordsUseCase() } returns flowOf(listOf("ракета"))

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RequestImport)
            viewModel.onAction(Action.SelectImportStrategy(ImportStrategy.MERGE))
            advanceUntilIdle()

            assertThat(viewModel.state.value.isImportStrategyDialogVisible).isEqualTo(false)
            assertThat(viewModel.state.value.pendingImportStrategy)
                .isEqualTo(ImportStrategy.MERGE)
            assertThat(events).contains(Event.LaunchImportFilePicker)
        }

    @Test
    fun `DismissImportStrategyDialog clears dialog and pending strategy without picker`() =
        runTest(testDispatcher) {
            every { observeKeywordsUseCase() } returns flowOf(listOf("ракета"))

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RequestImport)
            viewModel.onAction(Action.DismissImportStrategyDialog)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isImportStrategyDialogVisible).isEqualTo(false)
            assertThat(viewModel.state.value.pendingImportStrategy).isNull()
            assertThat(events).containsNone(Event.LaunchImportFilePicker)
        }

    @Test
    fun `ImportKeywords calls use case with selected strategy and emits success with counts`() =
        runTest(testDispatcher) {
            every { observeKeywordsUseCase() } returns flowOf(listOf("ракета"))
            coEvery { importKeywordsUseCase(any(), any()) } returns ImportResult.Success(
                strategy = ImportStrategy.MERGE,
                addedCount = 2,
                skippedCount = 1,
            )

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RequestImport)
            viewModel.onAction(Action.SelectImportStrategy(ImportStrategy.MERGE))
            viewModel.onAction(Action.ImportKeywords("{}"))
            advanceUntilIdle()

            coVerify(exactly = 1) { importKeywordsUseCase("{}", ImportStrategy.MERGE) }
            assertThat(viewModel.state.value.pendingImportStrategy).isNull()
            assertThat(events).contains(
                Event.KeywordsImportSuccess(
                    strategy = ImportStrategy.MERGE,
                    addedCount = 2,
                    skippedCount = 1,
                )
            )
        }

    @Test
    fun `ImportKeywords without pending strategy defaults to REPLACE`() =
        runTest(testDispatcher) {
            coEvery { importKeywordsUseCase(any(), any()) } returns ImportResult.Success(
                strategy = ImportStrategy.REPLACE,
                addedCount = 1,
                skippedCount = 0,
            )

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(Action.ImportKeywords("{}"))
            advanceUntilIdle()

            coVerify(exactly = 1) { importKeywordsUseCase("{}", ImportStrategy.REPLACE) }
        }

    @Test
    fun `ImportKeywords with InvalidFormat result emits KeywordsImportInvalidFormat`() =
        runTest(testDispatcher) {
            coEvery { importKeywordsUseCase(any(), any()) } returns
                    ImportResult.InvalidFormat(IllegalArgumentException("bad json"))

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.ImportKeywords("{}"))
            advanceUntilIdle()

            assertThat(events).contains(Event.KeywordsImportInvalidFormat)
        }

    @Test
    fun `ImportKeywords with WriteError result emits KeywordsImportWriteError`() =
        runTest(testDispatcher) {
            coEvery { importKeywordsUseCase(any(), any()) } returns ImportResult.WriteError

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.ImportKeywords("{}"))
            advanceUntilIdle()

            assertThat(events).contains(Event.KeywordsImportWriteError)
        }

    @Test
    fun `ImportKeywords with UnsupportedVersion result emits event with the file version`() =
        runTest(testDispatcher) {
            coEvery { importKeywordsUseCase(any(), any()) } returns
                    ImportResult.UnsupportedVersion(fileVersion = 99)

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.ImportKeywords("{}"))
            advanceUntilIdle()

            assertThat(events).contains(Event.KeywordsImportUnsupportedVersion(fileVersion = 99))
        }
}
