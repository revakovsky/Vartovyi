package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.PendingRemoval
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsTriggerKeywordsTest : KeywordsViewModelBaseTest() {

    @Test
    fun `valid keyword is sanitized, saved and reported`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateKeywordInput("ракета"))
        viewModel.onAction(Action.AddKeyword)
        advanceUntilIdle()

        coVerify(exactly = 1) { addKeywordUseCase("ракета") }
        assertThat(viewModel.state.value.inputKeyword).isEqualTo("")
        assertThat(events).contains(Event.KeywordAdded)
        assertThat(events.filterIsInstance<Event.KeywordNormalized>()).isEmpty()
    }

    @Test
    fun `promoted keyword is stored as ALL_WORDS and emits KeywordNormalized`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateKeywordInput("ракета місто"))
            viewModel.onAction(Action.AddKeyword)
            advanceUntilIdle()

            coVerify(exactly = 1) { addKeywordUseCase("ракета+місто") }
            assertThat(events).contains(Event.KeywordAdded)
            assertThat(events).contains(
                Event.KeywordNormalized(displayValue = "ракета + місто")
            )
        }

    @Test
    fun `cleaned keyword emits KeywordNormalized with stripped value`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateKeywordInput("ракета!!!"))
            viewModel.onAction(Action.AddKeyword)
            advanceUntilIdle()

            coVerify(exactly = 1) { addKeywordUseCase("ракета") }
            assertThat(events).contains(Event.KeywordNormalized(displayValue = "ракета"))
        }

    @Test
    fun `duplicate keyword is not saved and surfaces duplicateWord`() =
        runTest(testDispatcher) {
            every { observeKeywordsUseCase() } returns flowOf(listOf("ракета"))

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateKeywordInput("Ракета"))
            viewModel.onAction(Action.AddKeyword)
            advanceUntilIdle()

            coVerify(exactly = 0) { addKeywordUseCase(any()) }
            assertThat(viewModel.state.value.duplicateWord).isEqualTo("Ракета")
            assertThat(viewModel.state.value.inputKeyword).isEqualTo("")
        }

    @Test
    fun `keyword over limit is rejected with KeywordsMaxReached`() = runTest(testDispatcher) {
        val storedKeywords = List(KeywordsLimits.MAX_TOTAL_KEYWORDS) { index -> "слово$index" }
        every { observeKeywordsUseCase() } returns flowOf(storedKeywords)

        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateKeywordInput("нове"))
        viewModel.onAction(Action.AddKeyword)
        advanceUntilIdle()

        coVerify(exactly = 0) { addKeywordUseCase(any()) }
        assertThat(events).contains(
            Event.KeywordsMaxReached(max = KeywordsLimits.MAX_TOTAL_KEYWORDS)
        )
    }

    @Test
    fun `multiline keyword is rejected with KeywordMultiLineNotAllowed`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateKeywordInput("перший\nдругий"))
            viewModel.onAction(Action.AddKeyword)
            advanceUntilIdle()

            coVerify(exactly = 0) { addKeywordUseCase(any()) }
            assertThat(events).contains(Event.KeywordMultiLineNotAllowed)
        }

    @Test
    fun `too short keyword is rejected with KeywordTermTooShort`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateKeywordInput("a"))
        viewModel.onAction(Action.AddKeyword)
        advanceUntilIdle()

        coVerify(exactly = 0) { addKeywordUseCase(any()) }
        assertThat(events).contains(
            Event.KeywordTermTooShort(minLength = KeywordsLimits.MIN_TERM_LENGTH)
        )
    }

    @Test
    fun `symbols-only keyword is rejected with KeywordStartsWithNonAlphanumeric`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateKeywordInput("!!!@#"))
            viewModel.onAction(Action.AddKeyword)
            advanceUntilIdle()

            coVerify(exactly = 0) { addKeywordUseCase(any()) }
            assertThat(events).contains(Event.KeywordStartsWithNonAlphanumeric)
        }

    @Test
    fun `RemoveKeyword sets pendingRemoval without removing`() = runTest(testDispatcher) {
        val keywordRule = parseTriggerKeywordRuleFromStorage("ракета")

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.RemoveKeyword(keywordRule))
        advanceUntilIdle()

        coVerify(exactly = 0) { removeKeywordUseCase(any()) }
        assertThat(viewModel.state.value.pendingRemoval)
            .isEqualTo(PendingRemoval.Keyword(keywordRule))
    }

    @Test
    fun `ConfirmPendingRemoval removes keyword and emits KeywordRemoved`() =
        runTest(testDispatcher) {
            val keywordRule = parseTriggerKeywordRuleFromStorage("ракета")

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RemoveKeyword(keywordRule))
            viewModel.onAction(Action.ConfirmPendingRemoval)
            advanceUntilIdle()

            coVerify(exactly = 1) { removeKeywordUseCase("ракета") }
            assertThat(events).contains(Event.KeywordRemoved)
            assertThat(viewModel.state.value.pendingRemoval).isNull()
        }

    @Test
    fun `UpdateKeywordInput updates state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateKeywordInput("ракета"))

        assertThat(viewModel.state.value.inputKeyword).isEqualTo("ракета")
    }

    @Test
    fun `SelectTriggerKeywordRuleType updates state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(
            Action.SelectTriggerKeywordRuleType(TriggerKeywordRuleType.PHRASE)
        )

        assertThat(viewModel.state.value.selectedTriggerKeywordRuleType)
            .isEqualTo(TriggerKeywordRuleType.PHRASE)
    }
}
