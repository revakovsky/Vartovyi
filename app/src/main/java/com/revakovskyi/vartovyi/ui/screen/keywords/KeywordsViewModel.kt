package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.State
import com.revakovskyi.vartovyi.usecase.keywords.AddKeywordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddStopWordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.AddTelegramChannelUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveStopWordsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelFilterEnabledUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveTelegramChannelsUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveKeywordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveStopWordUseCase
import com.revakovskyi.vartovyi.usecase.keywords.RemoveTelegramChannelUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ToggleTelegramChannelFilterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KeywordsViewModel(
    private val observeKeywordsUseCase: ObserveKeywordsUseCase,
    private val observeStopWordsUseCase: ObserveStopWordsUseCase,
    private val observeTelegramChannelsUseCase: ObserveTelegramChannelsUseCase,
    private val observeTelegramChannelFilterEnabledUseCase: ObserveTelegramChannelFilterEnabledUseCase,
    private val addKeywordUseCase: AddKeywordUseCase,
    private val removeKeywordUseCase: RemoveKeywordUseCase,
    private val addStopWordUseCase: AddStopWordUseCase,
    private val removeStopWordUseCase: RemoveStopWordUseCase,
    private val addTelegramChannelUseCase: AddTelegramChannelUseCase,
    private val removeTelegramChannelUseCase: RemoveTelegramChannelUseCase,
    private val toggleTelegramChannelFilterUseCase: ToggleTelegramChannelFilterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    init {
        observeKeywords()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SelectTriggerKeywordRuleType -> selectTriggerKeywordRuleType(action.type)
            is Action.UpdateKeywordInput -> updateKeywordInput(action.value)
            is Action.UpdateStopWordInput -> updateStopWordInput(action.value)
            is Action.AddKeyword -> addKeyword()
            is Action.RemoveKeyword -> removeKeyword(action.keyword)
            is Action.AddStopWord -> addStopWord()
            is Action.RemoveStopWord -> removeStopWord(action.stopWord)
            is Action.ToggleTelegramChannelFilter -> toggleTelegramChannelFilter()
            is Action.UpdateTelegramChannelInput -> updateTelegramChannelInput(action.value)
            is Action.AddTelegramChannel -> addTelegramChannel()
            is Action.RemoveTelegramChannel -> removeTelegramChannel(action.channel)
            is Action.DismissDuplicateWordDialog -> dismissDuplicateWordDialog()
            is Action.ConfirmPendingRemoval -> confirmPendingRemoval()
            is Action.DismissPendingRemovalDialog -> dismissPendingRemovalDialog()
        }
    }

    private fun observeKeywords() {
        var isFirstEmission = true
        combine(
            observeKeywordsUseCase(),
            observeStopWordsUseCase(),
            observeTelegramChannelsUseCase(),
            observeTelegramChannelFilterEnabledUseCase(),
        ) { keywords, stopWords, telegramChannels, isTelegramChannelFilterEnabled ->
            val parsedKeywords = keywords.map { keyword ->
                TriggerKeywordRule.fromStorageValue(keyword)
            }
            val sortedKeywords = parsedKeywords.sortedWith(
                compareBy<TriggerKeywordRule>(
                    { keywordRule -> triggerRuleTypeOrder(keywordRule.type) },
                    { keywordRule -> keywordRule.displayValue.lowercase() },
                )
            )

            _state.update {
                it.copy(
                    keywords = sortedKeywords,
                    stopWords = stopWords,
                    telegramChannels = telegramChannels,
                    isTelegramChannelFilterEnabled = isTelegramChannelFilterEnabled,
                )
            }

            if (isFirstEmission) {
                isFirstEmission = false
                _state.update { it.copy(isLoading = false) }
            }
        }.launchIn(viewModelScope)
    }

    private fun updateKeywordInput(value: String) {
        _state.update { it.copy(inputKeyword = value) }
    }

    private fun selectTriggerKeywordRuleType(type: TriggerKeywordRuleType) {
        _state.update { it.copy(selectedTriggerKeywordRuleType = type) }
    }

    private fun updateStopWordInput(value: String) {
        _state.update { it.copy(inputStopWord = value) }
    }

    private fun addKeyword() {
        val newKeywordRule = TriggerKeywordRule.create(
            type = _state.value.selectedTriggerKeywordRuleType,
            input = _state.value.inputKeyword,
        ) ?: return

        if (
            _state.value.keywords.any { keywordRule ->
                keywordRule.normalizedSignature() == newKeywordRule.normalizedSignature()
            }
        ) {
            _state.update {
                it.copy(
                    inputKeyword = "",
                    duplicateWord = newKeywordRule.displayValue
                )
            }
            return
        }

        viewModelScope.launch {
            addKeywordUseCase(newKeywordRule.storageValue)
            _state.update { it.copy(inputKeyword = "") }
            _events.emit(Event.KeywordAdded)
        }
    }

    private fun removeKeyword(keyword: TriggerKeywordRule) {
        _state.update {
            it.copy(
                pendingRemoval = KeywordsUiContract.PendingRemoval.Keyword(keyword)
            )
        }
    }

    private fun addStopWord() {
        val stopWord = _state.value.inputStopWord.trim()

        if (stopWord.isBlank() || stopWord.length < 2) return

        if (_state.value.stopWords.any { it.equals(stopWord, ignoreCase = true) }) {
            _state.update { it.copy(inputStopWord = "", duplicateWord = stopWord) }
            return
        }

        viewModelScope.launch {
            addStopWordUseCase(stopWord)
            _state.update { it.copy(inputStopWord = "") }
            _events.emit(Event.StopWordAdded)
        }
    }

    private fun removeStopWord(stopWord: String) {
        _state.update {
            it.copy(
                pendingRemoval = KeywordsUiContract.PendingRemoval.StopWord(stopWord)
            )
        }
    }

    private fun toggleTelegramChannelFilter() {
        viewModelScope.launch { toggleTelegramChannelFilterUseCase() }
    }

    private fun updateTelegramChannelInput(value: String) {
        _state.update { it.copy(inputTelegramChannel = value) }
    }

    private fun addTelegramChannel() {
        val channel = _state.value.inputTelegramChannel.trim()

        if (channel.isBlank() || channel.length < 2) return

        if (_state.value.telegramChannels.any { it.equals(channel, ignoreCase = true) }) {
            _state.update { it.copy(inputTelegramChannel = "", duplicateWord = channel) }
            return
        }

        viewModelScope.launch {
            addTelegramChannelUseCase(channel)
            _state.update { it.copy(inputTelegramChannel = "") }
            _events.emit(Event.TelegramChannelAdded)
        }
    }

    private fun removeTelegramChannel(channel: String) {
        _state.update {
            it.copy(
                pendingRemoval = KeywordsUiContract.PendingRemoval.TelegramChannel(channel)
            )
        }
    }

    private fun dismissDuplicateWordDialog() {
        _state.update { it.copy(duplicateWord = null) }
    }

    private fun confirmPendingRemoval() {
        val pendingRemoval = _state.value.pendingRemoval ?: return

        viewModelScope.launch {
            when (pendingRemoval) {
                is KeywordsUiContract.PendingRemoval.Keyword -> {
                    removeKeywordUseCase(pendingRemoval.keywordRule.storageValue)
                    _events.emit(Event.KeywordRemoved)
                }

                is KeywordsUiContract.PendingRemoval.StopWord -> {
                    removeStopWordUseCase(pendingRemoval.stopWord)
                    _events.emit(Event.StopWordRemoved)
                }

                is KeywordsUiContract.PendingRemoval.TelegramChannel -> {
                    removeTelegramChannelUseCase(pendingRemoval.channel)
                    _events.emit(Event.TelegramChannelRemoved)
                }
            }

            _state.update { it.copy(pendingRemoval = null) }
        }
    }

    private fun dismissPendingRemovalDialog() {
        _state.update { it.copy(pendingRemoval = null) }
    }

    private fun triggerRuleTypeOrder(type: TriggerKeywordRuleType): Int {
        return when (type) {
            TriggerKeywordRuleType.WORD -> 0
            TriggerKeywordRuleType.ALL_WORDS -> 1
            TriggerKeywordRuleType.PHRASE -> 2
        }
    }

}
