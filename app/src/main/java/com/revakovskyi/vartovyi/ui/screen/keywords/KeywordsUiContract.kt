package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType

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
        data class CopyChip(val text: String) : Action
        data object ExportKeywords : Action
        data object NotifyExportSuccess : Action
        data object NotifyExportError : Action
        data object RequestImport : Action
        data class ImportKeywords(val jsonContent: String) : Action
        data object NotifyImportReadError : Action
    }

    sealed interface Event {
        data object KeywordAdded : Event
        data object KeywordRemoved : Event
        data object StopWordAdded : Event
        data object StopWordRemoved : Event
        data object TelegramChannelAdded : Event
        data object TelegramChannelRemoved : Event
        data object KeywordsScreenDataCleared : Event
        data class ChipCopied(val text: String) : Event
        data class LaunchExportFilePicker(val jsonContent: String) : Event
        data object KeywordsExportSuccess : Event
        data object KeywordsExportError : Event
        data object LaunchImportFilePicker : Event
        data object KeywordsImportSuccess : Event
        data object KeywordsImportInvalidFormat : Event
        data class KeywordsImportUnsupportedVersion(val fileVersion: Int) : Event
        data object KeywordsImportWriteError : Event
    }

}
