package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddKeywordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.AddStopWordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveStopWordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveKeywordUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.RemoveStopWordUseCase
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
    private val addKeywordUseCase: AddKeywordUseCase,
    private val removeKeywordUseCase: RemoveKeywordUseCase,
    private val addStopWordUseCase: AddStopWordUseCase,
    private val removeStopWordUseCase: RemoveStopWordUseCase,
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
            is KeywordsUiContract.Action.NavigateBack -> navigateBack()
        }
    }

    private fun observeKeywords() {
        combine(
            observeKeywordsUseCase(),
            observeStopWordsUseCase(),
        ) { keywords, stopWords ->
            _state.update { it.copy(keywords = keywords, stopWords = stopWords) }
        }.launchIn(viewModelScope)
    }

    private fun updateKeywordInput(value: String) {
        _state.update { it.copy(inputKeyword = value) }
    }

    private fun updateStopWordInput(value: String) {
        _state.update { it.copy(inputStopWord = value) }
    }

    private fun addKeyword() {
        viewModelScope.launch {
            addKeywordUseCase(_state.value.inputKeyword)
            _state.update { it.copy(inputKeyword = "") }
            _events.emit(KeywordsUiContract.Event.KeywordAdded)
        }
    }

    private fun removeKeyword(keyword: String) {
        viewModelScope.launch { removeKeywordUseCase(keyword) }
    }

    private fun addStopWord() {
        viewModelScope.launch {
            addStopWordUseCase(_state.value.inputStopWord)
            _state.update { it.copy(inputStopWord = "") }
            _events.emit(KeywordsUiContract.Event.StopWordAdded)
        }
    }

    private fun removeStopWord(stopWord: String) {
        viewModelScope.launch { removeStopWordUseCase(stopWord) }
    }

    private fun navigateBack() {
        viewModelScope.launch { _events.emit(KeywordsUiContract.Event.NavigateBack) }
    }

}
