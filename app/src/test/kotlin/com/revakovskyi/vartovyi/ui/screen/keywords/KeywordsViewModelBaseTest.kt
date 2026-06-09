package com.revakovskyi.vartovyi.ui.screen.keywords

import android.util.Log
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.usecase.keywords.AddKeywordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddStopWordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddTelegramChannelUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ClearKeywordsScreenDataUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ExportKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ImportKeywordsUseCase
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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
abstract class KeywordsViewModelBaseTest {

    protected val observeKeywordsUseCase = mockk<ObserveKeywordsUseCase>()
    protected val observeStopWordsUseCase = mockk<ObserveStopWordsUseCase>()
    protected val observeTelegramChannelsUseCase = mockk<ObserveTelegramChannelsUseCase>()
    protected val observeTelegramChannelFilterEnabledUseCase =
        mockk<ObserveTelegramChannelFilterEnabledUseCase>()
    protected val addKeywordUseCase = mockk<AddKeywordUseCase>(relaxed = true)
    protected val removeKeywordUseCase = mockk<RemoveKeywordUseCase>(relaxed = true)
    protected val addStopWordUseCase = mockk<AddStopWordUseCase>(relaxed = true)
    protected val removeStopWordUseCase = mockk<RemoveStopWordUseCase>(relaxed = true)
    protected val addTelegramChannelUseCase = mockk<AddTelegramChannelUseCase>(relaxed = true)
    protected val removeTelegramChannelUseCase = mockk<RemoveTelegramChannelUseCase>(relaxed = true)
    protected val toggleTelegramChannelFilterUseCase =
        mockk<ToggleTelegramChannelFilterUseCase>(relaxed = true)
    protected val clearKeywordsScreenDataUseCase =
        mockk<ClearKeywordsScreenDataUseCase>(relaxed = true)
    protected val restoreDefaultKeywordsUseCase =
        mockk<RestoreDefaultKeywordsUseCase>(relaxed = true)
    protected val restoreDefaultStopWordsUseCase =
        mockk<RestoreDefaultStopWordsUseCase>(relaxed = true)
    protected val exportKeywordsUseCase = mockk<ExportKeywordsUseCase>(relaxed = true)
    protected val importKeywordsUseCase = mockk<ImportKeywordsUseCase>(relaxed = true)
    protected val sanitizeWordInputUseCase: SanitizeWordInputUseCase =
        SanitizeWordInputUseCaseImpl()

    protected val testDispatcher = UnconfinedTestDispatcher()

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

    protected fun createViewModel(): KeywordsViewModel = KeywordsViewModel(
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

    protected fun TestScope.collectEvents(viewModel: KeywordsViewModel): List<Event> {
        val collectedEvents = mutableListOf<Event>()

        backgroundScope.launch(testDispatcher) {
            viewModel.events.toList(collectedEvents)
        }

        return collectedEvents
    }
}
