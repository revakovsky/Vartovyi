package com.revakovskyi.vartovyi.ui.screen.onboarding

import android.util.Log
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactly
import assertk.assertions.containsNone
import assertk.assertions.isEqualTo
import com.revakovskyi.vartovyi.model.OnboardingPage
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingUiContract.Event
import com.revakovskyi.vartovyi.usecase.onboarding.ObserveOnboardingCompletedUseCase
import com.revakovskyi.vartovyi.usecase.onboarding.SetOnboardingCompletedUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val observeOnboardingCompletedUseCase = mockk<ObserveOnboardingCompletedUseCase>()
    private val setOnboardingCompletedUseCase =
        mockk<SetOnboardingCompletedUseCase>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        every { observeOnboardingCompletedUseCase() } returns flowOf(false)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()

        unmockkStatic(Log::class)
    }

    @Test
    fun `Skip marks onboarding as completed and closes the screen`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.Skip)
        advanceUntilIdle()

        coVerify(exactly = 1) { setOnboardingCompletedUseCase() }
        assertThat(events).contains(Event.Close)
    }

    @Test
    fun `Skip shows the hint when onboarding was not completed before`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.Skip)
            advanceUntilIdle()

            assertThat(events).containsExactly(Event.ShowSkipHint, Event.Close)
        }

    @Test
    fun `Skip does not show the hint when the guide was reopened after completion`() =
        runTest(testDispatcher) {
            every { observeOnboardingCompletedUseCase() } returns flowOf(true)
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.Skip)
            advanceUntilIdle()

            assertThat(events).containsNone(Event.ShowSkipHint)
            assertThat(events).contains(Event.Close)
        }

    @Test
    fun `Skip still closes the screen when persisting the completion fails`() =
        runTest(testDispatcher) {
            coEvery { setOnboardingCompletedUseCase() } throws IllegalStateException("write failed")
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.Skip)
            advanceUntilIdle()

            assertThat(events).contains(Event.Close)
        }

    @Test
    fun `Complete marks onboarding as completed and closes the screen`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.Complete)
            advanceUntilIdle()

            coVerify(exactly = 1) { setOnboardingCompletedUseCase() }
            assertThat(events).containsExactly(Event.Close)
        }

    @Test
    fun `Complete still closes the screen when persisting the completion fails`() =
        runTest(testDispatcher) {
            coEvery { setOnboardingCompletedUseCase() } throws IllegalStateException("write failed")
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.Complete)
            advanceUntilIdle()

            assertThat(events).contains(Event.Close)
        }

    @Test
    fun `Start page from the route opens that page`() = runTest(testDispatcher) {
        val viewModel = createViewModel(startPage = OnboardingPage.TELEGRAM.ordinal)
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentPage).isEqualTo(OnboardingPage.TELEGRAM.ordinal)
    }

    @Test
    fun `Start page out of range falls back to the last page`() = runTest(testDispatcher) {
        val viewModel = createViewModel(startPage = OnboardingPage.entries.size + 5)
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentPage)
            .isEqualTo(OnboardingPage.entries.lastIndex)
    }

    @Test
    fun `Negative start page falls back to the first page`() = runTest(testDispatcher) {
        val viewModel = createViewModel(startPage = -3)
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentPage).isEqualTo(0)
    }

    @Test
    fun `NextPage stops at the last page`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        repeat(OnboardingPage.entries.size + 2) { viewModel.onAction(Action.NextPage) }
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentPage)
            .isEqualTo(OnboardingPage.entries.lastIndex)
    }

    @Test
    fun `PreviousPage stops at the first page`() = runTest(testDispatcher) {
        val viewModel = createViewModel(startPage = OnboardingPage.entries.lastIndex)
        advanceUntilIdle()

        repeat(OnboardingPage.entries.size + 2) { viewModel.onAction(Action.PreviousPage) }
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentPage).isEqualTo(0)
    }

    @Test
    fun `PageChanged updates the current page`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.PageChanged(pageIndex = OnboardingPage.TELEGRAM.ordinal))
        advanceUntilIdle()

        assertThat(viewModel.state.value.currentPage).isEqualTo(OnboardingPage.TELEGRAM.ordinal)
    }

    private fun createViewModel(startPage: Int = 0): OnboardingViewModel = OnboardingViewModel(
        startPage = startPage,
        observeOnboardingCompletedUseCase = observeOnboardingCompletedUseCase,
        setOnboardingCompletedUseCase = setOnboardingCompletedUseCase,
    )

    private fun TestScope.collectEvents(viewModel: OnboardingViewModel): List<Event> {
        val collectedEvents = mutableListOf<Event>()

        backgroundScope.launch(testDispatcher) {
            viewModel.events.toList(collectedEvents)
        }

        return collectedEvents
    }
}
