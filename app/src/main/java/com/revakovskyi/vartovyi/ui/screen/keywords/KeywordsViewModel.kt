package com.revakovskyi.vartovyi.ui.screen.keywords

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.model.WordInputTarget
import com.revakovskyi.vartovyi.result.KeywordSanitizationResult
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event.KeywordMultiLineNotAllowed
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event.KeywordStartsWithNonAlphanumeric
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.Event.KeywordTermTooShort
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract.State
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
import com.revakovskyi.vartovyi.usecase.keywords.ToggleTelegramChannelFilterUseCase
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "KeywordsViewModel"

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
    private val clearKeywordsScreenDataUseCase: ClearKeywordsScreenDataUseCase,
    private val restoreDefaultKeywordsUseCase: RestoreDefaultKeywordsUseCase,
    private val restoreDefaultStopWordsUseCase: RestoreDefaultStopWordsUseCase,
    private val sanitizeWordInputUseCase: SanitizeWordInputUseCase,
    private val exportKeywordsUseCase: ExportKeywordsUseCase,
    private val importKeywordsUseCase: ImportKeywordsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = _events.receiveAsFlow()

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
            is Action.SelectSuggestedTelegramChannel -> {
                selectSuggestedTelegramChannel(action.channel)
            }
            is Action.RemoveTelegramChannel -> removeTelegramChannel(action.channel)
            is Action.DismissDuplicateWordDialog -> dismissDuplicateWordDialog()
            is Action.ConfirmPendingRemoval -> confirmPendingRemoval()
            is Action.DismissPendingRemovalDialog -> dismissPendingRemovalDialog()
            is Action.OpenClearKeywordsDialog -> openClearKeywordsDialog()
            is Action.DismissClearKeywordsDialog -> dismissClearKeywordsDialog()
            is Action.ConfirmClearKeywords -> confirmClearKeywords()
            is Action.OpenRestoreDefaultsDialog -> openRestoreDefaultsDialog()
            is Action.DismissRestoreDefaultsDialog -> dismissRestoreDefaultsDialog()
            is Action.ConfirmRestoreDefaults -> confirmRestoreDefaults()
            is Action.CopyChip -> copyChip(action.text)
            is Action.ExportKeywords -> exportKeywords()
            is Action.NotifyExportSuccess -> notifyExportSuccess()
            is Action.NotifyExportError -> notifyExportError()
            is Action.RequestImport -> requestImport()
            is Action.DismissImportConfirmationDialog -> dismissImportConfirmationDialog()
            is Action.ConfirmImport -> confirmImport()
            is Action.ImportKeywords -> importKeywords(action.jsonContent)
            is Action.NotifyImportReadError -> notifyImportReadError()
            is Action.NotifyImportFileTooLarge -> notifyImportFileTooLarge()
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
                parseTriggerKeywordRuleFromStorage(keyword)
            }
            val sortedKeywords = parsedKeywords.sortedWith(
                compareBy(
                    { keywordRule -> triggerRuleTypeOrder(keywordRule.type) },
                    { keywordRule -> keywordRule.displayValue.lowercase() },
                )
            )

            _state.update { currentState ->
                currentState.copy(
                    keywords = sortedKeywords,
                    stopWords = stopWords,
                    telegramChannels = telegramChannels,
                    isTelegramChannelFilterEnabled = isTelegramChannelFilterEnabled,
                    isLoading = if (isFirstEmission) false else currentState.isLoading,
                )
            }

            if (isFirstEmission) {
                isFirstEmission = false
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
        val selectedType = _state.value.selectedTriggerKeywordRuleType
        val rawInput = _state.value.inputKeyword
        val target = WordInputTarget.TriggerKeyword(selectedType = selectedType)

        viewModelScope.launch {
            when (val outcome = sanitizeWordInputUseCase(rawInput, target)) {
                is KeywordSanitizationResult.Empty -> return@launch

                is KeywordSanitizationResult.MultiLineDetected -> {
                    _events.send(KeywordMultiLineNotAllowed)
                }

                is KeywordSanitizationResult.TermTooShort -> {
                    _events.send(
                        KeywordTermTooShort(minLength = KeywordsLimits.MIN_TERM_LENGTH)
                    )
                }

                is KeywordSanitizationResult.Sanitized -> {
                    addSanitizedKeyword(sanitized = outcome, selectedType = selectedType)
                }

                KeywordSanitizationResult.StartsWithNonAlphanumeric -> {
                    _events.send(KeywordStartsWithNonAlphanumeric)
                }
            }
        }
    }

    private suspend fun addSanitizedKeyword(
        sanitized: KeywordSanitizationResult.Sanitized,
        selectedType: TriggerKeywordRuleType,
    ) {
        val newKeywordRule = parseTriggerKeywordRuleFromStorage(sanitized.storageValue)

        val isDuplicate = _state.value.keywords.any { keywordRule ->
            keywordRule.normalizedSignature() == newKeywordRule.normalizedSignature()
        }
        if (isDuplicate) {
            _state.update {
                it.copy(
                    inputKeyword = "",
                    duplicateWord = newKeywordRule.displayValue,
                )
            }
            return
        }

        if (_state.value.keywords.size >= KeywordsLimits.MAX_TOTAL_KEYWORDS) {
            _events.send(Event.KeywordsMaxReached(max = KeywordsLimits.MAX_TOTAL_KEYWORDS))
            return
        }

        addKeywordUseCase(newKeywordRule.storageValue)
        _state.update { it.copy(inputKeyword = "") }
        _events.send(Event.KeywordAdded)

        val wasModified = wasContentModified(
            sanitized = sanitized,
            newKeywordRule = newKeywordRule,
            selectedType = selectedType,
        )
        if (wasModified) {
            _events.send(Event.KeywordNormalized(displayValue = newKeywordRule.displayValue))
        }
    }

    private fun wasContentModified(
        sanitized: KeywordSanitizationResult.Sanitized,
        newKeywordRule: TriggerKeywordRule,
        selectedType: TriggerKeywordRuleType,
    ): Boolean {
        if (sanitized.effectiveType != selectedType) return true

        val resultContent = newKeywordRule.terms.joinToString(
            separator = KeywordRuleFormat.PHRASE_TERM_SEPARATOR,
        )
        val rawContent = stripBalancedOuterQuotes(sanitized.normalizedRawInput)

        return resultContent != rawContent
    }

    private fun stripBalancedOuterQuotes(value: String): String {
        val hasBalancedOuterQuotes = value.length >= 2 &&
                value.startsWith(KeywordRuleFormat.QUOTE) &&
                value.endsWith(KeywordRuleFormat.QUOTE)

        return if (hasBalancedOuterQuotes) {
            value.substring(1, value.length - 1)
        } else {
            value
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
        val rawInput = _state.value.inputStopWord

        viewModelScope.launch {
            when (val outcome = sanitizeWordInputUseCase(rawInput, WordInputTarget.StopWord)) {
                is KeywordSanitizationResult.Empty -> return@launch

                is KeywordSanitizationResult.MultiLineDetected -> {
                    _events.send(KeywordMultiLineNotAllowed)
                }

                is KeywordSanitizationResult.TermTooShort -> {
                    _events.send(
                        KeywordTermTooShort(minLength = KeywordsLimits.MIN_TERM_LENGTH)
                    )
                }

                is KeywordSanitizationResult.Sanitized -> {
                    addSanitizedStopWord(sanitized = outcome, rawInput = rawInput)
                }

                KeywordSanitizationResult.StartsWithNonAlphanumeric -> return@launch
            }
        }
    }

    private suspend fun addSanitizedStopWord(
        sanitized: KeywordSanitizationResult.Sanitized,
        rawInput: String,
    ) {
        val stopWord = sanitized.storageValue

        if (_state.value.stopWords.any { it.equals(stopWord, ignoreCase = true) }) {
            _state.update { it.copy(inputStopWord = "", duplicateWord = stopWord) }
            return
        }

        addStopWordUseCase(stopWord)
        _state.update { it.copy(inputStopWord = "") }
        _events.send(Event.StopWordAdded)

        if (stopWord != rawInput.trim()) {
            _events.send(Event.KeywordNormalized(displayValue = stopWord))
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
        val rawInput = _state.value.inputTelegramChannel

        viewModelScope.launch {
            when (val outcome = sanitizeWordInputUseCase(rawInput, WordInputTarget.TelegramChannel)) {
                is KeywordSanitizationResult.Empty -> return@launch

                is KeywordSanitizationResult.MultiLineDetected -> {
                    _events.send(KeywordMultiLineNotAllowed)
                }

                is KeywordSanitizationResult.TermTooShort -> {
                    _events.send(
                        KeywordTermTooShort(minLength = KeywordsLimits.MIN_TERM_LENGTH)
                    )
                }

                is KeywordSanitizationResult.Sanitized -> {
                    addSanitizedTelegramChannel(sanitized = outcome, rawInput = rawInput)
                }

                KeywordSanitizationResult.StartsWithNonAlphanumeric -> return@launch
            }
        }
    }

    private suspend fun addSanitizedTelegramChannel(
        sanitized: KeywordSanitizationResult.Sanitized,
        rawInput: String,
    ) {
        val channel = sanitized.storageValue

        if (_state.value.telegramChannels.any { it.equals(channel, ignoreCase = true) }) {
            _state.update { it.copy(inputTelegramChannel = "", duplicateWord = channel) }
            return
        }

        addTelegramChannelUseCase(channel)
        _state.update { it.copy(inputTelegramChannel = "") }
        _events.send(Event.TelegramChannelAdded)

        if (channel != rawInput.trim()) {
            _events.send(Event.KeywordNormalized(displayValue = channel))
        }
    }

    private fun selectSuggestedTelegramChannel(channel: String) {
        viewModelScope.launch {
            val isAlreadyAdded = _state.value.telegramChannels.any {
                it.equals(channel, ignoreCase = true)
            }
            if (isAlreadyAdded) return@launch

            val outcome = sanitizeWordInputUseCase(channel, WordInputTarget.TelegramChannel)
            if (outcome is KeywordSanitizationResult.Sanitized) {
                addSanitizedTelegramChannel(sanitized = outcome, rawInput = channel)
            }
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
                    _events.send(Event.KeywordRemoved)
                }

                is KeywordsUiContract.PendingRemoval.StopWord -> {
                    removeStopWordUseCase(pendingRemoval.stopWord)
                    _events.send(Event.StopWordRemoved)
                }

                is KeywordsUiContract.PendingRemoval.TelegramChannel -> {
                    removeTelegramChannelUseCase(pendingRemoval.channel)
                    _events.send(Event.TelegramChannelRemoved)
                }
            }

            _state.update { it.copy(pendingRemoval = null) }
        }
    }

    private fun dismissPendingRemovalDialog() {
        _state.update { it.copy(pendingRemoval = null) }
    }

    private fun openClearKeywordsDialog() {
        _state.update { currentState ->
            currentState.copy(isClearKeywordsDialogVisible = true)
        }
    }

    private fun dismissClearKeywordsDialog() {
        _state.update { currentState ->
            currentState.copy(isClearKeywordsDialogVisible = false)
        }
    }

    private fun confirmClearKeywords() {
        viewModelScope.launch {
            clearKeywordsScreenDataUseCase()
            _state.update { currentState ->
                currentState.copy(
                    isClearKeywordsDialogVisible = false,
                    inputKeyword = "",
                    inputStopWord = "",
                    inputTelegramChannel = "",
                    duplicateWord = null,
                    pendingRemoval = null,
                    selectedTriggerKeywordRuleType = TriggerKeywordRuleType.WORD,
                )
            }
            _events.send(Event.KeywordsScreenDataCleared)
        }
    }

    private fun openRestoreDefaultsDialog() {
        _state.update { currentState ->
            currentState.copy(isRestoreDefaultsDialogVisible = true)
        }
    }

    private fun dismissRestoreDefaultsDialog() {
        _state.update { currentState ->
            currentState.copy(isRestoreDefaultsDialogVisible = false)
        }
    }

    private fun confirmRestoreDefaults() {
        viewModelScope.launch {
            val addedKeywordsCount = restoreDefaultKeywordsUseCase()
            val addedStopWordsCount = restoreDefaultStopWordsUseCase()

            _state.update { currentState ->
                currentState.copy(isRestoreDefaultsDialogVisible = false)
            }
            _events.send(
                Event.DefaultKeywordsRestored(
                    addedCount = addedKeywordsCount + addedStopWordsCount
                )
            )
        }
    }

    private fun copyChip(text: String) {
        viewModelScope.launch { _events.send(Event.ChipCopied(text)) }
    }

    private fun exportKeywords() {
        viewModelScope.launch {
            when (val result = exportKeywordsUseCase()) {
                is ExportResult.Success -> _events.send(Event.LaunchExportFilePicker(result.jsonContent))
                is ExportResult.Error -> {
                    Log.e(TAG, "exportKeywords: failed to build backup", result.exception)
                    _events.send(Event.KeywordsExportError)
                }
            }
        }
    }

    private fun notifyExportSuccess() {
        viewModelScope.launch { _events.send(Event.KeywordsExportSuccess) }
    }

    private fun notifyExportError() {
        viewModelScope.launch { _events.send(Event.KeywordsExportError) }
    }

    private fun notifyImportReadError() {
        viewModelScope.launch { _events.send(Event.KeywordsImportInvalidFormat) }
    }

    private fun notifyImportFileTooLarge() {
        viewModelScope.launch { _events.send(Event.KeywordsImportFileTooLarge) }
    }

    private fun requestImport() {
        if (_state.value.hasKeywordDataToClear) {
            _state.update { it.copy(isImportConfirmationDialogVisible = true) }
        } else {
            viewModelScope.launch { _events.send(Event.LaunchImportFilePicker) }
        }
    }

    private fun dismissImportConfirmationDialog() {
        _state.update { it.copy(isImportConfirmationDialogVisible = false) }
    }

    private fun confirmImport() {
        _state.update { it.copy(isImportConfirmationDialogVisible = false) }
        viewModelScope.launch { _events.send(Event.LaunchImportFilePicker) }
    }

    private fun importKeywords(jsonContent: String) {
        viewModelScope.launch {
            when (val result = importKeywordsUseCase(jsonContent)) {
                is ImportResult.Success -> _events.send(Event.KeywordsImportSuccess)

                is ImportResult.InvalidFormat -> {
                    Log.e(TAG, "importKeywords: exception", result.exception)
                    _events.send(Event.KeywordsImportInvalidFormat)
                }

                is ImportResult.WriteError -> {
                    Log.e(TAG, "importKeywords: write error")
                    _events.send(Event.KeywordsImportWriteError)
                }

                is ImportResult.UnsupportedVersion -> {
                    _events.send(Event.KeywordsImportUnsupportedVersion(result.fileVersion))
                }
            }
        }
    }

    private fun triggerRuleTypeOrder(type: TriggerKeywordRuleType): Int {
        return when (type) {
            TriggerKeywordRuleType.WORD -> 0
            TriggerKeywordRuleType.ALL_WORDS -> 1
            TriggerKeywordRuleType.PHRASE -> 2
        }
    }

}
