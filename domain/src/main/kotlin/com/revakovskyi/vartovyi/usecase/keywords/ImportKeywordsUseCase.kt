package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.model.ImportStrategy
import com.revakovskyi.vartovyi.model.KeywordsBackup
import com.revakovskyi.vartovyi.model.KeywordsDataSnapshot
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

sealed interface ImportResult {
    data class Success(
        val strategy: ImportStrategy,
        val addedCount: Int,
        val skippedCount: Int,
    ) : ImportResult

    data class InvalidFormat(val exception: Exception) : ImportResult
    data object WriteError : ImportResult
    data class UnsupportedVersion(val fileVersion: Int) : ImportResult
}

interface ImportKeywordsUseCase {
    suspend operator fun invoke(jsonContent: String, strategy: ImportStrategy): ImportResult
}

class ImportKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ImportKeywordsUseCase {

    override suspend operator fun invoke(
        jsonContent: String,
        strategy: ImportStrategy,
    ): ImportResult {
        val backup = try {
            Json.decodeFromString(KeywordsBackup.serializer(), jsonContent)
        } catch (e: SerializationException) {
            return ImportResult.InvalidFormat(e)
        } catch (e: IllegalArgumentException) {
            return ImportResult.InvalidFormat(e)
        }

        if (backup.version > KeywordsBackup.CURRENT_VERSION) {
            return ImportResult.UnsupportedVersion(backup.version)
        }

        var addedCount = 0
        var skippedCount = 0

        val written = keywordsRepository.replaceAllKeywordsData { currentData ->
            val baseData = when (strategy) {
                ImportStrategy.REPLACE -> EMPTY_SNAPSHOT
                ImportStrategy.MERGE -> currentData
            }

            val keywordsOutcome = mergeDeduplicated(
                current = baseData.keywords,
                incoming = backup.keywords,
                signatureOf = { value -> parseTriggerKeywordRuleFromStorage(value).normalizedSignature() },
            )
            val stopWordsOutcome = mergeDeduplicated(
                current = baseData.stopWords,
                incoming = backup.stopWords,
                signatureOf = { value -> value.lowercase() },
            )
            val telegramChannelsOutcome = mergeDeduplicated(
                current = baseData.telegramChannels,
                incoming = backup.telegramChannels,
                signatureOf = { value -> value },
            )

            addedCount =
                keywordsOutcome.added + stopWordsOutcome.added + telegramChannelsOutcome.added
            skippedCount =
                keywordsOutcome.skipped + stopWordsOutcome.skipped + telegramChannelsOutcome.skipped

            KeywordsDataSnapshot(
                keywords = keywordsOutcome.merged,
                stopWords = stopWordsOutcome.merged,
                telegramChannels = telegramChannelsOutcome.merged,
                isTelegramChannelFilterEnabled = when (strategy) {
                    ImportStrategy.REPLACE -> backup.isTelegramChannelFilterEnabled
                    ImportStrategy.MERGE ->
                        currentData.isTelegramChannelFilterEnabled || backup.isTelegramChannelFilterEnabled
                },
            )
        }

        return if (written) {
            ImportResult.Success(
                strategy = strategy,
                addedCount = addedCount,
                skippedCount = skippedCount,
            )
        } else {
            ImportResult.WriteError
        }
    }

    private fun mergeDeduplicated(
        current: List<String>,
        incoming: List<String>,
        signatureOf: (value: String) -> String,
    ): MergeOutcome {
        val seenSignatures = current.map(signatureOf).toMutableSet()
        val addedValues = mutableListOf<String>()
        var skipped = 0

        incoming.forEach { value ->
            if (seenSignatures.add(signatureOf(value))) addedValues += value
            else skipped++
        }

        return MergeOutcome(
            merged = current + addedValues,
            added = addedValues.size,
            skipped = skipped,
        )
    }

    private data class MergeOutcome(
        val merged: List<String>,
        val added: Int,
        val skipped: Int,
    )

    private companion object {
        val EMPTY_SNAPSHOT = KeywordsDataSnapshot(
            keywords = emptyList(),
            stopWords = emptyList(),
            telegramChannels = emptyList(),
            isTelegramChannelFilterEnabled = false,
        )
    }

}
