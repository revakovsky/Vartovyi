package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.isNull
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsDialogsTest : KeywordsViewModelBaseTest() {

    @Test
    fun `DismissPendingRemovalDialog clears pendingRemoval without removing`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(Action.RemoveStopWord("Пригород"))
            viewModel.onAction(Action.DismissPendingRemovalDialog)
            advanceUntilIdle()

            coVerify(exactly = 0) { removeStopWordUseCase(any()) }
            assertThat(viewModel.state.value.pendingRemoval).isNull()
        }

    @Test
    fun `DismissDuplicateWordDialog clears duplicateWord`() = runTest(testDispatcher) {
        every { observeStopWordsUseCase() } returns flowOf(listOf("Пригород"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateStopWordInput("пригород"))
        viewModel.onAction(Action.AddStopWord)
        viewModel.onAction(Action.DismissDuplicateWordDialog)
        advanceUntilIdle()

        assertThat(viewModel.state.value.duplicateWord).isNull()
    }
}
