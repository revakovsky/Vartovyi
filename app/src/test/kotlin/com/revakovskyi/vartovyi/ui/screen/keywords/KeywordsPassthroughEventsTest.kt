package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.contains
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsPassthroughEventsTest : KeywordsViewModelBaseTest() {

    @Test
    fun `CopyChip emits ChipCopied with the same text`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.CopyChip("ракета"))
        advanceUntilIdle()

        assertThat(events).contains(Event.ChipCopied("ракета"))
    }

    @Test
    fun `NotifyExportSuccess emits KeywordsExportSuccess`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.NotifyExportSuccess)
        advanceUntilIdle()

        assertThat(events).contains(Event.KeywordsExportSuccess)
    }

    @Test
    fun `NotifyExportError emits KeywordsExportError`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.NotifyExportError)
        advanceUntilIdle()

        assertThat(events).contains(Event.KeywordsExportError)
    }

    @Test
    fun `NotifyImportReadError emits KeywordsImportInvalidFormat`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.NotifyImportReadError)
        advanceUntilIdle()

        assertThat(events).contains(Event.KeywordsImportInvalidFormat)
    }

    @Test
    fun `NotifyImportFileTooLarge emits KeywordsImportFileTooLarge`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.NotifyImportFileTooLarge)
        advanceUntilIdle()

        assertThat(events).contains(Event.KeywordsImportFileTooLarge)
    }

}
