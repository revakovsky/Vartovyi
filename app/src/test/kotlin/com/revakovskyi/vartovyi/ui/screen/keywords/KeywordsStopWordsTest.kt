package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.PendingRemoval
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsStopWordsTest : KeywordsViewModelBaseTest() {

    @Test
    fun `valid stop word is sanitized, saved and reported`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateStopWordInput("Пригород"))
        viewModel.onAction(Action.AddStopWord)
        advanceUntilIdle()

        coVerify(exactly = 1) { addStopWordUseCase("Пригород") }
        assertThat(viewModel.state.value.inputStopWord).isEqualTo("")
        assertThat(events).contains(Event.StopWordAdded)
        assertThat(events.filterIsInstance<Event.KeywordNormalized>()).isEmpty()
    }

    @Test
    fun `normalized stop word is stored cleaned and emits KeywordNormalized`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateStopWordInput("off   topic"))
            viewModel.onAction(Action.AddStopWord)
            advanceUntilIdle()

            coVerify(exactly = 1) { addStopWordUseCase("off topic") }
            assertThat(events).contains(Event.StopWordAdded)
            assertThat(events).contains(Event.KeywordNormalized(displayValue = "off topic"))
        }

    @Test
    fun `multiline stop word is rejected with KeywordMultiLineNotAllowed`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateStopWordInput("перший\nдругий"))
            viewModel.onAction(Action.AddStopWord)
            advanceUntilIdle()

            coVerify(exactly = 0) { addStopWordUseCase(any()) }
            assertThat(events).contains(Event.KeywordMultiLineNotAllowed)
        }

    @Test
    fun `too short stop word is rejected with KeywordTermTooShort`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateStopWordInput("a"))
        viewModel.onAction(Action.AddStopWord)
        advanceUntilIdle()

        coVerify(exactly = 0) { addStopWordUseCase(any()) }
        assertThat(events).contains(
            Event.KeywordTermTooShort(minLength = KeywordsLimits.MIN_TERM_LENGTH)
        )
    }

    @Test
    fun `duplicate stop word is not saved and surfaces duplicateWord`() =
        runTest(testDispatcher) {
            every { observeStopWordsUseCase() } returns flowOf(listOf("Пригород"))

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateStopWordInput("пригород"))
            viewModel.onAction(Action.AddStopWord)
            advanceUntilIdle()

            coVerify(exactly = 0) { addStopWordUseCase(any()) }
            assertThat(viewModel.state.value.duplicateWord).isEqualTo("пригород")
            assertThat(viewModel.state.value.inputStopWord).isEqualTo("")
        }

    @Test
    fun `UpdateStopWordInput updates state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateStopWordInput("розвід"))

        assertThat(viewModel.state.value.inputStopWord).isEqualTo("розвід")
    }

    @Test
    fun `empty stop word input is ignored silently`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateStopWordInput("   "))
        viewModel.onAction(Action.AddStopWord)
        advanceUntilIdle()

        coVerify(exactly = 0) { addStopWordUseCase(any()) }
        assertThat(events).isEmpty()
    }

    @Test
    fun `RemoveStopWord sets pendingRemoval without removing`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.RemoveStopWord("Пригород"))
        advanceUntilIdle()

        coVerify(exactly = 0) { removeStopWordUseCase(any()) }
        assertThat(viewModel.state.value.pendingRemoval)
            .isEqualTo(PendingRemoval.StopWord("Пригород"))
    }

    @Test
    fun `ConfirmPendingRemoval removes stop word and emits StopWordRemoved`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RemoveStopWord("Пригород"))
            viewModel.onAction(Action.ConfirmPendingRemoval)
            advanceUntilIdle()

            coVerify(exactly = 1) { removeStopWordUseCase("Пригород") }
            assertThat(events).contains(Event.StopWordRemoved)
            assertThat(viewModel.state.value.pendingRemoval).isNull()
        }
}
