package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddKeywordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddStopWordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddTelegramChannelUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveStopWordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveTelegramChannelFilterEnabledUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveTelegramChannelsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveKeywordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveStopWordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveTelegramChannelUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ToggleTelegramChannelFilterUseCase
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

    private val _state = MutableStateFlow(KeywordsUiContract.State())
    val state: StateFlow<KeywordsUiContract.State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<KeywordsUiContract.Event>()
    val events: SharedFlow<KeywordsUiContract.Event> = _events.asSharedFlow()

    init {
        observeKeywords()
    }

    fun onAction(action: KeywordsUiContract.Action) {
        when (action) {
            is KeywordsUiContract.Action.UpdateKeywordInput -> updateKeywordInput(action.value)
            is KeywordsUiContract.Action.UpdateStopWordInput -> updateStopWordInput(action.value)
            is KeywordsUiContract.Action.AddKeyword -> addKeyword()
            is KeywordsUiContract.Action.RemoveKeyword -> removeKeyword(action.keyword)
            is KeywordsUiContract.Action.AddStopWord -> addStopWord()
            is KeywordsUiContract.Action.RemoveStopWord -> removeStopWord(action.stopWord)
            is KeywordsUiContract.Action.ToggleTelegramChannelFilter -> toggleTelegramChannelFilter()
            is KeywordsUiContract.Action.UpdateTelegramChannelInput -> updateTelegramChannelInput(action.value)
            is KeywordsUiContract.Action.AddTelegramChannel -> addTelegramChannel()
            is KeywordsUiContract.Action.RemoveTelegramChannel -> removeTelegramChannel(action.channel)
            is KeywordsUiContract.Action.DismissDuplicateWordDialog -> dismissDuplicateWordDialog()
            is KeywordsUiContract.Action.NavigateBack -> navigateBack()
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
            _state.update {
                it.copy(
                    keywords = keywords,
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

    private fun updateStopWordInput(value: String) {
        _state.update { it.copy(inputStopWord = value) }
    }

    private fun addKeyword() {
        val keyword = _state.value.inputKeyword.trim()

        if (keyword.isBlank() || keyword.length < 2) return

        if (_state.value.keywords.any { it.equals(keyword, ignoreCase = true) }) {
            _state.update { it.copy(inputKeyword = "", duplicateWord = keyword) }
            return
        }

        viewModelScope.launch {
            addKeywordUseCase(keyword)
            _state.update { it.copy(inputKeyword = "") }
            _events.emit(KeywordsUiContract.Event.KeywordAdded)
        }
    }

    private fun removeKeyword(keyword: String) {
        viewModelScope.launch { removeKeywordUseCase(keyword) }
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
            _events.emit(KeywordsUiContract.Event.StopWordAdded)
        }
    }

    private fun removeStopWord(stopWord: String) {
        viewModelScope.launch { removeStopWordUseCase(stopWord) }
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
            _events.emit(KeywordsUiContract.Event.TelegramChannelAdded)
        }
    }

    private fun removeTelegramChannel(channel: String) {
        viewModelScope.launch { removeTelegramChannelUseCase(channel) }
    }

    private fun dismissDuplicateWordDialog() {
        _state.update { it.copy(duplicateWord = null) }
    }

    private fun navigateBack() {
        viewModelScope.launch { _events.emit(KeywordsUiContract.Event.NavigateBack) }
    }

}
