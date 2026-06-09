package com.revakovskyi.vartovyi.ui.screen.keywords

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsNone
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.constants.POPULAR_TELEGRAM_CHANNELS
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
class KeywordsTelegramChannelsTest : KeywordsViewModelBaseTest() {

    @Test
    fun `valid channel is sanitized, saved and reported`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("Повітряні цілі Київ"))
        viewModel.onAction(Action.AddTelegramChannel)
        advanceUntilIdle()

        coVerify(exactly = 1) { addTelegramChannelUseCase("Повітряні цілі Київ") }
        assertThat(viewModel.state.value.inputTelegramChannel).isEqualTo("")
        assertThat(events).contains(Event.TelegramChannelAdded)
        assertThat(events.filterIsInstance<Event.KeywordNormalized>()).isEmpty()
    }

    @Test
    fun `channel preserves edge emoji in stored value`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("🚨 Повітряні цілі Київ"))
        viewModel.onAction(Action.AddTelegramChannel)
        advanceUntilIdle()

        coVerify(exactly = 1) { addTelegramChannelUseCase("🚨 Повітряні цілі Київ") }
        assertThat(events).contains(Event.TelegramChannelAdded)
    }

    @Test
    fun `multiline channel is rejected with KeywordMultiLineNotAllowed`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateTelegramChannelInput("канал\nдругий"))
            viewModel.onAction(Action.AddTelegramChannel)
            advanceUntilIdle()

            coVerify(exactly = 0) { addTelegramChannelUseCase(any()) }
            assertThat(events).contains(Event.KeywordMultiLineNotAllowed)
        }

    @Test
    fun `emoji-only channel is rejected and not saved`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("🚨🚨"))
        viewModel.onAction(Action.AddTelegramChannel)
        advanceUntilIdle()

        coVerify(exactly = 0) { addTelegramChannelUseCase(any()) }
        assertThat(events).containsNone(Event.TelegramChannelAdded)
    }

    @Test
    fun `duplicate channel is not saved and surfaces duplicateWord`() =
        runTest(testDispatcher) {
            every { observeTelegramChannelsUseCase() } returns flowOf(listOf("Повітряні Сили"))

            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateTelegramChannelInput("повітряні сили"))
            viewModel.onAction(Action.AddTelegramChannel)
            advanceUntilIdle()

            coVerify(exactly = 0) { addTelegramChannelUseCase(any()) }
            assertThat(viewModel.state.value.duplicateWord).isEqualTo("повітряні сили")
            assertThat(viewModel.state.value.inputTelegramChannel).isEqualTo("")
        }

    @Test
    fun `UpdateTelegramChannelInput updates state`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("@air_alert_ua"))

        assertThat(viewModel.state.value.inputTelegramChannel).isEqualTo("@air_alert_ua")
    }

    @Test
    fun `empty channel input is ignored silently`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("   "))
        viewModel.onAction(Action.AddTelegramChannel)
        advanceUntilIdle()

        coVerify(exactly = 0) { addTelegramChannelUseCase(any()) }
        assertThat(events).isEmpty()
    }

    @Test
    fun `too short channel is rejected with KeywordTermTooShort`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val events = collectEvents(viewModel)
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("a"))
        viewModel.onAction(Action.AddTelegramChannel)
        advanceUntilIdle()

        coVerify(exactly = 0) { addTelegramChannelUseCase(any()) }
        assertThat(events).contains(
            Event.KeywordTermTooShort(minLength = KeywordsLimits.MIN_TERM_LENGTH)
        )
    }

    @Test
    fun `normalized channel is stored cleaned and emits KeywordNormalized`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.UpdateTelegramChannelInput("  Повітряні   Сили  "))
            viewModel.onAction(Action.AddTelegramChannel)
            advanceUntilIdle()

            coVerify(exactly = 1) { addTelegramChannelUseCase("Повітряні Сили") }
            assertThat(events).contains(Event.TelegramChannelAdded)
            assertThat(events).contains(Event.KeywordNormalized(displayValue = "Повітряні Сили"))
        }

    @Test
    fun `RemoveTelegramChannel sets pendingRemoval without removing`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(Action.RemoveTelegramChannel("Повітряні Сили"))
            advanceUntilIdle()

            coVerify(exactly = 0) { removeTelegramChannelUseCase(any()) }
            assertThat(viewModel.state.value.pendingRemoval)
                .isEqualTo(PendingRemoval.TelegramChannel("Повітряні Сили"))
        }

    @Test
    fun `ConfirmPendingRemoval removes channel and emits TelegramChannelRemoved`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(Action.RemoveTelegramChannel("Повітряні Сили"))
            viewModel.onAction(Action.ConfirmPendingRemoval)
            advanceUntilIdle()

            coVerify(exactly = 1) { removeTelegramChannelUseCase("Повітряні Сили") }
            assertThat(events).contains(Event.TelegramChannelRemoved)
            assertThat(viewModel.state.value.pendingRemoval).isNull()
        }

    @Test
    fun `SelectSuggestedTelegramChannel adds the channel and clears input`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(
                Action.SelectSuggestedTelegramChannel("Повітряні Сили ЗС України")
            )
            advanceUntilIdle()

            coVerify(exactly = 1) { addTelegramChannelUseCase("Повітряні Сили ЗС України") }
            assertThat(viewModel.state.value.inputTelegramChannel).isEqualTo("")
            assertThat(events).contains(Event.TelegramChannelAdded)
        }

    @Test
    fun `SelectSuggestedTelegramChannel ignores an already added channel silently`() =
        runTest(testDispatcher) {
            every {
                observeTelegramChannelsUseCase()
            } returns flowOf(listOf("повітряні сили зс україни"))

            val viewModel = createViewModel()
            val events = collectEvents(viewModel)
            advanceUntilIdle()

            viewModel.onAction(
                Action.SelectSuggestedTelegramChannel("Повітряні Сили ЗС України")
            )
            advanceUntilIdle()

            coVerify(exactly = 0) { addTelegramChannelUseCase(any()) }
            assertThat(events).isEmpty()
            assertThat(viewModel.state.value.duplicateWord).isNull()
        }

    @Test
    fun `all popular channels are suggested when none is added`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.suggestedTelegramChannels)
            .isEqualTo(POPULAR_TELEGRAM_CHANNELS)
    }

    @Test
    fun `suggested channels hide already added ones ignoring case`() =
        runTest(testDispatcher) {
            every { observeTelegramChannelsUseCase() } returns flowOf(listOf("tlk news"))

            val viewModel = createViewModel()
            advanceUntilIdle()

            val suggestedNames = viewModel.state.value.suggestedTelegramChannels
                .map { suggestion -> suggestion.displayName }

            assertThat(suggestedNames).containsNone("TLK News")
            assertThat(suggestedNames.size).isEqualTo(POPULAR_TELEGRAM_CHANNELS.size - 1)
        }

    @Test
    fun `suggestions are filtered by typed channel name`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("полтава"))

        val suggestedNames = viewModel.state.value.suggestedTelegramChannels
            .map { suggestion -> suggestion.displayName }

        assertThat(suggestedNames).isEqualTo(
            listOf("Полтава радар | Radar Poltava", "ПОЛТАВА НОВИНИ | СИРЕНА")
        )
    }

    @Test
    fun `suggestion query is trimmed before filtering`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("  полтава  "))

        val suggestedNames = viewModel.state.value.suggestedTelegramChannels
            .map { suggestion -> suggestion.displayName }

        assertThat(suggestedNames).isEqualTo(
            listOf("Полтава радар | Radar Poltava", "ПОЛТАВА НОВИНИ | СИРЕНА")
        )
    }

    @Test
    fun `suggestions are filtered by typed handle`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.UpdateTelegramChannelInput("@tlk"))

        val suggestedNames = viewModel.state.value.suggestedTelegramChannels
            .map { suggestion -> suggestion.displayName }

        assertThat(suggestedNames).isEqualTo(listOf("TLK News"))
    }

    @Test
    fun `ToggleTelegramChannelFilter invokes the use case`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.ToggleTelegramChannelFilter)
        advanceUntilIdle()

        coVerify(exactly = 1) { toggleTelegramChannelFilterUseCase() }
    }

    @Test
    fun `telegram channel filter flag is propagated into state`() = runTest(testDispatcher) {
        every { observeTelegramChannelFilterEnabledUseCase() } returns flowOf(true)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isTelegramChannelFilterEnabled).isTrue()
    }

    @Test
    fun `ConfirmRestoreDefaults does not touch telegram channels`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(Action.ConfirmRestoreDefaults)
        advanceUntilIdle()

        coVerify(exactly = 0) { addTelegramChannelUseCase(any()) }
        coVerify(exactly = 0) { removeTelegramChannelUseCase(any()) }
    }
}
