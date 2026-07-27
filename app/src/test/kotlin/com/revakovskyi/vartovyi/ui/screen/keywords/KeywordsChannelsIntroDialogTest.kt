package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsChannelsIntroDialogTest : KeywordsViewModelBaseTest() {

    @Test
    fun `intro dialog is shown on entry while it is not hidden`() = runTest(testDispatcher) {
        every { observeKeywordsChannelsIntroHiddenUseCase() } returns flowOf(false)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isChannelsIntroDialogVisible).isTrue()
        coVerify(exactly = 0) { setKeywordsChannelsIntroHiddenUseCase() }
    }

    @Test
    fun `intro dialog is not shown when channels are already configured`() =
        runTest(testDispatcher) {
            every { observeKeywordsChannelsIntroHiddenUseCase() } returns flowOf(false)
            every { observeTelegramChannelsUseCase() } returns flowOf(listOf("Тривога Харків"))

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertThat(viewModel.state.value.isChannelsIntroDialogVisible).isFalse()
            coVerify(exactly = 0) { setKeywordsChannelsIntroHiddenUseCase() }
        }

    @Test
    fun `intro dialog stays hidden once the user has opted out`() = runTest(testDispatcher) {
        every { observeKeywordsChannelsIntroHiddenUseCase() } returns flowOf(true)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isChannelsIntroDialogVisible).isFalse()
    }

    @Test
    fun `dismiss hides dialog but keeps showing it next time`() = runTest(testDispatcher) {
        every { observeKeywordsChannelsIntroHiddenUseCase() } returns flowOf(false)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.DismissChannelsIntroDialog)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isChannelsIntroDialogVisible).isFalse()
        coVerify(exactly = 0) { setKeywordsChannelsIntroHiddenUseCase() }
    }

    @Test
    fun `hide forever persists the hidden flag`() = runTest(testDispatcher) {
        every { observeKeywordsChannelsIntroHiddenUseCase() } returns flowOf(false)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.HideChannelsIntroDialogForever)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isChannelsIntroDialogVisible).isFalse()
        coVerify(exactly = 1) { setKeywordsChannelsIntroHiddenUseCase() }
    }

}
