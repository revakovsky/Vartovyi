package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsNone
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsClearTest : KeywordsViewModelBaseTest() {

    @Test
    fun `OpenClearKeywordsDialog makes the dialog visible`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.OpenClearKeywordsDialog)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isClearKeywordsDialogVisible).isEqualTo(true)
    }

    @Test
    fun `DismissClearKeywordsDialog hides the dialog without clearing data`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.OpenClearKeywordsDialog)
            viewModel.onAction(Action.DismissClearKeywordsDialog)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isClearKeywordsDialogVisible).isFalse()
            coVerify(exactly = 0) { clearKeywordsScreenDataUseCase() }
            assertThat(events).containsNone(Event.KeywordsScreenDataCleared)
        }

    @Test
    fun `ConfirmClearKeywords invokes the use case and emits KeywordsScreenDataCleared`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.OpenClearKeywordsDialog)
            viewModel.onAction(Action.ConfirmClearKeywords)
            advanceUntilIdle()

            coVerify(exactly = 1) { clearKeywordsScreenDataUseCase() }
            assertThat(events).contains(Event.KeywordsScreenDataCleared)
        }

    @Test
    fun `ConfirmClearKeywords resets all transient input and dialog state`() =
        runTest(testDispatcher) {
            every { observeTelegramChannelsUseCase() } returns flowOf(listOf("Повітряні Сили"))

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(Action.SelectTriggerKeywordRuleType(TriggerKeywordRuleType.PHRASE))
            viewModel.onAction(Action.UpdateKeywordInput("ракета"))
            viewModel.onAction(Action.UpdateStopWordInput("розвід"))
            viewModel.onAction(Action.UpdateTelegramChannelInput("канал"))
            viewModel.onAction(Action.RemoveTelegramChannel("Повітряні Сили"))
            viewModel.onAction(Action.OpenClearKeywordsDialog)
            advanceUntilIdle()

            viewModel.onAction(Action.ConfirmClearKeywords)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertThat(state.isClearKeywordsDialogVisible).isFalse()
            assertThat(state.inputKeyword).isEqualTo("")
            assertThat(state.inputStopWord).isEqualTo("")
            assertThat(state.inputTelegramChannel).isEqualTo("")
            assertThat(state.duplicateWord).isNull()
            assertThat(state.pendingRemoval).isNull()
            assertThat(state.selectedTriggerKeywordRuleType)
                .isEqualTo(TriggerKeywordRuleType.WORD)
        }

}