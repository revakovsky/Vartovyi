package com.revakovskyi.vartovyi.ui.screen.keywords

import android.util.Log
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsNone
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.constants.POPULAR_TELEGRAM_CHANNELS
import com.revakovskyi.vartovyi.model.ImportStrategy
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.PendingRemoval
import com.revakovskyi.vartovyi.ui.screen.keywords.model.ExportDestination
import com.revakovskyi.vartovyi.usecase.keywords.AddKeywordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddStopWordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddTelegramChannelUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ClearKeywordsScreenDataUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ExportKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ExportResult
import com.revakovskyi.vartovyi.usecase.keywords.ImportKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ImportResult
import com.revakovskyi.vartovyi.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveStopWordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelFilterEnabledUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveKeywordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveStopWordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveTelegramChannelUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RestoreDefaultKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RestoreDefaultStopWordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.SanitizeWordInputUseCase
import com.revakovskyi.vartovyi.usecase.keywords.SanitizeWordInputUseCaseImpl
import com.revakovskyi.vartovyi.usecase.keywords.ToggleTelegramChannelFilterUseCase
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordsViewModelTest {

    private val observeKeywordsUseCase = mockk<ObserveKeywordsUseCase>()
    private val observeStopWordsUseCase = mockk<ObserveStopWordsUseCase>()
    private val observeTelegramChannelsUseCase = mockk<ObserveTelegramChannelsUseCase>()
    private val observeTelegramChannelFilterEnabledUseCase = mockk<ObserveTelegramChannelFilterEnabledUseCase>()
    private val addKeywordUseCase = mockk<AddKeywordUseCase>(relaxed = true)
    private val removeKeywordUseCase = mockk<RemoveKeywordUseCase>(relaxed = true)
    private val addStopWordUseCase = mockk<AddStopWordUseCase>(relaxed = true)
    private val removeStopWordUseCase = mockk<RemoveStopWordUseCase>(relaxed = true)
    private val addTelegramChannelUseCase = mockk<AddTelegramChannelUseCase>(relaxed = true)
    private val removeTelegramChannelUseCase = mockk<RemoveTelegramChannelUseCase>(relaxed = true)
    private val toggleTelegramChannelFilterUseCase = mockk<ToggleTelegramChannelFilterUseCase>(relaxed = true)
    private val clearKeywordsScreenDataUseCase = mockk<ClearKeywordsScreenDataUseCase>(relaxed = true)
    private val restoreDefaultKeywordsUseCase = mockk<RestoreDefaultKeywordsUseCase>(relaxed = true)
    private val restoreDefaultStopWordsUseCase = mockk<RestoreDefaultStopWordsUseCase>(relaxed = true)
    private val exportKeywordsUseCase = mockk<ExportKeywordsUseCase>(relaxed = true)
    private val importKeywordsUseCase = mockk<ImportKeywordsUseCase>(relaxed = true)
    private val sanitizeWordInputUseCase: SanitizeWordInputUseCase = SanitizeWordInputUseCaseImpl()

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        every { observeKeywordsUseCase() } returns flowOf(emptyList())
        every { observeStopWordsUseCase() } returns flowOf(emptyList())
        every { observeTelegramChannelsUseCase() } returns flowOf(emptyList())
        every { observeTelegramChannelFilterEnabledUseCase() } returns flowOf(false)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()

        unmockkStatic(Log::class)
    }

    @Nested
    inner class TriggerKeywords {

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

    @Nested
    inner class StopWords {

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

    @Nested
    inner class TelegramChannels {

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

    @Nested
    inner class Dialogs {

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

    @Nested
    inner class Import {

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

    @Nested
    inner class Export {

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

    private fun createViewModel(): KeywordsViewModel = KeywordsViewModel(
        observeKeywordsUseCase = observeKeywordsUseCase,
        observeStopWordsUseCase = observeStopWordsUseCase,
        observeTelegramChannelsUseCase = observeTelegramChannelsUseCase,
        observeTelegramChannelFilterEnabledUseCase = observeTelegramChannelFilterEnabledUseCase,
        addKeywordUseCase = addKeywordUseCase,
        removeKeywordUseCase = removeKeywordUseCase,
        addStopWordUseCase = addStopWordUseCase,
        removeStopWordUseCase = removeStopWordUseCase,
        addTelegramChannelUseCase = addTelegramChannelUseCase,
        removeTelegramChannelUseCase = removeTelegramChannelUseCase,
        toggleTelegramChannelFilterUseCase = toggleTelegramChannelFilterUseCase,
        clearKeywordsScreenDataUseCase = clearKeywordsScreenDataUseCase,
        restoreDefaultKeywordsUseCase = restoreDefaultKeywordsUseCase,
        restoreDefaultStopWordsUseCase = restoreDefaultStopWordsUseCase,
        sanitizeWordInputUseCase = sanitizeWordInputUseCase,
        exportKeywordsUseCase = exportKeywordsUseCase,
        importKeywordsUseCase = importKeywordsUseCase,
    )

    private fun TestScope.collectEvents(viewModel: KeywordsViewModel): List<Event> {
        val collectedEvents = mutableListOf<Event>()

        backgroundScope.launch(testDispatcher) {
            viewModel.events.toList(collectedEvents)
        }

        return collectedEvents
    }
}
