package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.model.ImportStrategy
import com.revakovskyi.vartovyi.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.screen.keywords.model.ExportDestination

interface KeywordsUiContract {

    sealed interface PendingRemoval {
        data class Keyword(val keywordRule: TriggerKeywordRule) : PendingRemoval
        data class StopWord(val stopWord: String) : PendingRemoval
        data class TelegramChannel(val channel: String) : PendingRemoval
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val keywords: List<TriggerKeywordRule> = emptyList(),
        val selectedTriggerKeywordRuleType: TriggerKeywordRuleType = TriggerKeywordRuleType.WORD,
        val stopWords: List<String> = emptyList(),
        val inputKeyword: String = "",
        val inputStopWord: String = "",
        val isTelegramChannelFilterEnabled: Boolean = false,
        val telegramChannels: List<String> = emptyList(),
        val inputTelegramChannel: String = "",
        val duplicateWord: String? = null,
        val pendingRemoval: PendingRemoval? = null,
        val isClearKeywordsDialogVisible: Boolean = false,
        val isRestoreDefaultsDialogVisible: Boolean = false,
        val isImportStrategyDialogVisible: Boolean = false,
        val pendingImportStrategy: ImportStrategy? = null,
        val isExportDestinationDialogVisible: Boolean = false,
    ) {
        val hasKeywordDataToClear: Boolean
            get() = keywords.isNotEmpty() ||
                    stopWords.isNotEmpty() ||
                    telegramChannels.isNotEmpty() ||
                    isTelegramChannelFilterEnabled

        val canExport: Boolean
            get() = keywords.isNotEmpty() || stopWords.isNotEmpty() || telegramChannels.isNotEmpty()
    }

    sealed interface Action {
        data class SelectTriggerKeywordRuleType(val type: TriggerKeywordRuleType) : Action
        data class UpdateKeywordInput(val value: String) : Action
        data class UpdateStopWordInput(val value: String) : Action
        data object AddKeyword : Action
        data class RemoveKeyword(val keyword: TriggerKeywordRule) : Action
        data object AddStopWord : Action
        data class RemoveStopWord(val stopWord: String) : Action
        data object ToggleTelegramChannelFilter : Action
        data class UpdateTelegramChannelInput(val value: String) : Action
        data object AddTelegramChannel : Action
        data class RemoveTelegramChannel(val channel: String) : Action
        data object DismissDuplicateWordDialog : Action
        data object ConfirmPendingRemoval : Action
        data object DismissPendingRemovalDialog : Action
        data object OpenClearKeywordsDialog : Action
        data object DismissClearKeywordsDialog : Action
        data object ConfirmClearKeywords : Action
        data object OpenRestoreDefaultsDialog : Action
        data object DismissRestoreDefaultsDialog : Action
        data object ConfirmRestoreDefaults : Action
        data class CopyChip(val text: String) : Action
        data object RequestExport : Action
        data object DismissExportDestinationDialog : Action
        data class SelectExportDestination(val destination: ExportDestination) : Action
        data object NotifyExportSuccess : Action
        data object NotifyExportError : Action
        data object RequestImport : Action
        data object DismissImportStrategyDialog : Action
        data class SelectImportStrategy(val strategy: ImportStrategy) : Action
        data class ImportKeywords(val jsonContent: String) : Action
        data object NotifyImportReadError : Action
        data object NotifyImportFileTooLarge : Action
    }

    sealed interface Event {
        data object KeywordAdded : Event
        data class KeywordNormalized(val displayValue: String) : Event
        data object KeywordMultiLineNotAllowed : Event
        data class KeywordTermTooShort(val minLength: Int) : Event
        data object KeywordStartsWithNonAlphanumeric : Event
        data class KeywordsMaxReached(val max: Int) : Event
        data object KeywordRemoved : Event
        data object StopWordAdded : Event
        data object StopWordRemoved : Event
        data object TelegramChannelAdded : Event
        data object TelegramChannelRemoved : Event
        data object KeywordsScreenDataCleared : Event
        data class DefaultKeywordsRestored(val addedCount: Int) : Event
        data class ChipCopied(val text: String) : Event
        data class LaunchExportFilePicker(val jsonContent: String) : Event
        data class LaunchExportShareSheet(val jsonContent: String) : Event
        data object KeywordsExportSuccess : Event
        data object KeywordsExportError : Event
        data object LaunchImportFilePicker : Event
        data class KeywordsImportSuccess(
            val strategy: ImportStrategy,
            val addedCount: Int,
            val skippedCount: Int,
        ) : Event
        data object KeywordsImportInvalidFormat : Event
        data class KeywordsImportUnsupportedVersion(val fileVersion: Int) : Event
        data object KeywordsImportWriteError : Event
        data object KeywordsImportFileTooLarge : Event
    }

}
