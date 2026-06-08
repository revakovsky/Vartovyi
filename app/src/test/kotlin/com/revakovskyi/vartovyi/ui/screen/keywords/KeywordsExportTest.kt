package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsNone
import assertk.assertions.isEqualTo
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.keywords.model.ExportDestination
import com.revakovskyi.vartovyi.usecase.keywords.ExportResult
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsExportTest : KeywordsViewModelBaseTest() {

    @Test
    fun `RequestExport shows the destination dialog`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.RequestExport)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isExportDestinationDialogVisible).isEqualTo(true)
        assertThat(events).containsNone(
            Event.LaunchExportFilePicker(""),
            Event.LaunchExportShareSheet(""),
        )
    }

    @Test
    fun `SelectExportDestination SAVE_TO_FILE emits LaunchExportFilePicker and hides dialog`() =
        runTest(testDispatcher) {
            coEvery { exportKeywordsUseCase() } returns ExportResult.Success(jsonContent = "{}")

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RequestExport)
            viewModel.onAction(
                Action.SelectExportDestination(ExportDestination.SAVE_TO_FILE)
            )
            advanceUntilIdle()

            assertThat(viewModel.state.value.isExportDestinationDialogVisible).isEqualTo(false)
            assertThat(events).contains(Event.LaunchExportFilePicker(jsonContent = "{}"))
        }

    @Test
    fun `SelectExportDestination SHARE emits LaunchExportShareSheet`() =
        runTest(testDispatcher) {
            coEvery { exportKeywordsUseCase() } returns ExportResult.Success(jsonContent = "{}")

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.SelectExportDestination(ExportDestination.SHARE))
            advanceUntilIdle()

            assertThat(events).contains(Event.LaunchExportShareSheet(jsonContent = "{}"))
        }

    @Test
    fun `SelectExportDestination with export error emits KeywordsExportError`() =
        runTest(testDispatcher) {
            coEvery { exportKeywordsUseCase() } returns
                    ExportResult.Error(IllegalStateException("boom"))

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.SelectExportDestination(ExportDestination.SHARE))
            advanceUntilIdle()

            assertThat(events).contains(Event.KeywordsExportError)
        }

    @Test
    fun `DismissExportDestinationDialog hides the dialog without emitting events`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RequestExport)
            viewModel.onAction(Action.DismissExportDestinationDialog)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isExportDestinationDialogVisible).isEqualTo(false)
            assertThat(events).containsNone(
                Event.LaunchExportFilePicker(""),
                Event.LaunchExportShareSheet(""),
            )
        }
}
